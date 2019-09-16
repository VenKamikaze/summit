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

-- used for selecting a recordset but not for report regions.
insert into CODE_SOURCE_TYPE
 values ('dml_select', 'text/plain; sql; dml; select', 6, 'dml_select');

-- used by reports only
insert into CODE_REGION_TYPE 
 values ('Report', 'Report Region', 1, 'dml_report');

insert into CODE_REGION_TYPE 
 values ('Form', 'Form Region', 2, 'static');

-- region position locations
insert into CODE_REGION_POSITION 
 values ('body1', 'Top most region on template body', 3);

insert into CODE_FIELD_TYPE
 values ('TEXT', 'Text Field', 1);
insert into CODE_FIELD_TYPE
 values ('NUMBER', 'Whole Number Field', 2);
insert into CODE_FIELD_TYPE
 values ('DROPDOWN', 'Drop-Down Field', 3);

-- Button type (still treated as a field)
insert into CODE_FIELD_TYPE
 values ('SUBMIT', 'Submit Button', 100);

insert into CODE_LABEL_TYPE
 values ('LEFT_OPT', 'Optional Field (Left)', 1);

insert into CODE_LABEL_TYPE
 values ('RIGHT_OPT', 'Optional Field (Right)', 2);

insert into CODE_LABEL_TYPE
 values ('LEFT_MAND', 'Mandatory Field (Left)', 3);

insert into CODE_LABEL_TYPE
 values ('RIGHT_MAND', 'Mandatory Field (Right)', 4);

-- Page RENDER processes. These execute on a page render.
insert into CODE_PROCESSING_TYPE
 values ('RENDER_PG1', 'Page Process On Page Render (before regions)', 1);

-- Page POST processes. These execute on a page POST/submit.
insert into CODE_PROCESSING_TYPE
 values ('POST1', 'Page Process On Page POST', 2);

-- Conditions that must evaluate to true for attached code to execute.
insert into CODE_CONDITIONAL_TYPE
 values ('TEXT_TRUE', 'Source returns "true" as text', 1);

insert into CODE_CONDITIONAL_TYPE
 values ('EXISTS', 'Source returns some value or record', 2);

insert into CODE_CONDITIONAL_TYPE
 values ('NOTEXISTS', 'Source returns no records', 3);

commit;
