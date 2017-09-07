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
insert into FIELD values (-10000, -61, 'code', 'static', 'TEXT', null, null);
insert into FIELD values (-10001, -61, 'description', 'static', 'TEXT', null, null);
insert into FIELD values (-10002, -62, 'sort_order', 'static', 'NUMBER', null, null);
insert into FIELD values (-10003, -61, 'source_identifier', 'static', 'TEXT', null, null);

-- Attach the fields to the region
insert into REGION_FIELD values (-10000, -10000, -10000, 1);
insert into REGION_FIELD values (-10001, -10000, -10001, 2);
insert into REGION_FIELD values (-10002, -10000, -10002, 3);
insert into REGION_FIELD values (-10003, -10000, -10003, 4);

commit;
