/* Commence building a backend IDE for building new applications/pages/regions/source/fields etc */

-- /run/-20000/-20002
-- This report will show all pages within a particular application.
-- The application ID is driven by the :applicationId bind parameter which should match a hidden field value.
--
-- Fixed 2026-07-02 (see doc/ide-plan.md):
--  * FIELD_TYPE_CODE 'BUTTON' does not exist in CODE_FIELD_TYPE - buttons use 'SUBMIT'
--    (button behaviour comes from the template id range -80..-89).
--  * A field_source with flag_default_value = 'Y' requires the field to have
--    default_source_type_code set, or rendering fails with "No service found for sourceType=null".
--  * Request parameters are only picked up via the pageParams=key:value format
--    (StringUtils.toParameterMap), not as bare query parameters.
--  * Bind variables followed by a quoted literal starting with a non-word char
--    (e.g. NULLIF(:x, '')) are NOT extracted by BindVarServiceImpl's regex and
--    the query then fails with "No value supplied for the SQL parameter".
--  * Report region binds come from FIELDS and are typed by field_type_code
--    (NUMBER field -> numeric bind), so ap.application_id = :applicationId is
--    correct here. Page processing binds (dml_selrow/dml_modify/conditionals)
--    are always VARCHAR - those queries need CAST(col as VARCHAR) = :bind.
--  * Re-runnable: deletes its own rows first (but never application -20000,
--    which other IDE pages hang off).

START TRANSACTION;

delete from field_source where id in (-20102);
delete from region_field where id in (-20002, -20103);
delete from field where id in (-20002, -20102);
delete from region_source where id in (-20000);
delete from source where id in (-20002, -20102);
delete from page_region where id in (-20000);
delete from region where id in (-20000);
delete from application_page where id in (-20002, -20102);
delete from page where id in (-20002, -20102);

-- The IDE application container. Other pages reference it, so only create it if missing.
insert into application (id, application_num, name)
select -20000, -20000, 'Summit - IDE'
 where not exists (select 1 from application where id = -20000);

-- Page definition
insert into page (id, template_id, "name")
values (-20002, -100, 'Application Pages Report');

-- Link page to internal application
insert into application_page (id, application_id, page_id, page_num)
values (-20002, -20000, -20002, 2);

-- Region definition (Report)
insert into region (id, template_id, "name", code_region_position, code_region_type, source_type_code)
values (-20000, -200, 'Pages in Application', 'body1', 'Report', 'dml_report');

-- Link region to page
insert into page_region (id, page_id, region_id, region_num)
values (-20000, -20002, -20000, 1);

-- Source for the report region
-- We filter by :applicationId which will be bound from the hidden field
insert into source (id, "source")
values (-20002, 'select p.id, p.name as "Page Name", ap.page_num as "Page Number" from application_page ap join page p on ap.page_id = p.id where ap.application_id = :applicationId order by ap.page_num');

-- Link source to region
insert into region_source (id, region_id, source_id)
values (-20000, -20000, -20002);

-- Hidden field to hold the application ID from request parameter
-- template -60 is 'Input Item - Hidden'
insert into field (id, template_id, "name", source_type_code, field_type_code, default_source_type_code)
values (-20002, -60, 'applicationId', 'static', 'NUMBER', null);

-- Link field to region
insert into region_field (id, region_id, field_id, field_num)
values (-20002, -20000, -20002, 1);

-- New Page for Creating a Page (stub - becomes the page create/edit form, see doc/ide-plan.md)
insert into page (id, template_id, "name")
values (-20102, -100, 'Create New Page');

-- Link page to internal application
insert into application_page (id, application_id, page_id, page_num)
values (-20102, -20000, -20102, 102);

-- Create Button on Page -20002
-- template -81 is 'Input Item - Javascript Button'
insert into field (id, template_id, "name", source_type_code, field_type_code, default_source_type_code)
values (-20102, -81, 'Create', 'static', 'SUBMIT', 'static');

-- Link Create button to region -20000
insert into region_field (id, region_id, field_id, field_num)
values (-20103, -20000, -20102, 2);

-- Source for the Javascript Button to redirect
-- We use location.href to navigate, passing the applicationId from the hidden field
-- in pageParams format so the target page's parameter map picks it up.
insert into source (id, "source")
values (-20102, 'location.href=''##__CONTEXTPATH__##/run/-20000/-20102?pageParams=applicationId:'' + document.getElementById(''-20002'').value;');

-- Link source to field
insert into field_source (id, field_id, source_id, flag_default_value)
values (-20102, -20102, -20102, 'Y');

COMMIT;
