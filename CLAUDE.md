# Summit — developer context

Summit is a FLOSS metadata-driven rapid application development tool modelled on
Oracle APEX: web applications (pages, reports, forms, processing) are defined as
rows in database tables, not code. Spring Boot 3.3.x / Java 17 / Maven / Lombok /
MapStruct. PostgreSQL is the primary database (Oracle aspirational).
Base package: `org.awiki.kamikaze.summit`.

Companion docs:
- `doc/metadata-authoring-guide.md` — cookbook for building pages/reports/forms
  in metadata SQL (patterns + gotcha checklist; READ THIS before writing page SQL).
- `doc/ide-plan.md` — plan + progress for the in-progress "IDE" builder app, and
  the full list of metadata-authoring gotchas.
- `doc/claude-prompt.txt` — self-contained project context blurb for pasting
  into an LLM that can't read this repo.
- `.junie/guidelines.md` — build/test conventions, default template IDs, ID ranges.
- `notes.txt` — historical TODOs. Partially stale (e.g. PAGE_PROCESSING
  conditionals ARE implemented and verified).

## Build / run / verify

```bash
mvn clean package                    # build; NOTE: tests hit the REAL local postgres (not H2) — apply DDL deltas first or SummitApplicationTests fails
java -jar target/summit-sb-*.jar     # run; expects postgres summit/summit_dev@localhost:5432/summit
./testing/verify/run-verify.bash     # end-to-end verification suite (needs running app + DB)
```

- DB connection config: `src/main/resources/application.properties` (and `application.yaml`).
- DB bootstrap (fresh database): `src/main/resources/sql/postgres/` — `create.bash`,
  `ddl.sql`, `setup-codetables.sql`, `setup-backend.sql` (templates), then
  `summitdev/*.sql` (IDE app) and optionally `test_dml.sql` / `test_form_dml.sql`.
- All summitdev SQL files are re-runnable (delete-their-own-rows-then-insert).
- Reload a metadata file into the running DB: `PGPASSWORD=summit_dev psql -h localhost -U summit -d summit -v ON_ERROR_STOP=1 -q -f <file.sql>`.
- Restart dance: `pkill -f "summit-sb-.*jar"` (exit 144 is normal), wait for
  8080 to free, then start the new jar — if the old JVM still holds 8080 the
  new one dies at startup and requests silently hit the OLD jar.
- The verification harness (`testing/verify/verify-lib.bash`) asserts on rendered
  HTML, the report JSON API, and DB state, and handles the CSRF cookie/token dance
  for POSTs. Every IDE page gets a `t_*.bash` script. Env overrides: SUMMIT_URL,
  SUMMIT_DB_HOST/PORT/USER/NAME.
- Verify-script gotcha: Spring consumes only ONE FlashMap per request, and flash
  maps match on target path AND query params — GET each POST's redirect target
  before the next POST, or a stale same-target flash shadows the next one (bit
  t_040; flashes for different `?id=` targets don't collide, see t_070).

## Data model (metadata tables)

APPLICATION → APPLICATION_PAGE → PAGE → PAGE_REGION → REGION → REGION_FIELD → FIELD,
with SOURCE (SQL/static text, linked via REGION_SOURCE / FIELD_SOURCE /
PAGE_PROCESSING_SOURCE), TEMPLATE (HTML with `##__VAR__##` substitution variables,
hierarchical via parent_id), LABEL + FIELD_LABEL, PAGE_PROCESSING (+_SOURCE,
+_SOURCE_SELECT, +_CONDITIONAL, and a SUCCESS_MESSAGE column since 2026-07-11),
CONDITIONAL, FIELD_CONDITIONAL, VALIDATION + VALIDATION_CONDITIONAL (since
2026-07-11), and CODE_* lookup tables. Sequences exist for
application/page/region/field (+ spare_seq), for the link/child tables (source,
application_page, page_region, region_field, ... — added 2026-07-02), and for
the validation tables.

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
- `POST /run/{applicationId}/{pageId}` → `processPageOnSubmit` (returns a
  `PageSubmitResult`): the submitted button name (validated against the
  region's buttons via `__SUMMIT_FORM_ID__`) becomes the reserved `:REQUEST`
  bind; page VALIDATIONs run next (since 2026-07-11, each gated by
  VALIDATION_CONDITIONAL; NOT_NULL checks the submitted param, source-backed
  types go through ConditionalEvaluatorService) — any failure skips all
  processing and the controller re-renders the page inline (HTTP 200) with the
  submitted values preserved and the errors in the page template's
  `##__NOTIFICATION__##` div; otherwise POST1 processings run in
  processing_num order, each gated by PAGE_PROCESSING_CONDITIONAL, and each
  passing processing's SUCCESS_MESSAGE is collected and flashed across the
  redirect (flash attribute `summitSuccessMessages`, rendered into
  `##__NOTIFICATION__##` by the next GET, consumed once); values a POST1
  processing selects into PAGE_PROCESSING_SOURCE_SELECT rows are written back
  into the parameter map replacing same-named submitted params (since
  2026-07-12, APEX "returning into item" — a dml_selcel Save CTE ending in
  `select id from <insert-cte>` feeds the generated id into :id); then
  redirect back to the same page with the form data as query params — unless
  a BRANCH1 processing fires (since 2026-07-11): branches evaluate after
  POST1 in processing_num order, gated by PAGE_PROCESSING_CONDITIONAL, first
  passing branch wins; a 'static' source is a URL template with URL-encoded
  :param substitution from the (post-write-back) submitted form, a
  'dml_selcel' source is a query returning the target URL. The IDE forms
  branch to the new row's edit page (`?id=:id`) on Save, and back to their
  report page on Update/Delete.

## Processor / formatter dispatch

- SOURCE_TYPE_CODE → processor (`ProxySourceProcessorServiceImpl`, keyed by
  "responsibilities"): `static` StaticTextProcessor; `dml_select`/`dml_selrow`
  SQLQuerySourceProcessor; `dml_selcel` SqlDMLCellProcessor; `dml_modify`
  SqlDML batch processors; `dml_report` SQLQueryReportRegionSourceProcessor;
  `ddl_exec` DDL processors.
- `dml_modify` executes via `jdbc.update()` and CANNOT return values; to capture
  RETURNING output (e.g. a generated id), run the data-modifying CTE as
  `dml_selcel` with a final `SELECT` — Postgres executes every data-modifying
  CTE exactly once even if the final SELECT doesn't reference it.
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
(-21100, t_040), pages-in-application report (-20002, t_030), page create/edit
form (-20102, t_050 — Save INSERTs PAGE + APPLICATION_PAGE via one Postgres
data-modifying CTE), regions-on-page report (-23000) +
region create/edit form (-23100, t_060 — 4-table Save CTE: REGION + PAGE_REGION
+ SOURCE + REGION_SOURCE; SQL source edited in new textarea template -64),
fields-on-region report (-24000) + field create/edit form (-24100, t_070 —
optional default source via a 'none' sentinel dropdown option +
`NULLIF(:bind, 'none')`, conditional SOURCE/FIELD_SOURCE insert inside the
CTE), plus t_010 locking in conditional-processing behaviour. Navigation:
applications list rows → app form (`pageParams=id:<id>`), Create → app form
(`id:0`), app form's Pages button → pages report, pages report rows → page
form (`id:<pageId>`; child forms derive parent ids from the row id), page
form's Regions button → regions report → region form, region form's Fields
button → fields report → field form. The full APPLICATION → PAGE → REGION →
FIELD chain is maintainable from the IDE.

Enabling changes landed with page 4: link/child-table sequences
(`ddl-link-table-sequences-20260702.sql`, mirrored in ddl.sql) and a
`FieldMapper` @ObjectFactory so DROPDOWN fields map to `DropDownFieldDto` —
dropdowns had never worked on the render path before this (dispatch is by DTO
class; plain FieldDto fell into SimpleFieldProcessor which can't run
`dml_select`). Dropdown recipe + CTE/bind-scraper notes: `doc/ide-plan.md`.

**2026-07-11**: post-POST branching implemented (BRANCH1, see Request flow
above). Delete actions added to all four forms — bottom-up only
(Delete button hidden while children exist via field conditionals; field
deletes cascade the field's own child rows in one CTE). Validations +
success/error messages implemented, APEX-3.2 style (see Request flow above and
the CLOSED entry in `doc/ide-plan.md`): VALIDATION / VALIDATION_CONDITIONAL /
CODE_VALIDATION_TYPE tables + PAGE_PROCESSING.SUCCESS_MESSAGE
(`ddl-validations-20260711.sql`, mirrored in ddl.sql/setup-codetables.sql),
`ValidationService`, `PageSubmitResult`, `##__NOTIFICATION__##` in page
template -100 (summit-error/summit-success styles in main.css), NOT_NULL
validations gated to Save/Update + success messages on all four IDE forms,
and a `BindVarMapper` fix so a blank NUMBER field binds as SQL NULL instead
of crashing. All verified (t_040–t_070; suite green).

**2026-07-12**: generated-id write-back implemented (APEX "returning into
item", see Request flow above and the CLOSED entry in `doc/ide-plan.md`):
POST1 source_select results replace same-named parameter-map entries before
branches run; the four IDE Save sources became dml_selcel data-modifying CTEs
ending in `select id from <insert-cte>` with a source_select row mapping the
id onto :id, and each form gained a Save-gated branch to its own page with
`?id=:id` (the old report-page branch is re-gated to Update/Delete). Save now
lands on the new row's edit page with the 'created' flash. All verified
(t_040–t_070; suite green). Next agreed scope: none yet — candidates are the
deferred items: JS confirm on Delete (template -80 has no onclick hook), IDE
maintenance pages for processing/branch/validation/conditional metadata,
Oracle pagination ordering; `dto/edit` + `PageEditController` are an older
abandoned approach (`Old*` services too) — ignore them.
