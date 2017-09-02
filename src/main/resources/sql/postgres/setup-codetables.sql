START TRANSACTION;

-- used by reports only
insert into CODE_SOURCE_TYPE 
 values ('dml_report', 'text/plain; sql; dml; report', 1, 'dml_report');

insert into CODE_SOURCE_TYPE
 values ('static', 'text/plain', 2, 'static');

-- page processing, for selecting a single cell, not a row. 
insert into CODE_SOURCE_TYPE
 values ('dml_selcel', 'text/plain; sql; dml; select; cell', 3, 'dml_selcel');
-- page processing, used for update/insert/delete actions.
insert into CODE_SOURCE_TYPE
 values ('dml_modify', 'text/plain; sql; dml; modify', 4, 'dml_modify');
-- page processing, for selecting a single row. 
insert into CODE_SOURCE_TYPE
 values ('dml_selrow', 'text/plain; sql; dml; select; row', 5, 'dml_selrow');


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
