START TRANSACTION;

insert into CODE_SOURCE_TYPE
 values ('static', 'text/plain', 2, 'static');

insert into CODE_FIELD_TYPE
 values ('TEXT', 'Text Field', 1);
insert into CODE_FIELD_TYPE
 values ('NUMBER', 'Whole Number Field', 2);
insert into CODE_FIELD_TYPE
 values ('DROPDOWN', 'Drop-Down Field', 3);

commit;
