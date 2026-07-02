/* Summit IDE (application -20000) - Page 4: Page create/edit form.
 *
 * Makes the old empty "Create New Page" stub (-20102) a real form.
 *
 * /run/-20000/-20102?pageParams=id:0,applicationId:<appId>  -> blank form, Save
 * /run/-20000/-20102?pageParams=id:<pageId>                 -> populated form, Update
 *
 * Reached from the Pages-in-Application report (page -20002): row click passes
 * the page id (edit mode; the applicationId is derived from the page id by the
 * render query), the Create button passes id:0 plus the applicationId.
 *
 * This page owns the PAGE/APPLICATION_PAGE rows for -20102 (moved out of
 * summitdev-20241102.sql) - load that file first, then this one.
 * ID block for the new rows: -22000..-22099. Re-runnable.
 *
 * First multi-table insert in the IDE: Save creates PAGE and APPLICATION_PAGE
 * in one dml_modify source via a Postgres data-modifying CTE. Verified against
 * the runtime before committing to the approach:
 *  - the bind scraper (BindVarServiceImpl) is a plain :word regex, no SQL
 *    parsing, so the CTE syntax is invisible to it. The usual quoted-literal
 *    trap still applies: a bind is hidden if the next quoted literal starts
 *    with a non-word char - here the only literals after binds are sequence
 *    names ('application_page_seq'), which start with a word char. Checked.
 *  - dml_modify executes via NamedParameterJdbcTemplate.update(), which
 *    happily runs a WITH ... INSERT statement.
 * Standard bind rules apply (doc/ide-plan.md): page-processing binds are
 * always VARCHAR -> CAST(col as VARCHAR) = :bind / CAST(:bind as NUMERIC).
 */

START TRANSACTION;

delete from field_conditional where id in (-22000, -22001, -22002);
delete from page_processing_conditional where id in (-22000, -22001);
delete from conditional where id in (-22000, -22001, -22002, -22003);
delete from page_processing_source_select where id in (-22000, -22001, -22002, -22003, -22004);
delete from page_processing_source where id in (-22000, -22001, -22002);
delete from page_processing where id in (-22000, -22001, -22002);
delete from field_label where id in (-22000, -22001, -22002);
delete from label where id in (-22000, -22001, -22002);
delete from field_source where id in (-22001, -22007);
delete from region_field where id in (-22000, -22001, -22002, -22003, -22004, -22005, -22006, -22007);
delete from field where id in (-22000, -22001, -22002, -22003, -22004, -22005, -22006, -22007);
delete from source where id in (-22000, -22001, -22002, -22003, -22004, -22005, -22006, -22007);
delete from page_region where id = -22000;
delete from region where id = -22000;
delete from application_page where id = -20102;
delete from page where id = -20102;

-- The Save CTE generates ids from these sequences; make sure they are ahead
-- of every manually-inserted row (metadata ids are negative, test data isn't).
select setval('page_seq', greatest((select coalesce(max(id), 1) from page), 1));
select setval('application_page_seq', greatest((select coalesce(max(id), 1) from application_page), 1));

-- Page (template -100 = Summit Standard Page Style 1). Keeps the stub's id
-- -20102 because the pages report's links already target it.
insert into page (id, template_id, "name")
values (-20102, -100, 'Page - Create / Edit');

insert into application_page (id, application_id, page_id, page_num)
values (-20102, -20000, -20102, 4);

-- Form region (template -103 = Summit - Internal Edit - Form Region)
insert into region (id, template_id, "name", code_region_position, code_region_type, source_type_code)
values (-22000, -103, 'Page Form', 'body1', 'Form', 'static');

insert into page_region (id, page_id, region_id, region_num)
values (-22000, -20102, -22000, 1);

---------------------------------------------------------------------------
-- GET: populate the form fields from PAGE + APPLICATION_PAGE when :id is given
---------------------------------------------------------------------------

insert into source (id, "source")
values (-22000, 'select p.id, ap.application_id, p.name, ap.page_num, p.template_id from page p join application_page ap on ap.page_id = p.id where CAST(p.id as VARCHAR) = :id');

insert into page_processing (id, page_id, processing_type_code, processing_num)
values (-22000, -20102, 'RENDER_PG1', 1);

insert into page_processing_source (id, page_processing_id, source_id, source_type_code)
values (-22000, -22000, -22000, 'dml_selrow');

-- Map result columns to fields by index; names must match the field names.
-- applicationId is populated from the join in edit mode (the row link only
-- passes id:), and from the pageParams in create mode.
insert into page_processing_source_select (id, page_processing_source_id, field_index, field_name)
values (-22000, -22000, 0, 'id'),
       (-22001, -22000, 1, 'applicationId'),
       (-22002, -22000, 2, 'name'),
       (-22003, -22000, 3, 'page_num'),
       (-22004, -22000, 4, 'template_id');

---------------------------------------------------------------------------
-- Fields
---------------------------------------------------------------------------

insert into field (id, template_id, "name", source_type_code, field_type_code, default_source_type_code) values
  (-22000, -60, 'id',            'static',     'NUMBER',   null),
  (-22001, -60, 'applicationId', 'static',     'NUMBER',   null),
  (-22002, -61, 'name',          'static',     'TEXT',     null),
  (-22003, -62, 'page_num',      'static',     'NUMBER',   null),
  (-22004, -63, 'template_id',   'dml_select', 'DROPDOWN', null),
  (-22005, -80, 'Save',          'static',     'SUBMIT',   null),
  (-22006, -80, 'Update',        'static',     'SUBMIT',   null),
  (-22007, -81, 'Regions',       'static',     'SUBMIT',   'static');

insert into region_field (id, region_id, field_id, field_num) values
  (-22000, -22000, -22000, 1),
  (-22001, -22000, -22001, 2),
  (-22002, -22000, -22002, 3),
  (-22003, -22000, -22003, 4),
  (-22004, -22000, -22004, 5),
  (-22005, -22000, -22005, 6),
  (-22006, -22000, -22006, 7),
  (-22007, -22000, -22007, 8);

-- Labels for the editable fields
insert into label (id, template_id, label_type_code, text, notes) values
  (-22000, -1000, 'LEFT_OPT',  'Page Name',     'Human readable page name'),
  (-22001, -1000, 'LEFT_MAND', 'Page Number',   'Page number within the application'),
  (-22002, -1000, 'LEFT_MAND', 'Page Template', 'Page-level template (class PageDto)');

insert into field_label (id, field_id, label_id) values
  (-22000, -22002, -22000),
  (-22001, -22003, -22001),
  (-22002, -22004, -22002);

-- Dropdown options: page-level templates from the TEMPLATE table.
-- Exactly two columns (key, display value), processed with dml_select.
insert into source (id, "source")
values (-22001, 'select id, description from template where class_name = ''org.awiki.kamikaze.summit.dto.render.PageDto'' order by id desc');

insert into field_source (id, field_id, source_id, flag_default_value)
values (-22001, -22004, -22001, 'N');

-- Regions button: navigate to the regions-on-page report (IDE page 5) for
-- this page. Element id '-22000' is the hidden id field.
insert into source (id, "source")
values (-22007, 'location.href=''##__CONTEXTPATH__##/run/-20000/-23000?pageParams=pageId:'' + document.getElementById(''-22000'').value;');

insert into field_source (id, field_id, source_id, flag_default_value)
values (-22007, -22007, -22007, 'Y');

---------------------------------------------------------------------------
-- POST: Save = INSERT into PAGE + APPLICATION_PAGE (one CTE), Update = both
---------------------------------------------------------------------------

insert into source (id, "source") values
  (-22002, 'with new_page as (insert into page (id, template_id, name) select nextval(''page_seq''), CAST(:template_id as NUMERIC), :name returning id) insert into application_page (id, application_id, page_id, page_num) select nextval(''application_page_seq''), CAST(:applicationId as NUMERIC), id, CAST(:page_num as NUMERIC) from new_page'),
  (-22003, 'with upd as (update page set template_id = CAST(:template_id as NUMERIC), name = :name where CAST(id as VARCHAR) = :id returning id) update application_page set page_num = CAST(:page_num as NUMERIC) where page_id in (select id from upd)');

insert into page_processing (id, page_id, processing_type_code, processing_num) values
  (-22001, -20102, 'POST1', 1),
  (-22002, -20102, 'POST1', 2);

insert into page_processing_source (id, page_processing_id, source_id, source_type_code) values
  (-22001, -22001, -22002, 'dml_modify'),
  (-22002, -22002, -22003, 'dml_modify');

-- Run the INSERT only for Save, the UPDATE only for Update.
insert into source (id, "source") values
  (-22004, 'select ''true'' where :REQUEST = ''Save'''),
  (-22005, 'select ''true'' where :REQUEST = ''Update''');

insert into conditional (id, source_id, source_type_code, conditional_type_code) values
  (-22000, -22004, 'dml_selcel', 'TEXT_TRUE'),
  (-22001, -22005, 'dml_selcel', 'TEXT_TRUE');

insert into page_processing_conditional (id, page_processing_id, conditional_id) values
  (-22000, -22001, -22000),
  (-22001, -22002, -22001);

---------------------------------------------------------------------------
-- Conditional button display: Save when creating, Update + Regions when editing
---------------------------------------------------------------------------

-- Field conditional: binds come from the region's FIELDS (typed), so compare
-- the numeric column directly. id:0 in create mode matches no page.
insert into source (id, "source")
values (-22006, 'select 1 from page where id = :id');

insert into conditional (id, source_id, source_type_code, conditional_type_code) values
  (-22002, -22006, 'dml_selcel', 'NOTEXISTS'),
  (-22003, -22006, 'dml_selcel', 'EXISTS');

insert into field_conditional (id, field_id, conditional_id) values
  (-22000, -22005, -22002),  -- Save    : shown when the page does not exist
  (-22001, -22006, -22003),  -- Update  : shown when the page exists
  (-22002, -22007, -22003);  -- Regions : shown when the page exists

COMMIT;
