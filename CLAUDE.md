# Summit — developer context

Summit is a FLOSS metadata-driven rapid application development tool modelled on
Oracle APEX: web applications (pages, reports, forms, processing) are defined as
rows in database tables, not code. Spring Boot 3.3.x / Java 17 / Maven / Lombok /
MapStruct. PostgreSQL is the primary database (Oracle aspirational).
Base package: `org.awiki.kamikaze.summit`.

Companion docs:
- `doc/ide-plan.md` — plan + progress for the in-progress "IDE" builder app, and
  the full list of metadata-authoring gotchas (READ THIS before writing page SQL).
- `.junie/guidelines.md` — build/test conventions, default template IDs, ID ranges.
- `notes.txt` — historical TODOs. Partially stale (e.g. PAGE_PROCESSING
  conditionals ARE implemented and verified).

## Build / run / verify

```bash
mvn clean package                    # build (tests use H2, do not need postgres)
java -jar target/summit-sb-*.jar     # run; expects postgres summit/summit_dev@localhost:5432/summit
./testing/verify/run-verify.bash     # end-to-end verification suite (needs running app + DB)
```

- DB connection config: `src/main/resources/application.properties` (and `application.yaml`).
- DB bootstrap (fresh database): `src/main/resources/sql/postgres/` — `create.bash`,
  `ddl.sql`, `setup-codetables.sql`, `setup-backend.sql` (templates), then
  `summitdev/*.sql` (IDE app) and optionally `test_dml.sql` / `test_form_dml.sql`.
- All summitdev SQL files are re-runnable (delete-their-own-rows-then-insert).
- The verification harness (`testing/verify/verify-lib.bash`) asserts on rendered
  HTML, the report JSON API, and DB state, and handles the CSRF cookie/token dance
  for POSTs. Every IDE page gets a `t_*.bash` script. Env overrides: SUMMIT_URL,
  SUMMIT_DB_HOST/PORT/USER/NAME.

## Data model (metadata tables)

APPLICATION → APPLICATION_PAGE → PAGE → PAGE_REGION → REGION → REGION_FIELD → FIELD,
with SOURCE (SQL/static text, linked via REGION_SOURCE / FIELD_SOURCE /
PAGE_PROCESSING_SOURCE), TEMPLATE (HTML with `##__VAR__##` substitution variables,
hierarchical via parent_id), LABEL + FIELD_LABEL, PAGE_PROCESSING (+_SOURCE,
+_SOURCE_SELECT, +_CONDITIONAL), CONDITIONAL, FIELD_CONDITIONAL, and CODE_* lookup
tables. Sequences exist for application/page/region/field (+ spare_seq) — NOT yet
for link/child tables (source, application_page, page_region, region_field, ...).

Conventions (`.junie/guidelines.md`): hand-authored metadata IDs live in
-40000..-20000 (IDE app itself is -20000; its pages use blocks: -21000 list,
-21100 app form, -20002 pages report, -20102 page-form stub). Default templates:
page -100, report region -200, form region -103, hidden field -60, text -61,
number -62, dropdown -63, submit button -80, JS button -81, label -1000,
report row-link -50.

## Request flow

- `GET /run/{applicationId}/{pageId}` → `PageRenderController.view()` →
  `PageRenderingServiceImpl.renderPageToString`: maps ApplicationPage→PageDto
  (MapStruct `PageMapper` + CycleAvoidingMappingContext), runs RENDER_PG1 page
  processing (populates fields via `dml_selrow` + PAGE_PROCESSING_SOURCE_SELECT
  column-index→field-name mapping), processes region/field sources, then
  formatters build the HTML. Page parameters arrive as `?pageParams=key:value,key2:v2`
  **and** (since 2026-07-02) as plain query parameters (pageParams wins on
  duplicates) — the latter so post-submit redirects render.
- Report regions render their table CLIENT-side: the page embeds JS that calls
  `GET/POST /api/filter/json/{regionId}` (`PageRestController`) and mustache-renders
  the JSON into `#mustacheReportRegion-<regionId>`. Filtering/pagination are built
  on JSQLParser query rewriting (`service/report/`).
- `POST /run/{applicationId}/{pageId}` → `processPageOnSubmit`: the submitted
  button name (validated against the region's buttons via `__SUMMIT_FORM_ID__`)
  becomes the reserved `:REQUEST` bind; POST1 processings run in processing_num
  order, each gated by PAGE_PROCESSING_CONDITIONAL; then redirect back to the
  same page with the form data as query params. Branching (`processPageBranch`)
  is a stub — you cannot yet redirect elsewhere after a POST.

## Processor / formatter dispatch

- SOURCE_TYPE_CODE → processor (`ProxySourceProcessorServiceImpl`, keyed by
  "responsibilities"): `static` StaticTextProcessor; `dml_select`/`dml_selrow`
  SQLQuerySourceProcessor; `dml_selcel` SqlDMLCellProcessor; `dml_modify`
  SqlDML batch processors; `dml_report` SQLQueryReportRegionSourceProcessor;
  `ddl_exec` DDL processors.
- DTO class canonical name → formatter (`ProxyFormatterService`):
  `GenericFormatterServiceImpl` handles Page/Region/Label/DropDownOption/result
  tables; `FieldFormatterServiceImpl` handles fields. Templates come from each
  row's template_id; template.class_name records which DTO class it is for.
- Conditionals (`ConditionalEvaluatorServiceImpl`): source_type `dml_selcel`
  (single value) or `dml_selrow` (row count). Types: `TEXT_TRUE` (result equals
  'true'), `EXISTS`, `NOTEXISTS`. Field conditionals evaluate with FIELD binds;
  page-processing conditionals evaluate with parameter-map binds (see below).
- Internal replacement variables in templates: `##__DATA__##` (child content),
  `##__REGION-ID__##`, `##__PAGE-ID__##`, `##__ID__##`, `##__NAME__##`,
  `##__CONTEXTPATH__##`, `##__APIPATH__##` (globals from application.yaml),
  `##__CSRF__##` (hidden input via CsrfTokenProvider). See
  `doc/InternalReplacementVariables.txt`.

## Bind variable rules (CRITICAL — wrong choice = runtime 500)

- Report-region sources and FIELD conditional sources get binds from the region's
  FIELDS, typed by field_type_code: NUMBER → BigDecimal/NUMERIC, else VARCHAR;
  unpopulated field → SQL NULL. Compare numeric columns directly: `col = :bind`.
- Page-processing sources (dml_selrow/dml_modify) and PAGE_PROCESSING conditionals
  get binds from the parameter map / submitted form — ALWAYS VARCHAR. Compare
  numeric columns with `CAST(col as VARCHAR) = :bind`; numeric DML values need
  `CAST(:bind as NUMERIC)`.
- Every bind in a render-processing source must be present in the parameter map,
  or rendering fails ("No value supplied for the SQL parameter"). Hence "create"
  links pass `pageParams=id:0` (id 0 never exists → blank form, NOTEXISTS → Save).
- The bind scraper regex (`BindVarServiceImpl`) SKIPS a bind if a later quoted
  literal starts with a non-word char: `NULLIF(:x, '')` hides `:x`;
  `:REQUEST = 'Save'` is fine. Avoid `''` literals after binds.
- `StringUtils.toParameterMap` crashes on an empty value (`id:`); NUMBER fields
  populated with '' throw NumberFormatException when bound. Always pass real values.
- Fields are only visible as binds to sources in the SAME region — duplicate
  hidden fields into every region that needs them.

## Metadata authoring gotchas

- `field_source.flag_default_value = 'Y'` requires the field's
  `default_source_type_code` to be set (usually 'static'), or rendering fails
  with "No service found for sourceType=null".
- CODE_FIELD_TYPE has no BUTTON — buttons use field_type_code 'SUBMIT'; button
  behaviour comes from the template id range -80..-89 (FieldService.BUTTONS_PREDICATE).
- Report row-linking: the report source's first column must be aliased plain
  lowercase `id`; a template -50 field renders `region-<regionId>-sprt-link`
  whose value is the URL prefix the JS appends the row id to.
- Report region fields must render OUTSIDE `#mustacheReportRegion-<id>` (the JS
  replaces that div's contents). Templates -200/-102 render `##__DATA__##` into a
  sibling `regionFields-<id>` div — keep that structure in new report templates.
- Forms MUST contain a submit-type field whose name matches the intended
  :REQUEST action, or the POST is ignored (`determineSubmittedButton`).
- "Found a Form ! FIXME" ERROR log lines during render are pre-existing noise
  (form region *source* processing unimplemented), not failures.

## Security state (placeholder)

Spring Security permits all requests; in-memory user admin/password exists but is
unused. CSRF is real: CookieCsrfTokenRepository(httpOnly=false), token inlined
into forms via `##__CSRF__##` (`summit.csrf-key-name` in application.properties).
Session storage is spring-session-jdbc (needs its tables, see
`sql/postgres/ddl-session-table-*.sql`). POST to /api/filter/json without a
token is rejected 403 (verified) — but report search/pagination still work
because `Summit.Report.doRequest` uses `$.ajax` with the default GET method,
even though the search form says method="post".

## Current work state (2026-07-02, branch feature/test_ai_assist)

CSRF substitution work is merged into this branch's history. The active effort is
the **Summit IDE** (app -20000) — see `doc/ide-plan.md` for the build order.
Done + verified: applications list (-21000, t_020), application create/edit form
(-21100, t_040), pages-in-application report (-20002, t_030; fixed-up
summitdev-20241102.sql), plus t_010 locking in conditional-processing behaviour.
Navigation: list rows → app form (`pageParams=id:<id>`), Create → app form
(`id:0`), form's Pages button → pages report.

**Next: IDE page 4** — make the -20102 "Create New Page" stub a real page
create/edit form. Needs: sequences for link tables (DDL delta), multi-table
insert (preferred: Postgres data-modifying CTE `with new_page as (insert ...
returning id) insert into application_page ...`), template dropdown sourced from
TEMPLATE. Then regions report+form (needs a textarea field template), then
fields report+form. Conditions/validations/processing maintenance is deliberately
deferred. Known deferred items: post-POST branching, delete actions, Oracle
pagination ordering, `dto/edit` + `PageEditController` are an older abandoned
approach (`Old*` services too) — ignore them.
