# Summit IDE — rough build plan

Goal: an internal Summit-built application (like Oracle APEX app 4550) for creating and
editing Summit applications through the web UI instead of hand-written SQL inserts.
Scope for the first pass is deliberately small: maintain APPLICATION / PAGE / REGION / FIELD
only. Conditions, validations and processing maintenance come later, page by page.

## Existing groundwork

- Application **-20000 "Summit - IDE"** already exists (`src/main/resources/sql/postgres/summitdev/summitdev-20241102.sql`)
  with page -20002 (Pages-in-Application report, driven by `:applicationId`) and an empty
  "Create New Page" stub (-20102). Build on this.
- The CRUD form pattern is proven in `src/main/resources/sql/postgres/test_form_dml.sql`:
  - GET: `PAGE_PROCESSING` (`RENDER_PG1`) + `dml_selrow` source + `PAGE_PROCESSING_SOURCE_SELECT`
    maps result columns to page fields by index.
  - POST: `dml_modify` sources, with `:REQUEST = 'Save'/'Update'` conditionals selecting
    which processing runs, and EXISTS/NOTEXISTS conditionals toggling which button shows.
- Sequences exist for `application`, `page`, `region`, `field` (+ `spare_seq`), so runtime
  ID generation is available from plain SQL.
- Conventions (.junie/guidelines.md): new metadata IDs in **-40000..-20000**; default
  template IDs (page -100, report region -200, hidden -60, text -61, number -62,
  dropdown -63, submit -80, JS button -81, label -1000, report td href link -50).

## Verification

Every page gets a `t_*.bash` script under `testing/verify/` (run via
`testing/verify/run-verify.bash` against a running app + database). The scripts
assert on rendered HTML, the report JSON API (`/api/filter/json/<regionId>`),
and database state, and handle the CSRF cookie/token dance for POSTs.
`t_010` locks in the PAGE_PROCESSING_CONDITIONAL behaviour end-to-end.

## Pages, in build order

1. **Applications list** — DONE (2026-07-02): `summitdev/summitdev-20260702-ide-p1-applications.sql`,
   page -21000, verified by `testing/verify/t_020_ide_applications_list.bash`.
   Row links and Create button target the application form page, reserved as **-21100**.
   Report over `select id, application_num, name from application`;
   row link (template -50 pattern) to the application form; Create button (template -81)
   linking to the form with no id. Needed no new functionality.
2. **Application create/edit form** — DONE (2026-07-02):
   `summitdev/summitdev-20260702-ide-p2-application-form.sql`, page -21100,
   verified by `testing/verify/t_040_ide_application_form.bash`.
   Create mode is entered with `pageParams=id:0`. Save/Update/Pages buttons are
   gated by EXISTS/NOTEXISTS field conditionals; INSERT ids come from
   `application_seq` (the script setvals it past existing rows).
   Required one small Java change: `PageRenderController.view()` now merges
   plain query parameters into the page parameter map, because the post-submit
   redirect carries form data as plain query params and render processing
   failed with unbound parameters otherwise.
3. **Pages-in-application report** — DONE (2026-07-02): `summitdev-20241102.sql`
   fixed up (BUTTON→SUBMIT FK violation, missing default_source_type_code,
   pageParams URL format, made re-runnable) and verified by
   `testing/verify/t_030_ide_pages_report.bash`. Reached from the application
   form's "Pages" button; its Create button targets the page-form stub -20102.
4. **Page create/edit form** — name, page_num, template dropdown (from TEMPLATE).
   First multi-table create (PAGE + APPLICATION_PAGE) — see gap list.
5. **Regions-on-page report + region create/edit form** — name, region_num,
   position/type/source-type dropdowns (from CODE_* tables), template dropdown,
   SQL source (needs a textarea field template).
6. **Fields-on-region report + field create/edit form** — name, field_num,
   field-type/source-type/template dropdowns, optional default source.

Stop and reassess after page 6 before adding conditions/validations/processing maintenance.

## Gaps to close as pages need them

- **Sequences for child/link tables** (needed by page 4): none exist for `source`,
  `application_page`, `page_region`, `region_field`, `region_source`, `field_source`,
  `label` etc. Add a small DDL delta (or use `spare_seq` to start).
- ~~**VARCHAR binds**~~ understood and documented — see bind variable rules below.
- ~~**Verify PAGE_PROCESSING conditionals**~~ VERIFIED WORKING (2026-07-02, see
  `testing/verify/t_010_page_processing_conditional.bash`) — notes.txt is out of date.
- **Multi-table insert strategy** (page 4): either multiple PAGE_PROCESSING steps sharing
  a conditional with `currval('page_seq')` in the later statements, or a single Postgres
  data-modifying CTE (`with new_page as (insert ... returning id) insert into application_page ...`).
  CTE is simpler; Postgres-only is acceptable for now.
- **Textarea field template** (page 5): new TEMPLATE row for multi-line SQL entry —
  data only, no Java expected.
- **Post-POST branching with parameters** (later): `processPageOnSubmit` branch target
  is TODO ("need attributes included") and `processPageBranch` is a commented-out stub.
  Mitigated 2026-07-02: `view()` accepts plain query params as page params, so the
  default redirect-back-to-self after a POST now renders instead of 500ing.
  Real branch support (e.g. land on the new row's edit page) is still future work.
- **Delete actions** (later): explicitly out of scope for the first pass.

## Bind variable rules (which type binds where)

Learned 2026-07-02 while building the application form:

- **Report region sources**: binds come from the region's FIELDS
  (`BindVarServiceImpl.createBindVarsFromFields` + `BindVarMapper`) and are typed
  by `field_type_code`: NUMBER → `BigDecimal`/NUMERIC, others → VARCHAR, an
  unpopulated field → SQL NULL. Compare numeric columns directly (`col = :bind`).
- **Field conditional sources**: same field-typed binds. `id = :id` works for both
  populated and blank (NULL matches nothing → NOTEXISTS true on create).
- **Page processing sources** (`dml_selrow`, `dml_modify`) **and their
  conditionals**: binds come from the parameter map / submitted form and are
  ALWAYS VARCHAR (`createVarcharBindVarsFromParameterMap`). Compare numeric
  columns with `CAST(col as VARCHAR) = :bind`; numeric values in DML need
  `CAST(:bind as NUMERIC)`.
- A NUMBER field populated with a non-numeric or empty string will throw
  NumberFormatException when used as a bind (`new BigDecimal(...)`) — hence the
  `pageParams=id:0` create-mode convention rather than a blank id.
- The bind scraper regex skips a bind if a later quoted literal starts with a
  non-word character (e.g. `NULLIF(:x, '')` — the `''` hides `:x`). `:REQUEST = 'Save'`
  is fine ('S is a word char). Avoid `''` literals after binds.
- `StringUtils.toParameterMap` crashes on a key with an empty value (`id:`) —
  always pass a real value in pageParams.
- Every bind in a render-processing source MUST be present in the parameter map
  or rendering fails with "No value supplied for the SQL parameter" — another
  reason for the `id:0` convention on create links.

## Gotchas learned while building (metadata authoring rules)

- If a `field_source` row has `flag_default_value = 'Y'`, the field MUST have
  `default_source_type_code` set (usually `'static'`), or rendering fails with
  "No service found for sourceType=null" (SimpleFieldProcessorServiceImpl).
- There is no `BUTTON` code in CODE_FIELD_TYPE — use `'SUBMIT'` for all buttons.
  Button behaviour is actually driven by the template id range -80..-89
  (FieldService.BUTTONS_PREDICATE), not the field type code.
- Report row-linking requires the report source's link column to be aliased
  plain lowercase `id` — summit-report.js matches the header cell value exactly
  and appends the id to the sprt-link URL prefix.
- Report region fields must NOT render inside `mustacheReportRegion-<id>`:
  summit-report.js `buildHtmlTable` replaces that div's entire contents with the
  AJAX table, clobbering any server-rendered fields (buttons vanished on load).
  Fixed 2026-07-02 in templates -200/-102 (setup-backend.sql + DB): `##__DATA__##`
  now renders in a sibling `regionFields-<id>` div and the JS-owned div starts empty.
- The local database may be stale vs summitdev-20241102.sql: the Create-button
  and page -20102 rows from that file are NOT loaded (and the file itself has
  both gotchas above: `'BUTTON'` FK violation and missing default_source_type_code),
  so it needs fixing before it can be applied.

## Standing limitations to design around (not fix)

- Hidden bind-variable fields must be duplicated into every region whose source uses them.
- Page parameters are only usable if they populate a field on the page.
- Bind variable scraping is a naive `:word` regex — avoid literal colons in IDE SQL sources.
