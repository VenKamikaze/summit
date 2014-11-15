---------- database
create role summit WITH LOGIN;

create tablespace SUMMIT OWNER summit LOCATION '/var/lib/postgres/tablespaces/summit';

create database summit OWNER summit tablespace SUMMIT;
