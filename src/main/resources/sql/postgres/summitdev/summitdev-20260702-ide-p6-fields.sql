/* Summit IDE (application -20000) - Page 6: Fields-on-region report + field
 * create/edit form.
 *
 * Report:  /run/-20000/-24000?pageParams=regionId:<regionId>
 *   Reached from the region form's (-23100) Fields button. Row click -> field
 *   form in edit mode (id:), Create button -> create mode (id:0,regionId:<id>).
 * Form:    /run/-20000/-24100?pageParams=id:0,regionId:<regionId>  (create)
 *          /run/-20000/-24100?pageParams=id:<fieldId>              (edit)
 *
 * ID blocks: report -24000..-24099, form -24100..-24199. Re-runnable.
 *
 * Form fields: name, field_num, template dropdown (field templates = the two
 * render field DTO class_names), field-type / source-type dropdowns (CODE_*),
 * optional default source type + default value source text.
 *
 * The "optional" default source type uses a 'none' sentinel option key and
 * NULLIF(:default_source_type_code, 'none') in the DML - 'none' starts with a
 * word character so the bind stays visible to the scraper ('' would hide it,
 * see doc/ide-plan.md). Same trick in reverse on render: coalesce(col, 'none')
 * selects the sentinel option for fields without a default source type.
 *
 * Save creates FIELD + REGION_FIELD always, and SOURCE + FIELD_SOURCE
 * (flag_default_value = 'Y') only when the default source text is non-empty
 * (the CTE's where length(:default_source) > 0 makes the last two inserts
 * no-ops otherwise - no literal traps).
 *
 * First-pass limitations (documented, not bugs):
 *  - Entering default source text requires also picking a default source type
 *    (usually 'static'), or the field fails to render - the standing
 *    flag_default_value='Y' + default_source_type_code=null gotcha.
 *  - Update only touches an existing default-value SOURCE; it won't create
 *    one for a field saved without (same shape as the region form).
 *  - field_source rows with flag 'N' (e.g. dropdown option sources) are not
 *    maintained here.
 */

START TRANSACTION;

---------------------------------------------------------------------------
-- Deletes (children first), both pages
---------------------------------------------------------------------------

delete from field_conditional where id in (-24100, -24101, -24102);
delete from page_processing_conditional where id in (-24100, -24101, -24102, -24103);
delete from validation_conditional where id in (-24100, -24101);
delete from validation where id in (-24100, -24101);
delete from conditional where id in (-24100, -24101, -24102, -24103, -24104, -24105, -24106);
delete from page_processing_source_select where id in (-24100, -24101, -24102, -24103, -24104, -24105, -24106, -24107, -24108);
delete from page_processing_source where id in (-24100, -24101, -24102, -24103, -24104);
delete from page_processing where id in (-24100, -24101, -24102, -24103, -24104);
delete from field_label where id in (-24100, -24101, -24102, -24103, -24104, -24105, -24106);
delete from label where id in (-24100, -24101, -24102, -24103, -24104, -24105, -24106);
delete from field_source where id in (-24001, -24002, -24101, -24102, -24103, -24104);
delete from region_field where id in (-24000, -24001, -24002, -24100, -24101, -24102, -24103, -24104, -24105, -24106, -24107, -24108, -24109, -24110, -24111);
delete from field where id in (-24000, -24001, -24002, -24100, -24101, -24102, -24103, -24104, -24105, -24106, -24107, -24108, -24109, -24110, -24111);
delete from region_source where id in (-24000);
delete from source where id in (-24000, -24001, -24002, -24100, -24101, -24102, -24103, -24104, -24105, -24106, -24107, -24108, -24109, -24110, -24111, -24112, -24113, -24114);
delete from page_region where id in (-24000, -24100);
delete from region where id in (-24000, -24100);
delete from application_page where id in (-24000, -24100);
delete from page where id in (-24000, -24100);

-- The Save CTE generates ids from these sequences.
select setval('field_seq',        greatest((select coalesce(max(id), 1) from field), 1));
select setval('region_field_seq', greatest((select coalesce(max(id), 1) from region_field), 1));
select setval('source_seq',       greatest((select coalesce(max(id), 1) from source), 1));
select setval('field_source_seq', greatest((select coalesce(max(id), 1) from field_source), 1));

---------------------------------------------------------------------------
-- Page -24000: Fields-on-region report
---------------------------------------------------------------------------

insert into page (id, template_id, "name")
values (-24000, -100, 'Region Fields Report');

insert into application_page (id, application_id, page_id, page_num)
values (-24000, -20000, -24000, 7);

insert into region (id, template_id, "name", code_region_position, code_region_type, source_type_code)
values (-24000, -200, 'Fields on Region', 'body1', 'Report', 'dml_report');

insert into page_region (id, page_id, region_id, region_num)
values (-24000, -24000, -24000, 1);

-- Report source: regionId is a NUMBER field -> numeric bind, compare directly.
-- First column MUST be aliased plain lowercase 'id' for row links.
insert into source (id, "source")
values (-24000, 'select f.id, f.name as "Field Name", rf.field_num as "Field Number", f.field_type_code as "Field Type" from region_field rf join field f on rf.field_id = f.id where rf.region_id = :regionId order by rf.field_num');

insert into region_source (id, region_id, source_id)
values (-24000, -24000, -24000);

-- Hidden field holding the region id from the request parameter
insert into field (id, template_id, "name", source_type_code, field_type_code, default_source_type_code)
values (-24000, -60, 'regionId', 'static', 'NUMBER', null);

insert into region_field (id, region_id, field_id, field_num)
values (-24000, -24000, -24000, 1);

-- Row link -> field form in edit mode (the form derives regionId from the
-- field id, so only id: is passed)
insert into field (id, template_id, "name", source_type_code, field_type_code, default_source_type_code)
values (-24001, -50, 'fields-sprt-link', 'static', 'TEXT', 'static');

insert into region_field (id, region_id, field_id, field_num)
values (-24001, -24000, -24001, 2);

insert into source (id, "source")
values (-24001, '##__CONTEXTPATH__##/run/-20000/-24100?pageParams=id:');

insert into field_source (id, field_id, source_id, flag_default_value)
values (-24001, -24001, -24001, 'Y');

-- Create button -> field form in create mode
insert into field (id, template_id, "name", source_type_code, field_type_code, default_source_type_code)
values (-24002, -81, 'Create', 'static', 'SUBMIT', 'static');

insert into region_field (id, region_id, field_id, field_num)
values (-24002, -24000, -24002, 3);

insert into source (id, "source")
values (-24002, 'location.href=''##__CONTEXTPATH__##/run/-20000/-24100?pageParams=id:0,regionId:'' + document.getElementById(''-24000'').value;');

insert into field_source (id, field_id, source_id, flag_default_value)
values (-24002, -24002, -24002, 'Y');

---------------------------------------------------------------------------
-- Page -24100: Field create/edit form
---------------------------------------------------------------------------

insert into page (id, template_id, "name")
values (-24100, -100, 'Field - Create / Edit');

insert into application_page (id, application_id, page_id, page_num)
values (-24100, -20000, -24100, 8);

insert into region (id, template_id, "name", code_region_position, code_region_type, source_type_code)
values (-24100, -103, 'Field Form', 'body1', 'Form', 'static');

insert into page_region (id, page_id, region_id, region_num)
values (-24100, -24100, -24100, 1);

-- GET: populate the form from FIELD + REGION_FIELD + (optional) default SOURCE.
-- All quoted literals sit BEFORE the only bind (:id), so the scraper is safe.
insert into source (id, "source")
values (-24100, 'select f.id, rf.region_id, f.name, rf.field_num, f.template_id, f.field_type_code, f.source_type_code, coalesce(f.default_source_type_code, ''none''), coalesce(s.source, '''') from field f join region_field rf on rf.field_id = f.id left join field_source fs on fs.field_id = f.id and fs.flag_default_value = ''Y'' left join source s on s.id = fs.source_id where CAST(f.id as VARCHAR) = :id');

insert into page_processing (id, page_id, processing_type_code, processing_num)
values (-24100, -24100, 'RENDER_PG1', 1);

insert into page_processing_source (id, page_processing_id, source_id, source_type_code)
values (-24100, -24100, -24100, 'dml_selrow');

insert into page_processing_source_select (id, page_processing_source_id, field_index, field_name)
values (-24100, -24100, 0, 'id'),
       (-24101, -24100, 1, 'regionId'),
       (-24102, -24100, 2, 'name'),
       (-24103, -24100, 3, 'field_num'),
       (-24104, -24100, 4, 'template_id'),
       (-24105, -24100, 5, 'field_type_code'),
       (-24106, -24100, 6, 'source_type_code'),
       (-24107, -24100, 7, 'default_source_type_code'),
       (-24108, -24100, 8, 'default_source');

-- Fields
insert into field (id, template_id, "name", source_type_code, field_type_code, default_source_type_code) values
  (-24100, -60, 'id',                       'static',     'NUMBER',   null),
  (-24101, -60, 'regionId',                 'static',     'NUMBER',   null),
  (-24102, -61, 'name',                     'static',     'TEXT',     null),
  (-24103, -62, 'field_num',                'static',     'NUMBER',   null),
  (-24104, -63, 'template_id',              'dml_select', 'DROPDOWN', null),
  (-24105, -63, 'field_type_code',          'dml_select', 'DROPDOWN', null),
  (-24106, -63, 'source_type_code',         'dml_select', 'DROPDOWN', null),
  (-24107, -63, 'default_source_type_code', 'dml_select', 'DROPDOWN', null),
  (-24108, -64, 'default_source',           'static',     'TEXT',     null),
  (-24109, -80, 'Save',                     'static',     'SUBMIT',   null),
  (-24110, -80, 'Update',                   'static',     'SUBMIT',   null),
  (-24111, -80, 'Delete',                   'static',     'SUBMIT',   null);

insert into region_field (id, region_id, field_id, field_num) values
  (-24100, -24100, -24100, 1),
  (-24101, -24100, -24101, 2),
  (-24102, -24100, -24102, 3),
  (-24103, -24100, -24103, 4),
  (-24104, -24100, -24104, 5),
  (-24105, -24100, -24105, 6),
  (-24106, -24100, -24106, 7),
  (-24107, -24100, -24107, 8),
  (-24108, -24100, -24108, 9),
  (-24109, -24100, -24109, 10),
  (-24110, -24100, -24110, 11),
  (-24111, -24100, -24111, 12);

-- Labels
insert into label (id, template_id, label_type_code, text, notes) values
  (-24100, -1000, 'LEFT_OPT',  'Field Name',           'Also the bind variable / submitted parameter name'),
  (-24101, -1000, 'LEFT_MAND', 'Field Number',         'Render order within the region'),
  (-24102, -1000, 'LEFT_MAND', 'Field Template',       'Input item template; -80..-89 render as buttons'),
  (-24103, -1000, 'LEFT_MAND', 'Field Type',           'Drives bind variable typing (NUMBER -> numeric)'),
  (-24104, -1000, 'LEFT_MAND', 'Source Type',          'How the field value source is processed'),
  (-24105, -1000, 'LEFT_OPT',  'Default Source Type',  'Required if a default value source is entered (usually static)'),
  (-24106, -1000, 'LEFT_OPT',  'Default Value Source', 'e.g. static text or button javascript');

insert into field_label (id, field_id, label_id) values
  (-24100, -24102, -24100),
  (-24101, -24103, -24101),
  (-24102, -24104, -24102),
  (-24103, -24105, -24103),
  (-24104, -24106, -24104),
  (-24105, -24107, -24105),
  (-24106, -24108, -24106);

-- Dropdown option sources (two columns, bind-free).
-- Field templates = the two render field DTO classes (this also excludes the
-- old dto.entry.FieldDto template id 13).
insert into source (id, "source") values
  (-24101, 'select id, description from template where class_name in (''org.awiki.kamikaze.summit.dto.render.FieldDto'', ''org.awiki.kamikaze.summit.dto.render.DropDownFieldDto'') order by id desc'),
  (-24102, 'select code, description from code_field_type order by sort_order'),
  (-24103, 'select code, description from code_source_type order by sort_order'),
  (-24104, 'select code, description from (select ''none'' as code, ''(none)'' as description, -1 as sort_order union all select code, description, sort_order from code_source_type) t order by sort_order');

insert into field_source (id, field_id, source_id, flag_default_value) values
  (-24101, -24104, -24101, 'N'),
  (-24102, -24105, -24102, 'N'),
  (-24103, -24106, -24103, 'N'),
  (-24104, -24107, -24104, 'N');

-- POST: Save = one CTE inserting FIELD + REGION_FIELD (+ SOURCE + FIELD_SOURCE
--       when default source text was entered), Update = one CTE updating
--       FIELD, REGION_FIELD.field_num and the default SOURCE.
insert into source (id, "source") values
  (-24105, 'with new_field as (insert into field (id, template_id, name, source_type_code, field_type_code, default_source_type_code) select nextval(''field_seq''), CAST(:template_id as NUMERIC), :name, :source_type_code, :field_type_code, NULLIF(:default_source_type_code, ''none'') returning id), new_rf as (insert into region_field (id, region_id, field_id, field_num) select nextval(''region_field_seq''), CAST(:regionId as NUMERIC), nf.id, CAST(:field_num as NUMERIC) from new_field nf returning id), new_source as (insert into source (id, source) select nextval(''source_seq''), :default_source where length(:default_source) > 0 returning id) insert into field_source (id, field_id, source_id, flag_default_value) select nextval(''field_source_seq''), nf.id, ns.id, ''Y'' from new_field nf, new_source ns'),
  (-24106, 'with upd_field as (update field set template_id = CAST(:template_id as NUMERIC), name = :name, source_type_code = :source_type_code, field_type_code = :field_type_code, default_source_type_code = NULLIF(:default_source_type_code, ''none'') where CAST(id as VARCHAR) = :id returning id), upd_rf as (update region_field set field_num = CAST(:field_num as NUMERIC) where field_id in (select id from upd_field) returning id) update source set source = :default_source where id in (select fs.source_id from field_source fs join upd_field uf on fs.field_id = uf.id where fs.flag_default_value = ''Y'')');

insert into page_processing (id, page_id, processing_type_code, processing_num, success_message) values
  (-24101, -24100, 'POST1', 1, 'Field created.'),
  (-24102, -24100, 'POST1', 2, 'Field updated.');

insert into page_processing_source (id, page_processing_id, source_id, source_type_code) values
  (-24101, -24101, -24105, 'dml_modify'),
  (-24102, -24102, -24106, 'dml_modify');

insert into source (id, "source") values
  (-24107, 'select ''true'' where :REQUEST = ''Save'''),
  (-24108, 'select ''true'' where :REQUEST = ''Update''');

insert into conditional (id, source_id, source_type_code, conditional_type_code) values
  (-24100, -24107, 'dml_selcel', 'TEXT_TRUE'),
  (-24101, -24108, 'dml_selcel', 'TEXT_TRUE');

insert into page_processing_conditional (id, page_processing_id, conditional_id) values
  (-24100, -24101, -24100),
  (-24101, -24102, -24101);

---------------------------------------------------------------------------
-- Delete: fields are the leaf of the hierarchy, so this cascades the field's
-- own child rows in one CTE: FIELD_SOURCE (+ its SOURCE rows), FIELD_LABEL
-- (+ its LABEL rows), FIELD_CONDITIONAL, REGION_FIELD, then FIELD.
---------------------------------------------------------------------------

insert into source (id, "source") values
  (-24112, 'with del_fs as (delete from field_source where CAST(field_id as VARCHAR) = :id returning source_id), del_src as (delete from source where id in (select source_id from del_fs) returning id), del_fl as (delete from field_label where CAST(field_id as VARCHAR) = :id returning label_id), del_lbl as (delete from label where id in (select label_id from del_fl) returning id), del_fc as (delete from field_conditional where CAST(field_id as VARCHAR) = :id returning id), del_rf as (delete from region_field where CAST(field_id as VARCHAR) = :id returning id) delete from field where CAST(id as VARCHAR) = :id'),
  (-24113, 'select ''true'' where :REQUEST = ''Delete''');

insert into page_processing (id, page_id, processing_type_code, processing_num, success_message)
values (-24104, -24100, 'POST1', 3, 'Field deleted.');

insert into page_processing_source (id, page_processing_id, source_id, source_type_code)
values (-24104, -24104, -24112, 'dml_modify');

insert into conditional (id, source_id, source_type_code, conditional_type_code)
values (-24105, -24113, 'dml_selcel', 'TEXT_TRUE');

insert into page_processing_conditional (id, page_processing_id, conditional_id)
values (-24103, -24104, -24105);

---------------------------------------------------------------------------
-- Validations: run on POST before any processing, gated to Save/Update.
-- Field name is validated even though its label is optional: it is the bind
-- variable / submitted parameter name, so it is functionally required.
---------------------------------------------------------------------------

insert into source (id, "source")
values (-24114, 'select ''true'' where :REQUEST in (''Save'', ''Update'')');

insert into conditional (id, source_id, source_type_code, conditional_type_code)
values (-24106, -24114, 'dml_selcel', 'TEXT_TRUE');

insert into validation (id, page_id, "name", validation_num, validation_type_code, field_name, error_message) values
  (-24100, -24100, 'name not null',      1, 'NOT_NULL', 'name',      'Field Name is required.'),
  (-24101, -24100, 'field_num not null', 2, 'NOT_NULL', 'field_num', 'Field Number is required.');

insert into validation_conditional (id, validation_id, conditional_id) values
  (-24100, -24100, -24106),
  (-24101, -24101, -24106);

---------------------------------------------------------------------------
-- Branch: after a Save, Update or Delete, land back on the Fields-on-Region
-- report for this region. The :regionId in the static URL template
-- substitutes from the submitted form (the hidden regionId field).
---------------------------------------------------------------------------

insert into code_processing_type
select 'BRANCH1', 'Page Branch After Page POST Processing', 3
where not exists (select 1 from code_processing_type where code = 'BRANCH1');

insert into source (id, "source") values
  (-24110, 'select ''true'' where :REQUEST in (''Save'', ''Update'', ''Delete'')'),
  (-24111, '/run/-20000/-24000?regionId=:regionId');

insert into page_processing (id, page_id, processing_type_code, processing_num)
values (-24103, -24100, 'BRANCH1', 4);

insert into page_processing_source (id, page_processing_id, source_id, source_type_code)
values (-24103, -24103, -24111, 'static');

insert into conditional (id, source_id, source_type_code, conditional_type_code)
values (-24104, -24110, 'dml_selcel', 'TEXT_TRUE');

insert into page_processing_conditional (id, page_processing_id, conditional_id)
values (-24102, -24103, -24104);

-- Conditional button display: Save when creating, Update when editing.
insert into source (id, "source")
values (-24109, 'select 1 from field where id = :id');

insert into conditional (id, source_id, source_type_code, conditional_type_code) values
  (-24102, -24109, 'dml_selcel', 'NOTEXISTS'),
  (-24103, -24109, 'dml_selcel', 'EXISTS');

insert into field_conditional (id, field_id, conditional_id) values
  (-24100, -24109, -24102),  -- Save   : shown when the field does not exist
  (-24101, -24110, -24103),  -- Update : shown when the field exists
  (-24102, -24111, -24103);  -- Delete : shown when the field exists (leaf, no gate needed)

COMMIT;
