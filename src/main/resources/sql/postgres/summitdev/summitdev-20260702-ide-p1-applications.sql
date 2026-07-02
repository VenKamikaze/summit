/* Summit IDE (application -20000) - Page 1: Applications list.
 *
 * Report of all applications with:
 *  - row click -> application edit form (page -21100, built next; shows
 *    "does not exist" until then)
 *  - Create button -> same form page with no id (blank form)
 *
 * See doc/ide-plan.md. ID block for this page: -21000..-21099.
 * Re-runnable: deletes its own rows first.
 */

START TRANSACTION;

delete from field_source where id in (-21001, -21002);
delete from region_field where id in (-21001, -21002);
delete from field where id in (-21001, -21002);
delete from region_source where id = -21000;
delete from source where id in (-21000, -21001, -21002);
delete from page_region where id = -21000;
delete from region where id = -21000;
delete from application_page where id = -21000;
delete from page where id = -21000;

-- Page (template -100 = Summit Standard Page Style 1)
insert into page (id, template_id, "name")
values (-21000, -100, 'Applications');

insert into application_page (id, application_id, page_id, page_num)
values (-21000, -20000, -21000, 1);

-- Report region (template -200 = Summit Standard Interactive Report Style 1)
insert into region (id, template_id, "name", code_region_position, code_region_type, source_type_code)
values (-21000, -200, 'Applications', 'body1', 'Report', 'dml_report');

insert into page_region (id, page_id, region_id, region_num)
values (-21000, -21000, -21000, 1);

-- Report source. The first column MUST be aliased plain lowercase 'id':
-- summit-report.js looks for a header cell named 'id' to build row links.
insert into source (id, "source")
values (-21000, 'select id, application_num as "Application Number", name as "Application Name" from application order by name');

insert into region_source (id, region_id, source_id)
values (-21000, -21000, -21000);

-- Row link (template -50): renders hidden input id="region--21000-sprt-link";
-- the report JS appends the clicked row''s id value to this URL prefix.
insert into field (id, template_id, "name", source_type_code, field_type_code, default_source_type_code)
values (-21001, -50, 'applications-sprt-link', 'static', 'TEXT', 'static');

insert into region_field (id, region_id, field_id, field_num)
values (-21001, -21000, -21001, 1);

insert into source (id, "source")
values (-21001, '##__CONTEXTPATH__##/run/-20000/-21100?pageParams=id:');

insert into field_source (id, field_id, source_id, flag_default_value)
values (-21001, -21001, -21001, 'Y');

-- Create button (template -81 = Javascript Button). field_type_code SUBMIT is
-- the codetable''s generic button type; button behaviour comes from the
-- template id range (-80..-89), see FieldService.BUTTONS_PREDICATE.
-- default_source_type_code must be set when field_source.flag_default_value
-- is Y, or field rendering fails with "No service found for sourceType=null".
insert into field (id, template_id, "name", source_type_code, field_type_code, default_source_type_code)
values (-21002, -81, 'Create', 'static', 'SUBMIT', 'static');

insert into region_field (id, region_id, field_id, field_num)
values (-21002, -21000, -21002, 2);

-- id:0 puts the form in "create" mode: the id never exists, and the form page's
-- render processing requires an :id parameter to be present.
insert into source (id, "source")
values (-21002, 'location.href=''##__CONTEXTPATH__##/run/-20000/-21100?pageParams=id:0'';');

insert into field_source (id, field_id, source_id, flag_default_value)
values (-21002, -21002, -21002, 'Y');

COMMIT;
