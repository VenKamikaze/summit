# Summit metadata authoring guide

How to build application pages and components as Summit metadata SQL. This is
the practical cookbook; architecture and Java internals live in `CLAUDE.md`,
and the history of how these patterns were arrived at is in `doc/ide-plan.md`.
The canonical worked examples are the Summit IDE files in
`src/main/resources/sql/postgres/summitdev/summitdev-20260702-ide-p*.sql` —
copy from those, they encode every rule below.

## 1. The mental model

Everything a user sees at `/run/{applicationId}/{pageId}` is rows in these
tables:

```
APPLICATION
  └─ APPLICATION_PAGE (page_num)
       └─ PAGE (template_id)
            ├─ PAGE_REGION (region_num)
            │    └─ REGION (template_id, code_region_type, source_type_code)
            │         ├─ REGION_SOURCE ─ SOURCE          (report SQL / static text)
            │         └─ REGION_FIELD (field_num)
            │              └─ FIELD (template_id, field_type_code, source_type_code)
            │                   ├─ FIELD_SOURCE ─ SOURCE (default value / JS / dropdown SQL)
            │                   ├─ FIELD_LABEL ─ LABEL
            │                   └─ FIELD_CONDITIONAL ─ CONDITIONAL (show/hide)
            ├─ PAGE_PROCESSING (RENDER_PG1 | POST1 | BRANCH1, processing_num)
            │    ├─ PAGE_PROCESSING_SOURCE ─ SOURCE (source_type_code)
            │    ├─ PAGE_PROCESSING_SOURCE_SELECT (field_index → field_name)
            │    └─ PAGE_PROCESSING_CONDITIONAL ─ CONDITIONAL (gate)
            └─ VALIDATION (validation_num, validation_type_code, field_name, error_message)
                 └─ VALIDATION_CONDITIONAL ─ CONDITIONAL (gate)
```

TEMPLATEs are HTML fragments with `##__VAR__##` substitution variables;
each PAGE/REGION/FIELD/LABEL row points at the template that renders it.
CONDITIONALs wrap a SOURCE (a SQL query) with a type: `TEXT_TRUE` (single
value equals the string `true`), `EXISTS` / `NOTEXISTS` (row count).

A **report page** is a region with `source_type_code = 'dml_report'` whose
SOURCE is a SELECT; the table renders client-side via `/api/filter/json/{regionId}`.
A **form page** is a static region full of FIELDs, a `RENDER_PG1` processing
that populates them on GET, `POST1` processings that run DML on submit, and
`BRANCH1` processings that decide where to redirect afterwards.

## 2. Ground rules (before writing any SQL)

**ID conventions** (`.junie/guidelines.md`): hand-authored metadata IDs are
negative, in the range **-40000..-20000**. Give each page its own block
(e.g. the IDE uses -21000..-21099 for the applications list, -21100..-21199
for the application form) and use the same block across all tables. Runtime
row creation uses the sequences (`application_seq`, `page_seq`, `region_seq`,
`field_seq`, `spare_seq`, plus per-link-table sequences).

**Default template IDs** (from `setup-backend.sql`):

| id    | template                                   |
|-------|--------------------------------------------|
| -100  | standard page                              |
| -200  | report region (interactive, AJAX table)    |
| -103  | form region                                |
| -50   | report row-link field                      |
| -60   | hidden field                               |
| -61   | text input                                 |
| -62   | number input                               |
| -63   | dropdown (select)                          |
| -64   | textarea (e.g. SQL source entry)           |
| -80   | submit button                              |
| -81   | javascript button (onclick from source)    |
| -1000 | field label                                |

**One re-runnable file per page.** Every summitdev file starts with a
transaction, deletes its own rows (children first — reverse FK order), then
inserts. When your page links to a page built by another file, that other file
owns the target's PAGE/APPLICATION_PAGE rows; only reference the id. This keeps
every file independently loadable:

```bash
PGPASSWORD=summit_dev psql -h localhost -U summit -d summit -v ON_ERROR_STOP=1 -q -f <file.sql>
```

**Bind variable rules — read this twice.** Which binds a SOURCE receives, and
their SQL types, depend on where the source is attached:

| Source attached to…                                      | Binds come from…                | Types                                     |
|----------------------------------------------------------|---------------------------------|-------------------------------------------|
| report region source, FIELD conditional                  | the region's FIELDS             | by field_type_code: NUMBER → NUMERIC, else VARCHAR; unpopulated → NULL |
| page-processing source, PAGE_PROCESSING / VALIDATION conditional | parameter map / submitted form  | **always VARCHAR**                         |

Consequences:

- In page-processing SQL, compare numeric columns as
  `CAST(col as VARCHAR) = :bind` and cast numeric DML values as
  `CAST(:bind as NUMERIC)`. In field-bound SQL, compare directly: `col = :id`.
- Every bind in a `RENDER_PG1` source must be present in the parameter map or
  the GET 500s ("No value supplied for the SQL parameter"). Hence the create
  convention: links to a blank form pass `pageParams=id:0` (id 0 never exists).
  Never pass an empty value (`id:` crashes `toParameterMap`; a NUMBER field
  holding `''` crashes binding).
- The bind scraper is a naive `:word` regex that **skips a bind if a later
  quoted literal starts with a non-word character**: `NULLIF(:x, '')` hides
  `:x`; `:REQUEST = 'Save'` is fine. Avoid `''` literals after binds; literals
  before all binds are harmless.
- Fields are only visible as binds to sources in the **same region** —
  duplicate hidden fields into every region that needs them.
- `:REQUEST` is reserved: on POST it holds the submitted button's name.

## 3. Skeleton: application and page

```sql
-- Once per app:
insert into application (id, application_num, name) values (-25000, 250, 'My App');

-- Per page: PAGE + the APPLICATION_PAGE link.
insert into page (id, template_id, "name") values (-25001, -100, 'My Page');
insert into application_page (id, application_id, page_id, page_num)
values (-25001, -25000, -25001, 1);
```

The page now renders (empty) at `/run/-25000/-25001`.

## 4. Report pages

A report needs: a region typed `dml_report`, a SELECT source, and usually a
row-link field and a Create button. Full example:
`summitdev-20260702-ide-p1-applications.sql`.

```sql
insert into region (id, template_id, "name", code_region_position, code_region_type, source_type_code)
values (-25010, -200, 'Widgets', 'body1', 'Report', 'dml_report');
insert into page_region (id, page_id, region_id, region_num) values (-25010, -25001, -25010, 1);

-- First column MUST be aliased plain lowercase 'id' if you want row links:
-- summit-report.js matches the header cell named 'id' and appends its value
-- to the row-link URL prefix. Other column aliases become the table headers.
insert into source (id, "source")
values (-25010, 'select id, name as "Widget Name" from widget order by name');
insert into region_source (id, region_id, source_id) values (-25010, -25010, -25010);
```

Rendering is client-side: the page embeds JS that fetches
`/api/filter/json/{regionId}` and mustache-renders the rows into
`#mustacheReportRegion-<regionId>`. Search and pagination come for free.
If the source has binds, they resolve from the region's own fields (typed —
see §2), so a parameterised report needs a hidden field in the same region
populated from a page param.

**Row link** — a template -50 field whose static default is the URL prefix;
the JS appends the clicked row's `id`:

```sql
insert into field (id, template_id, "name", source_type_code, field_type_code, default_source_type_code)
values (-25011, -50, 'widgets-sprt-link', 'static', 'TEXT', 'static');
insert into region_field (id, region_id, field_id, field_num) values (-25011, -25010, -25011, 1);
insert into source (id, "source") values (-25011, '##__CONTEXTPATH__##/run/-25000/-25002?pageParams=id:');
insert into field_source (id, field_id, source_id, flag_default_value) values (-25011, -25011, -25011, 'Y');
```

**Create button** — a javascript button (template -81) navigating to the form
in create mode (`id:0`):

```sql
insert into field (id, template_id, "name", source_type_code, field_type_code, default_source_type_code)
values (-25012, -81, 'Create', 'static', 'SUBMIT', 'static');
insert into region_field (id, region_id, field_id, field_num) values (-25012, -25010, -25012, 2);
insert into source (id, "source")
values (-25012, 'location.href=''##__CONTEXTPATH__##/run/-25000/-25002?pageParams=id:0'';');
insert into field_source (id, field_id, source_id, flag_default_value) values (-25012, -25012, -25012, 'Y');
```

Notes:
- There is no `BUTTON` in CODE_FIELD_TYPE — all buttons are `field_type_code
  'SUBMIT'`; *button behaviour* comes from the template id being in -80..-89
  (`FieldService.BUTTONS_PREDICATE`).
- Any field with `field_source.flag_default_value = 'Y'` **must** set
  `field.default_source_type_code` (usually `'static'`) or rendering fails with
  "No service found for sourceType=null".
- Region fields render in the `regionFields-<id>` div, a *sibling* of the
  JS-owned `mustacheReportRegion-<id>` div. Keep that structure in any custom
  report template, or the AJAX table load clobbers your buttons.

## 5. Form pages

The complete pattern — every piece below in one file — is
`summitdev-20260702-ide-p2-application-form.sql` (single-table) and
`summitdev-20260702-ide-p5-regions.sql` (multi-table Save CTE, dropdowns,
textarea). A form page has one static region:

```sql
insert into region (id, template_id, "name", code_region_position, code_region_type, source_type_code)
values (-25020, -103, 'Widget Form', 'body1', 'Form', 'static');
insert into page_region (id, page_id, region_id, region_num) values (-25020, -25002, -25020, 1);
```

### 5.1 Fields and labels

```sql
insert into field (id, template_id, "name", source_type_code, field_type_code, default_source_type_code) values
  (-25020, -60, 'id',     'static', 'NUMBER', null),   -- hidden; carries the pk
  (-25021, -61, 'name',   'static', 'TEXT',   null),
  (-25022, -80, 'Save',   'static', 'SUBMIT', null),
  (-25023, -80, 'Update', 'static', 'SUBMIT', null),
  (-25024, -80, 'Delete', 'static', 'SUBMIT', null);
-- region_field rows give each a field_num ordering (omitted for brevity).

insert into label (id, template_id, label_type_code, text, notes)
values (-25020, -1000, 'LEFT_MAND', 'Widget Name', 'Display name');
insert into field_label (id, field_id, label_id) values (-25020, -25021, -25020);
```

Field names double as: the HTML input name, the bind-variable name, the
`source_select` target, and (for buttons) the `:REQUEST` value. Keep them
lowercase-snake-case matching the DB columns and life is easy.
`label_type_code`: `LEFT_MAND` / `LEFT_OPT` (mandatory adds the marker).

### 5.2 GET: populate the form (RENDER_PG1)

A `dml_selrow` source selects the row; `page_processing_source_select` maps
result columns (by index) onto field names:

```sql
insert into source (id, "source")
values (-25025, 'select id, name from widget where CAST(id as VARCHAR) = :id');
insert into page_processing (id, page_id, processing_type_code, processing_num)
values (-25020, -25002, 'RENDER_PG1', 1);
insert into page_processing_source (id, page_processing_id, source_id, source_type_code)
values (-25020, -25020, -25025, 'dml_selrow');
insert into page_processing_source_select (id, page_processing_source_id, field_index, field_name) values
  (-25020, -25020, 0, 'id'),
  (-25021, -25020, 1, 'name');
```

With `pageParams=id:0` the query returns nothing and the form renders blank —
that *is* create mode. Prefer render queries that need only `:id`, deriving
parent ids via joins: then a post-Save redirect to `?id=<newid>` renders fully.

### 5.3 POST: Save / Update / Delete (POST1 + conditionals)

Each action is a POST1 processing gated by a `:REQUEST` conditional. The
**Save is `dml_selcel`**, a data-modifying CTE ending in a SELECT of the
generated id — `dml_modify` runs via `jdbc.update()` and cannot return values,
but Postgres executes every data-modifying CTE exactly once even if the final
SELECT is all you keep. The `source_select` row writes the id back into the
parameter map (replacing the submitted `:id`) before branches run — that is
how Save lands on the new row's edit page.

```sql
insert into source (id, "source") values
  (-25026, 'with new_w as (insert into widget (id, name) values (nextval(''spare_seq''), :name) returning id) select id from new_w'),
  (-25027, 'update widget set name = :name where CAST(id as VARCHAR) = :id'),
  (-25028, 'delete from widget where CAST(id as VARCHAR) = :id');

insert into page_processing (id, page_id, processing_type_code, processing_num, success_message) values
  (-25021, -25002, 'POST1', 1, 'Widget created.'),
  (-25022, -25002, 'POST1', 2, 'Widget updated.'),
  (-25023, -25002, 'POST1', 3, 'Widget deleted.');

insert into page_processing_source (id, page_processing_id, source_id, source_type_code) values
  (-25021, -25021, -25026, 'dml_selcel'),
  (-25022, -25022, -25027, 'dml_modify'),
  (-25023, -25023, -25028, 'dml_modify');

-- Write the generated id back into :id (APEX "returning into item"):
insert into page_processing_source_select (id, page_processing_source_id, field_index, field_name)
values (-25022, -25021, 0, 'id');

-- Gates: each processing runs only for its button.
insert into source (id, "source") values
  (-25029, 'select ''true'' where :REQUEST = ''Save'''),
  (-25030, 'select ''true'' where :REQUEST = ''Update'''),
  (-25031, 'select ''true'' where :REQUEST = ''Delete''');
insert into conditional (id, source_id, source_type_code, conditional_type_code) values
  (-25020, -25029, 'dml_selcel', 'TEXT_TRUE'),
  (-25021, -25030, 'dml_selcel', 'TEXT_TRUE'),
  (-25022, -25031, 'dml_selcel', 'TEXT_TRUE');
insert into page_processing_conditional (id, page_processing_id, conditional_id) values
  (-25020, -25021, -25020),
  (-25021, -25022, -25021),
  (-25022, -25023, -25022);
```

The form MUST contain a submit field whose name matches the intended
`:REQUEST` value, or the POST is ignored (`determineSubmittedButton`).
`success_message` values from passing processings are flashed across the
redirect and rendered into the page template's `##__NOTIFICATION__##` div
(consumed once).

For **multi-table creates** (e.g. REGION + PAGE_REGION + SOURCE +
REGION_SOURCE), chain data-modifying CTEs in one Save source — see the p4/p5/p6
files. A CTE insert can be made conditional with a `where` clause on a bind
(p6 inserts FIELD_SOURCE only `where length(:default_source) > 0`).

### 5.4 Branches: where to land after the POST (BRANCH1)

BRANCH1 processings evaluate after all POST1, in processing_num order; the
first whose conditional passes wins. A `'static'` source is a URL template
whose `:name` variables substitute (URL-encoded) from the post-write-back form;
a `'dml_selcel'` source is a query returning the URL. No branch → redirect back
to the same page with the form values as query params.

```sql
insert into source (id, "source") values
  (-25032, '/run/-25000/-25002?id=:id'),                          -- Save → new row's edit page
  (-25033, '/run/-25000/-25001'),                                 -- Update/Delete → report page
  (-25034, 'select ''true'' where :REQUEST in (''Update'', ''Delete'')');

insert into page_processing (id, page_id, processing_type_code, processing_num) values
  (-25024, -25002, 'BRANCH1', 4),
  (-25025, -25002, 'BRANCH1', 5);
insert into page_processing_source (id, page_processing_id, source_id, source_type_code) values
  (-25024, -25024, -25032, 'static'),
  (-25025, -25025, -25033, 'static');
-- Gate branch 4 with the existing :REQUEST='Save' conditional source, branch 5 with -25034
-- (new CONDITIONAL rows may reuse an existing SOURCE).
```

### 5.5 Validations

Validations run before any POST1; a failure skips all processing and
re-renders the page inline (HTTP 200) with submitted values preserved and the
error in `##__NOTIFICATION__##`. Gate them to the mutating buttons so Delete
isn't blocked by an empty form:

```sql
insert into source (id, "source")
values (-25035, 'select ''true'' where :REQUEST in (''Save'', ''Update'')');
insert into conditional (id, source_id, source_type_code, conditional_type_code)
values (-25023, -25035, 'dml_selcel', 'TEXT_TRUE');
insert into validation (id, page_id, "name", validation_num, validation_type_code, field_name, error_message)
values (-25020, -25002, 'name not null', 1, 'NOT_NULL', 'name', 'Widget Name is required.');
insert into validation_conditional (id, validation_id, conditional_id)
values (-25020, -25020, -25023);
```

`NOT_NULL` checks the submitted parameter; source-backed validation types go
through the conditional evaluator.

### 5.6 Conditional field display

FIELD conditionals get **field-typed** binds (§2), so compare directly. The
classic Save-vs-Update toggle, plus hiding Delete while children exist:

```sql
insert into source (id, "source") values
  (-25036, 'select 1 from widget where id = :id');
insert into conditional (id, source_id, source_type_code, conditional_type_code) values
  (-25024, -25036, 'dml_selcel', 'NOTEXISTS'),   -- row absent  → creating
  (-25025, -25036, 'dml_selcel', 'EXISTS');      -- row present → editing
insert into field_conditional (id, field_id, conditional_id) values
  (-25024, -25022, -25024),   -- Save shown when creating
  (-25025, -25023, -25025),   -- Update shown when editing
  (-25026, -25024, -25025);   -- Delete shown when editing (add a no-children EXISTS query if needed)
```

An unpopulated `id` field binds as NULL → matches nothing → NOTEXISTS passes:
blank form shows Save.

### 5.7 Dropdowns

Recipe (dropdowns only work because of the `FieldMapper` @ObjectFactory —
dispatch is by DTO class): template **-63**, `field_type_code 'DROPDOWN'`,
`source_type_code 'dml_select'`, field_source `flag_default_value = 'N'`, and a
**bind-free** two-column source (key, display value) — option sources receive
no binds at runtime:

```sql
insert into field (id, template_id, "name", source_type_code, field_type_code, default_source_type_code)
values (-25026, -63, 'template_id', 'dml_select', 'DROPDOWN', null);
insert into source (id, "source")
values (-25037, 'select CAST(id as VARCHAR), name from template where class_name like ''%RegionDto'' order by id desc');
insert into field_source (id, field_id, source_id, flag_default_value) values (-25026, -25026, -25037, 'N');
```

The selected option is whichever key string-equals the field's populated value
(from RENDER_PG1 source_select or a page param). For an *optional* dropdown,
add a `'none'` sentinel row (`select 'none', '- None -' union all ...`) and
strip it in the Save SQL with `NULLIF(:bind, 'none')` — remember the scraper
rule: the NULLIF's `''`-style literal trap doesn't apply here because `'none'`
starts with a word character.

## 6. Navigating between pages

- Report row → form: row-link field (§4) with URL prefix ending `id:`.
- Button → other page: javascript button (template -81) whose static source is
  a `location.href=...` snippet; read the current row's pk from the hidden id
  field, whose HTML element id is the *field id*:
  `document.getElementById(''-25020'').value`.
- Child pages derive parent ids by joining up from the row id rather than
  passing extra params (keeps render sources single-bind, §5.2).

## 7. Loading and verifying

1. Load the file: `PGPASSWORD=summit_dev psql -h localhost -U summit -d summit -v ON_ERROR_STOP=1 -q -f <file.sql>` — no app restart needed for metadata-only changes.
2. GET the page and grep the HTML; POST with the CSRF dance.
3. Add a `testing/verify/t_*.bash` script using `verify-lib.bash` (asserts on
   rendered HTML, `/api/filter/json/<regionId>`, and DB state; handles CSRF).
   Run the suite: `./testing/verify/run-verify.bash`.
   Gotcha: GET each POST's redirect target before the next POST — Spring
   consumes one FlashMap per matching request (path + query params), and a
   stale same-target flash shadows the next one.

"Found a Form ! FIXME" ERROR log lines during render are pre-existing noise,
not failures.

## 8. Gotcha checklist (fast reference)

- [ ] Binds: field-typed vs parameter-map-VARCHAR — CAST accordingly (§2).
- [ ] No `''` (or other non-word-leading) quoted literal after any bind.
- [ ] Every RENDER_PG1 bind present in the parameter map; create links pass `id:0`.
- [ ] `flag_default_value='Y'` ⇒ `default_source_type_code` set.
- [ ] Buttons: field_type `SUBMIT`, template in -80..-89; form has a button named for each `:REQUEST` it handles.
- [ ] Report link column aliased plain lowercase `id`; row-link field uses template -50.
- [ ] Report templates keep fields in a sibling `regionFields-<id>` div.
- [ ] Need a value back from DML (generated id)? `dml_selcel` CTE, not `dml_modify`.
- [ ] Hidden bind fields duplicated into every region whose source needs them.
- [ ] Dropdown option sources are bind-free, two columns, key as VARCHAR.
- [ ] File is re-runnable: deletes own rows (children first), owns its own page rows, references (not creates) other files' pages.
- [ ] Sequences bumped past manual ids before `nextval` inserts (`select setval(...)` guard, see p2 file).
