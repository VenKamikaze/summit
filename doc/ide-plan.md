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
4. **Page create/edit form** — DONE (2026-07-02):
   `summitdev/summitdev-20260702-ide-p4-page-form.sql` (page -20102, the old
   stub, now owned by this file; new rows in -22000..-22099), verified by
   `testing/verify/t_050_ide_page_form.bash`. Name, page_num, template
   dropdown from TEMPLATE (class PageDto). Save INSERTs PAGE +
   APPLICATION_PAGE in a single dml_modify source via a Postgres
   data-modifying CTE; Update updates both tables the same way. Ids come from
   page_seq + the new application_page_seq. The pages report (-20002) gained
   a row link (edit mode, passes id: only — the form derives applicationId
   from the page id) and its Create button now passes id:0 as well.
   Required two enabling changes:
   - DDL delta `sql/postgres/ddl-link-table-sequences-20260702.sql`
     (sequences for the link/child tables; also added to ddl.sql).
   - `FieldMapper` @ObjectFactory: DROPDOWN fields now map to DropDownFieldDto
     (dropdowns had NEVER worked on the render path — see gotchas).
5. **Regions-on-page report + region create/edit form** — DONE (2026-07-02):
   `summitdev/summitdev-20260702-ide-p5-regions.sql` (report page -23000,
   form page -23100), verified by `testing/verify/t_060_ide_regions.bash`
   (including that an IDE-created report region renders on its page and
   serves rows through the JSON API). Name, region_num, template dropdown
   (explicit id list -200/-103 — region templates share no class_name),
   position/type/source-type dropdowns from CODE_* tables, SQL source in the
   new textarea template -64 (data only, added to setup-backend.sql and
   insert-if-missing in the p5 file). Save creates REGION + PAGE_REGION +
   SOURCE + REGION_SOURCE in one 4-table data-modifying CTE; Update updates
   REGION, PAGE_REGION.region_num and the linked SOURCE in one CTE. Reached
   from the page form's new Regions button (edit mode only). First-pass
   limitations documented in the SQL file header (source row always created;
   update only reaches a source linked via REGION_SOURCE; type/source-type
   combinations unvalidated).
6. **Fields-on-region report + field create/edit form** — DONE (2026-07-02):
   `summitdev/summitdev-20260702-ide-p6-fields.sql` (report page -24000, form
   page -24100), verified by `testing/verify/t_070_ide_fields.bash` (including
   that an IDE-created field renders on its host page with its static default
   value). Name, field_num, template dropdown (field templates = the two
   render field DTO class_names), field-type/source-type dropdowns from CODE_*
   tables, optional default source type + default value source textarea.
   The optional default source type uses a **'none' sentinel option** +
   `NULLIF(:bind, 'none')` in the DML ('' would hide the bind from the
   scraper) and `coalesce(col, 'none')` on render. Save inserts FIELD +
   REGION_FIELD always, and SOURCE + FIELD_SOURCE (flag 'Y') only when default
   source text was entered (`where length(:default_source) > 0` inside the
   CTE). Reached from the region form's new Fields button (edit mode only).
   Limitations documented in the SQL file header (default text requires a
   default type or the field won't render; update won't create a missing
   default source; flag-'N' field_source rows not maintained).

Page 6 reached (2026-07-02) — **stop and reassess** before adding
conditions/validations/processing maintenance, per the note below. The full
APPLICATION → PAGE → REGION → FIELD chain is now maintainable from the IDE.

## Gaps to close as pages need them

- ~~**Sequences for child/link tables**~~ CLOSED (2026-07-02):
  `ddl-link-table-sequences-20260702.sql` adds `application_page_seq`,
  `source_seq`, `page_region_seq`, `region_field_seq`, `region_source_seq`,
  `field_source_seq`, `label_seq`, `field_label_seq` (and setvals them past
  existing rows); ddl.sql has the same creates for fresh databases.
- ~~**VARCHAR binds**~~ understood and documented — see bind variable rules below.
- ~~**Verify PAGE_PROCESSING conditionals**~~ VERIFIED WORKING (2026-07-02, see
  `testing/verify/t_010_page_processing_conditional.bash`) — notes.txt is out of date.
- ~~**Multi-table insert strategy**~~ CLOSED (2026-07-02): the Postgres
  data-modifying CTE works as a single dml_modify source, verified end-to-end
  by t_050. The bind scraper is a plain `:word` regex (no SQL parsing), so the
  CTE syntax is invisible to it, and `NamedParameterJdbcTemplate.update()` runs
  `WITH ... INSERT` fine. The quoted-literal trap still applies inside CTEs:
  keep any literal that follows a bind starting with a word character
  (sequence names like 'application_page_seq' are safe).
- ~~**Textarea field template**~~ CLOSED (2026-07-02): TEMPLATE -64
  'Input Item - TextArea' (class FieldDto, data only) in setup-backend.sql,
  insert-if-missing in the p5 summitdev file for existing databases.
- ~~**Post-POST branching with parameters**~~ CLOSED (2026-07-11): BRANCH1
  page processings are now implemented (`PageProcessingServiceImpl.processBranchSource`).
  A branch is a PAGE_PROCESSING row of type BRANCH1 (+ PAGE_PROCESSING_SOURCE,
  gated by PAGE_PROCESSING_CONDITIONAL, typically on :REQUEST); branches evaluate
  in processing_num order after all POST1 processing and the FIRST one whose
  conditional passes wins (APEX semantics). Source types: 'static' = a
  context-relative URL template whose :name variables substitute (URL-encoded)
  from the submitted form params — a missing param substitutes empty with a
  warning; 'dml_selcel' = a query (VARCHAR binds) returning the target URL.
  The four IDE forms now branch back to their report page after Save/Update
  (asserted in t_040/t_050/t_060/t_070). CODE_PROCESSING_TYPE 'BRANCH1' is in
  setup-codetables.sql and insert-if-missing in each form's summitdev file.
  Landing on a NEW row's edit page is still future work (needs the INSERT's
  generated id fed back into the parameter map, APEX "returning into item").
- ~~**Delete actions**~~ CLOSED (2026-07-11): every IDE form has a Delete button
  (template -80, :REQUEST = 'Delete' POST1 dml_modify, branch back to the report).
  Deletes are **bottom-up** (no cascade): the button is gated by a field
  conditional so it only shows when the row has no children — app with no
  pages, page with no regions AND no page_processings, region with no fields;
  fields are the leaf so their delete CTE cascades the field's own child rows
  (FIELD_SOURCE + SOURCE, FIELD_LABEL + LABEL, FIELD_CONDITIONAL, REGION_FIELD).
  Caveats: the server does NOT re-check the visibility conditional on POST (a
  hand-crafted Delete POST against a row with children FK-500s), there is no
  JS confirm dialog yet (template -80 has no onclick hook — an APEX-style
  confirm needs a new button template), and orphaned CONDITIONAL/SOURCE rows
  from deleted field_conditionals are left behind (harmless).
- ~~**Validations + success/error messages**~~ CLOSED (2026-07-11), APEX-3.2
  style — dedicated validation metadata, inline error re-render, flashed
  success messages:
  - Schema (`ddl-validations-20260711.sql` delta; mirrored in ddl.sql and
    setup-codetables.sql for fresh databases): VALIDATION (page_id, name,
    validation_num, validation_type_code, field_name, error_message, optional
    source_id/source_type_code), VALIDATION_CONDITIONAL, CODE_VALIDATION_TYPE
    (NOT_NULL / TEXT_TRUE / EXISTS / NOTEXISTS), a SUCCESS_MESSAGE column on
    PAGE_PROCESSING, and validation_seq / validation_conditional_seq.
  - Runtime: on POST, validations run after the submitted button is determined
    and BEFORE any POST1 processing (`ValidationServiceImpl` — NOT_NULL checks
    the submitted param; the source-backed types delegate to
    ConditionalEvaluatorService with VARCHAR parameter-map binds). Each
    validation is gated by VALIDATION_CONDITIONAL (the IDE ones fire only on
    :REQUEST = 'Save'/'Update'). Any failure aborts processing and the
    controller re-renders the page as HTTP 200 with the submitted form as the
    parameter map — submitted values win over RENDER_PG1 DB values, so the
    user's input is preserved. Errors render (HTML-escaped, class
    `summit-error`) into the page template's new `##__NOTIFICATION__##`
    placeholder; template -100 gained a
    `<div class="notification" id="summit-notification">` for it.
  - Success messages: each POST1 processing whose conditional passed
    contributes its SUCCESS_MESSAGE; the controller flashes them across the
    post-submit redirect (flash attribute `summitSuccessMessages`, consumed by
    the next GET, rendered class `summit-success`). TEST GOTCHA: Spring
    consumes only ONE matching FlashMap per request — a script that POSTs
    twice without GETting the redirect target in between will see the FIRST
    (stale) flash on its next GET (burned in t_040's delete assertion).
  - Enabler: `BindVarMapper` now binds a NUMBER field populated with a blank
    value as SQL NULL instead of crashing in `new BigDecimal("")` — required
    for the inline re-render of a failed submit.
  - IDE metadata: NOT_NULL validations on all four forms (p2 application_num,
    p4 page_num, p5 region_num, p6 name + field_num) and success messages
    ('X created./updated./deleted.') on every POST1 processing; asserted in
    t_040–t_070.

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
  is fine ('S is a word char). Avoid `''` literals after binds. Literals
  BEFORE all binds are harmless: `coalesce(s.source, '') ... where id = :id`
  works (used by the region form's render query). When unsure, test the SQL
  against the regex before loading it.
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
- **Dropdown fields never worked on the render path** until 2026-07-02: field
  processors/formatters dispatch on the DTO class canonical name, but
  `FieldMapper` always produced a plain `FieldDto`, so DROPDOWN fields hit
  `SimpleFieldProcessorServiceImpl`, which asks for a *singular* processor for
  `dml_select` and dies with "No service found for sourceType=dml_select"
  (page -10001 of the old test app 500s for exactly this reason on a stock
  build). Fixed with an @ObjectFactory on FieldMapper keyed on
  field_type_code = 'DROPDOWN'. The working dropdown recipe: field template
  -63, source_type_code 'dml_select', field_type_code 'DROPDOWN', field_source
  flag_default_value 'N', source returning exactly two columns (key, display
  value). The selected option follows the RENDER_PG1 source_select value (or a
  page parameter) matching the option key by string equality; dropdown option
  sources get NO bind variables (runtime passes null), so they must be
  bind-free SQL.
- When one summitdev file's page links to another file's page (pages report ->
  page form), give the *target* page's PAGE/APPLICATION_PAGE rows to the file
  that builds the real page, and only ever reference the id from the linking
  file — that keeps every file independently re-runnable (FK deletes don't
  cross files). Done for -20102 (owned by the p4 file, referenced by
  summitdev-20241102.sql).

## Standing limitations to design around (not fix)

- Hidden bind-variable fields must be duplicated into every region whose source uses them.
- Page parameters are only usable if they populate a field on the page.
- Bind variable scraping is a naive `:word` regex — avoid literal colons in IDE SQL sources.
