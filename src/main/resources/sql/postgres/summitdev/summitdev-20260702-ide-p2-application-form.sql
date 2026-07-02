/* Summit IDE (application -20000) - Page 2: Application create/edit form.
 *
 * /run/-20000/-21100                        -> blank form, Save button
 * /run/-20000/-21100?pageParams=id:<appId>  -> populated form, Update + Pages buttons
 *
 * Reached from the Applications list (page -21000): row click passes the id,
 * the Create button passes nothing. See doc/ide-plan.md.
 * ID block for this page: -21100..-21199. Re-runnable: deletes its own rows first.
 *
 * Bind variable rules (learned the hard way, see doc/ide-plan.md):
 *  - page processing binds (dml_selrow/dml_modify + their conditionals) come
 *    from the parameter map and are always VARCHAR: numeric columns are
 *    compared with CAST(col as VARCHAR) = :bind, and numeric dml_modify
 *    values need CAST(:bind as NUMERIC).
 *  - FIELD conditional binds come from the region's fields and are typed by
 *    field_type_code (NUMBER -> numeric, unpopulated -> NULL), so those
 *    sources compare numeric columns directly: id = :id.
 *  - avoid NULLIF(:x, '') and similar: a quoted literal starting with a
 *    non-word character after a bind hides the bind from the scraper regex.
 *  - "create" mode is entered with pageParams=id:0 (id 0 never exists);
 *    an absent :id parameter would fail render processing, and 'id:' with an
 *    empty value breaks StringUtils.toParameterMap.
 */

START TRANSACTION;

delete from field_conditional where id in (-21100, -21101, -21102);
delete from page_processing_conditional where id in (-21100, -21101);
delete from conditional where id in (-21100, -21101, -21102, -21103);
delete from page_processing_source_select where id in (-21100, -21101, -21102);
delete from page_processing_source where id in (-21100, -21101, -21102);
delete from page_processing where id in (-21100, -21101, -21102);
delete from field_label where id in (-21100, -21101);
delete from label where id in (-21100, -21101);
delete from field_source where id in (-21105);
delete from region_field where id in (-21100, -21101, -21102, -21103, -21104, -21105);
delete from field where id in (-21100, -21101, -21102, -21103, -21104, -21105);
delete from source where id in (-21100, -21105, -21106, -21107, -21108, -21109, -21110);
delete from page_region where id = -21100;
delete from region where id = -21100;
delete from application_page where id = -21100;
delete from page where id = -21100;

-- The insert below generates ids from application_seq; make sure it is ahead
-- of every manually-inserted application id.
select setval('application_seq', greatest((select coalesce(max(id), 1) from application), 1));

-- Page (template -100 = Summit Standard Page Style 1)
insert into page (id, template_id, "name")
values (-21100, -100, 'Application - Create / Edit');

insert into application_page (id, application_id, page_id, page_num)
values (-21100, -20000, -21100, 3);

-- Form region (template -103 = Summit - Internal Edit - Form Region)
insert into region (id, template_id, "name", code_region_position, code_region_type, source_type_code)
values (-21100, -103, 'Application Form', 'body1', 'Form', 'static');

insert into page_region (id, page_id, region_id, region_num)
values (-21100, -21100, -21100, 1);

---------------------------------------------------------------------------
-- GET: populate the form fields from the application row when :id is given
---------------------------------------------------------------------------

insert into source (id, "source")
values (-21100, 'select id, application_num, name from application where CAST(id as VARCHAR) = :id');

insert into page_processing (id, page_id, processing_type_code, processing_num)
values (-21100, -21100, 'RENDER_PG1', 1);

insert into page_processing_source (id, page_processing_id, source_id, source_type_code)
values (-21100, -21100, -21100, 'dml_selrow');

-- Map result columns to fields by index; names must match the field names.
insert into page_processing_source_select (id, page_processing_source_id, field_index, field_name)
values (-21100, -21100, 0, 'id'),
       (-21101, -21100, 1, 'application_num'),
       (-21102, -21100, 2, 'name');

---------------------------------------------------------------------------
-- Fields
---------------------------------------------------------------------------

--                        ID, TEMPLATE_ID, NAME, SOURCE_TYPE, FIELD_TYPE, DEFAULT_SOURCE_TYPE
insert into field (id, template_id, "name", source_type_code, field_type_code, default_source_type_code) values
  (-21100, -60, 'id',              'static', 'NUMBER', null),
  (-21101, -62, 'application_num', 'static', 'NUMBER', null),
  (-21102, -61, 'name',            'static', 'TEXT',   null),
  (-21103, -80, 'Save',            'static', 'SUBMIT', null),
  (-21104, -80, 'Update',          'static', 'SUBMIT', null),
  (-21105, -81, 'Pages',           'static', 'SUBMIT', 'static');

insert into region_field (id, region_id, field_id, field_num) values
  (-21100, -21100, -21100, 1),
  (-21101, -21100, -21101, 2),
  (-21102, -21100, -21102, 3),
  (-21103, -21100, -21103, 4),
  (-21104, -21100, -21104, 5),
  (-21105, -21100, -21105, 6);

-- Labels for the editable fields
insert into label (id, template_id, label_type_code, text, notes) values
  (-21100, -1000, 'LEFT_MAND', 'Application Number', 'Unique number for the application'),
  (-21101, -1000, 'LEFT_OPT',  'Application Name',   'Human readable application name');

insert into field_label (id, field_id, label_id) values
  (-21100, -21101, -21100),
  (-21101, -21102, -21101);

-- Pages button: navigate to the pages-in-application report for this app.
-- Element id '-21100' is the hidden id field (fields render with id = field id).
insert into source (id, "source")
values (-21105, 'location.href=''##__CONTEXTPATH__##/run/-20000/-20002?pageParams=applicationId:'' + document.getElementById(''-21100'').value;');

insert into field_source (id, field_id, source_id, flag_default_value)
values (-21105, -21105, -21105, 'Y');

---------------------------------------------------------------------------
-- POST: Save = INSERT (id from application_seq), Update = UPDATE
---------------------------------------------------------------------------

insert into source (id, "source") values
  (-21106, 'insert into application (id, application_num, name) values (nextval(''application_seq''), CAST(:application_num as NUMERIC), :name)'),
  (-21107, 'update application set application_num = CAST(:application_num as NUMERIC), name = :name where CAST(id as VARCHAR) = :id');

insert into page_processing (id, page_id, processing_type_code, processing_num) values
  (-21101, -21100, 'POST1', 1),
  (-21102, -21100, 'POST1', 2);

insert into page_processing_source (id, page_processing_id, source_id, source_type_code) values
  (-21101, -21101, -21106, 'dml_modify'),
  (-21102, -21102, -21107, 'dml_modify');

-- Run the INSERT only for Save, the UPDATE only for Update.
insert into source (id, "source") values
  (-21108, 'select ''true'' where :REQUEST = ''Save'''),
  (-21109, 'select ''true'' where :REQUEST = ''Update''');

insert into conditional (id, source_id, source_type_code, conditional_type_code) values
  (-21100, -21108, 'dml_selcel', 'TEXT_TRUE'),
  (-21101, -21109, 'dml_selcel', 'TEXT_TRUE');

insert into page_processing_conditional (id, page_processing_id, conditional_id) values
  (-21100, -21101, -21100),
  (-21101, -21102, -21101);

---------------------------------------------------------------------------
-- Conditional button display: Save when creating, Update + Pages when editing
---------------------------------------------------------------------------

-- Field conditional: binds come from the region's FIELDS (typed), so compare
-- the numeric column directly. Unpopulated id field binds as NULL -> no rows.
insert into source (id, "source")
values (-21110, 'select 1 from application where id = :id');

insert into conditional (id, source_id, source_type_code, conditional_type_code) values
  (-21102, -21110, 'dml_selcel', 'NOTEXISTS'),
  (-21103, -21110, 'dml_selcel', 'EXISTS');

insert into field_conditional (id, field_id, conditional_id) values
  (-21100, -21103, -21102),  -- Save     : shown when the app does not exist
  (-21101, -21104, -21103),  -- Update   : shown when the app exists
  (-21102, -21105, -21103);  -- Pages    : shown when the app exists

COMMIT;
