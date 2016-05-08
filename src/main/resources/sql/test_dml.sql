--test_dml.sql

--unused at this time
--insert into applicatio_schemas values ();

set autocommit to off;

insert into application select nextval('application_seq'), 1, 'Test Application';

insert into template select nextval('spare_seq'), 'Test Report Template', 'header', 'body', 'footer';

insert into page (select nextval('page_seq'), id, 'Test Report Page' from template where name = 'Test Report Template');

insert into application_page (select nextval('spare_seq'), id, (select id from page where name = 'Test Report Page'), 1 from application where name = 'Test Application');

-- TODO review this, seems odd being the same code as source_type
insert into code_source_type values ('dml_report', 'text/plain; sql; dml; report', 1, 'dml_report');

-- TODO review this, seems odd being the same code as source_type
--insert into code_processing_type values ('dml_report', 'text/plain; sql; dml; report', 1, 'dml_report');

insert into code_region_type values ('Report', 'Report Region', 1, 'dml_report');

insert into code_region_position values ('body1', 'Top most region on template body', 3);

insert into region  (select nextval('region_seq'), 'Test Report Region', 'body1', 'Report', 'text/plain; sql; dml; report');

insert into page_region (select nextval('spare_seq'), (select id from page where name = 'Test Report Page'), id, 1 from region where name ='Test Report Region');

insert into source values (1, 'select id, page_id, region_id, region_num from page_region');

insert into region_source (select nextval('spare_seq'), id, 1 from region where name = 'Test Report Region');

commit;