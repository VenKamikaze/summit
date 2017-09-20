START TRANSACTION;

-- from now on,
-- < -10000 is used for testing.

delete from application_page where id <= -10000;
delete from application where id <= -10000;
delete from page_region where id <= -10000;
delete from page where id <= -10000;
delete from region_source where id <= -10000;
delete from region_field where id <= -10000;
delete from region where id <= -10000;
delete from source where id <= -10000;
delete from field where id <= -10000;
delete from template where id <= -10000;

commit;

START TRANSACTION;

insert into application select -10000, -10000, 'Summit - Test Application';

insert into page (select -10000, -100, 'Summit - Internal Edit - Form Page' from template where class_name = 'org.awiki.kamikaze.summit.dto.edit.EditPageDto');

insert into application_page (select -10000, id, (select id from page where name = 'Summit - Internal Edit - Form Page'), 1 from application where name = 'Summit - Test Application');

insert into region  (select -10000, id, 'Summit - Internal Edit - Edit CodeSourceType Form', 'body1', 'Form', 'static' from template where class_name = 'org.awiki.kamikaze.summit.dto.edit.EditRegionDto' and description like 'Summit - Internal Edit - Form Region');

insert into page_region select -10000, (select id from page where name like 'Summit - Internal Edit - Form Page'), id, id from region where name like 'Summit - Internal Edit - Edit CodeSourceType Form';

--Retrieving values on PAGE_PROCESSING on GET is below

-- Populate fields on page with this source
insert into source values (-10000, 'select CODE, DESCRIPTION, SORT_ORDER, SOURCE_IDENTIFIER from CODE_SOURCE_TYPE where CODE = :code');

-- Create the PageProcessing so it gets executed before regions are rendered, then link to the source above
insert into PAGE_PROCESSING (select -10000, id, 'RENDER_PG1', 1 from PAGE where name like 'Summit - Internal Edit - Form Page');
insert into PAGE_PROCESSING_SOURCE values (-10000, -10000, -10000, 'dml_selrow');
-- Link each fieldname to the resultset columns in the corresponding source by column index.
insert into PAGE_PROCESSING_SOURCE_SELECT values (-10000, -10000, 0, 'code');
insert into PAGE_PROCESSING_SOURCE_SELECT values (-10001, -10000, 1, 'description');
insert into PAGE_PROCESSING_SOURCE_SELECT values (-10002, -10000, 2, 'sort_order');
insert into PAGE_PROCESSING_SOURCE_SELECT values (-10003, -10000, 3, 'source_identifier');

-- Create the fields, make sure the names correspond with the names in PAGE_PROCESSING_SOURCE_SELECT or we won't get values populated.
-- not best approach? will put this in the template for the form instead. insert into FIELD values (-10000, -60, '__SUBMITTED_FORM_ID__', 'static', 'TEXT', null, null);
insert into FIELD values (-10001, -61, 'code', 'static', 'TEXT', null, null);
insert into FIELD values (-10002, -61, 'description', 'static', 'TEXT', null, null);
insert into FIELD values (-10003, -62, 'sort_order', 'static', 'NUMBER', null, null);
insert into FIELD values (-10004, -61, 'source_identifier', 'static', 'TEXT', null, null);
insert into FIELD values (-10005, -80, 'Save', 'static', 'SUBMIT', null, null);
insert into FIELD values (-10006, -80, 'Update', 'static', 'SUBMIT', null, null);

-- Attach the fields to the region
--insert into REGION_FIELD values (-10000, -10000, -10000, 1);
insert into REGION_FIELD values (-10001, -10000, -10001, 2);
insert into REGION_FIELD values (-10002, -10000, -10002, 3);
insert into REGION_FIELD values (-10003, -10000, -10003, 4);
insert into REGION_FIELD values (-10004, -10000, -10004, 5);
insert into REGION_FIELD values (-10005, -10000, -10005, 6);
insert into REGION_FIELD values (-10006, -10000, -10006, 7);

--Writing values using PAGE_PROCESSING on POST is below.

-- The source bind values must match the field names that the values are submitted as.
insert into source values (-10001, 'insert into CODE_SOURCE_TYPE values (:code, :description, CAST(:sort_order as NUMERIC), :source_identifier)'); -- TODO dynamically handle field types rather than needing a CAST

-- Create the PageProcessing so it gets executed on a POST, then link to the source above
insert into PAGE_PROCESSING (select -10001, id, 'POST1', 1 from PAGE where name like 'Summit - Internal Edit - Form Page');
insert into PAGE_PROCESSING_SOURCE values (-10001, -10001, -10001, 'dml_modify');

-- Conditional processing of insert above.
insert into source values (-10002, 'select ''true'' where :REQUEST = ''Save''');
insert into CONDITIONAL values (-10000, -10002, 'dml_selcel', 'TEXT_TRUE');
insert into PAGE_PROCESSING_CONDITIONAL values (-10000, -10001, -10000); -- links it to the PAGE_PROCESSING

-- Conditional display of submit button 'Save' (FIELD -10005).
--   If the code wanted does not exist, then display the 'Save' button
insert into source values (-10003, 'select 1 from CODE_SOURCE_TYPE where code = :code');
insert into CONDITIONAL values (-10001, -10003, 'dml_selcel', 'NOTEXISTS');
insert into FIELD_CONDITIONAL values (-10001, -10005, -10001);

-- Conditional display of submit button 'Update' (FIELD -10006)
--   If the code wanted does exist, then display the 'Update' button
insert into source values (-10004, 'select 1 from CODE_SOURCE_TYPE where code = :code');
insert into CONDITIONAL values (-10002, -10004, 'dml_selcel', 'EXISTS');
insert into FIELD_CONDITIONAL values (-10002, -10006, -10002);

commit;
