---------- database
create tablespace SUMMIT datafile '/u02/oradata/summit01.dbf' size 10m autoextend on maxsize unlimited;

create user summit identified by summit_dev default tablespace SUMMIT;

grant
 create job,
 create session,
 create sequence,
 create table,
 create trigger,
 create procedure,
 create view
to SUMMIT;

alter user SUMMIT quota unlimited on SUMMIT;
