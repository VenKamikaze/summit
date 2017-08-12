START TRANSACTION;

-- used by reports only
insert into CODE_SOURCE_TYPE 
 values ('dml_report', 'text/plain; sql; dml; report', 1, 'dml_report');

insert into CODE_SOURCE_TYPE
 values ('static', 'text/plain', 2, 'static');

-- page processing, but could be implemented for fields too?
insert into CODE_SOURCE_TYPE
 values ('dml_select', 'text/plain; sql; dml; select', 3, 'dml_select');

-- used by reports only
insert into CODE_REGION_TYPE 
 values ('Report', 'Report Region', 1, 'dml_report');

-- region position locations
insert into CODE_REGION_POSITION 
 values ('body1', 'Top most region on template body', 3);

insert into CODE_FIELD_TYPE
 values ('TEXT', 'Text Field', 1);
insert into CODE_FIELD_TYPE
 values ('NUMBER', 'Whole Number Field', 2);
insert into CODE_FIELD_TYPE
 values ('DROPDOWN', 'Drop-Down Field', 3);

insert into CODE_PROCESSING_TYPE
 values ('RENDER_PG1', 'Page Process On Page Render (before regions)', 1);

commit;
