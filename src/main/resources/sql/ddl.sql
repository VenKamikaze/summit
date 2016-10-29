
---------- tables - run as summit
create table CODE_SOURCE_TYPE
(
  CODE character varying(10) primary key,
  DESCRIPTION character varying(200) not null,
  SORT_ORDER bigint not null,
  SOURCE_IDENTIFIER character varying(200) not null unique
);


create table CODE_FIELD_SOURCE_TYPE
(
  CODE character varying(10) primary key,
  DESCRIPTION character varying(200) not null,
  SORT_ORDER bigint not null,
  SOURCE_TYPE_CODE character varying(10) references CODE_SOURCE_TYPE(CODE)
);

create table CODE_REGION_TYPE
(
  CODE character varying(10) primary key,
  DESCRIPTION character varying(200) not null,
  SORT_ORDER bigint not null,
  SOURCE_TYPE_CODE character varying(10) references CODE_SOURCE_TYPE(CODE)
);

create table CODE_REGION_POSITION
(
  CODE character varying(10) primary key,
  DESCRIPTION character varying(200) not null,
  SORT_ORDER bigint not null
);

-- Used by page processing regions.
--Specifies both the TYPE of processing, e.g. clear session state, redirect to page, run DML
-- and also the type of source code expected for this particular processing (can be null).
create table CODE_PROCESSING_TYPE
(
  CODE character varying(10) primary key,
  DESCRIPTION character varying(200) not null,
  SORT_ORDER bigint not null,
  SOURCE_TYPE_CODE character varying(10) references CODE_SOURCE_TYPE(CODE)
);

-- use compared to CODE_PROCESSING_TYPE?
--create table CODE_PROCESSING_SOURCE_TYPE
--(
--  CODE character varying(10) primary key,
--  DESCRIPTION character varying(200) not null,
--  SORT_ORDER bigint not null,
--  SOURCE_TYPE_CODE character varying(10) references CODE_SOURCE_TYPE(CODE)
--);

-- use?
create table CODE_FIELD_TYPE
(
  CODE character varying(10) primary key,
  DESCRIPTION character varying(200) not null,
  SORT_ORDER bigint not null
);


------- Main tables

create table APPLICATION
(
  ID bigint primary key,
  APPLICATION_NUM bigint not null unique,
  NAME character varying(200)
);

-- how do we handle setting up the appropriate datasources per schema in an app?
-- maybe limit to one schema per app at this time.
create table APPLICATION_SCHEMAS
(
  ID bigint primary key,
  APPLICATION_ID bigint not null references APPLICATION(ID),
  SCHEMA_NAME character varying(30) not null
);


create table TEMPLATE
(
  ID bigint primary key,
  NAME character varying(200) not null,
  HEADER_SOURCE character varying(32000),
  BODY_SOURCE character varying(32000),
  FOOTER_SOURCE character varying(32000)
);

create table TEMPLATE_FOR_LISTS
(
  ID bigint primary key,
  TEMPLATE_ID bigint not null references TEMPLATE(ID),
  NAME character varying(200) not null,
  HEADER_PRE character varying(32000),
  HEADER_POST character varying(32000),
  BODY_PRE character varying(32000),
  BODY_POST character varying(32000),
  FOOTER_PRE character varying(32000),
  FOOTER_POST character varying(32000)
);


create table PAGE
(
  ID bigint primary key,
  TEMPLATE_ID bigint not null references TEMPLATE(ID),
  NAME character varying(200)
);

-- so pages could be shared across applications.
create table APPLICATION_PAGE
(
  ID bigint primary key,
  APPLICATION_ID bigint not null references APPLICATION(ID),
  PAGE_ID bigint not null references PAGE(ID),
  PAGE_NUM bigint not null
);


-- e.g. actual source is stored in here
create table SOURCE
(
  ID bigint primary key,
  SOURCE character varying(32000)
);


-- e.g. the request processing objects on a page.
create table PAGE_PROCESSING
(
  ID bigint primary key,
  PAGE_ID bigint not null references PAGE(ID),
  PROCESSING_TYPE_CODE character varying(10) not null references CODE_PROCESSING_TYPE(CODE),
  PROCESSING_NUM bigint not null
  --SOURCE character varying(32000)
);

create table PAGE_PROCESSING_SOURCE
(
  ID bigint primary key,
  PAGE_PROCESSING_ID bigint not null references PAGE_PROCESSING(ID),
  SOURCE_ID bigint not null references SOURCE(ID)
);

-- e.g. each field in a region
-- Seems like this should not need SOURCE as a field since it joins off with FIELD_SOURCE?
create table FIELD
(
  ID bigint primary key,
  NAME character varying(200),
  SOURCE_TYPE_CODE character varying(10) references CODE_SOURCE_TYPE(CODE),
  FIELD_TYPE_CODE character varying(10) references CODE_FIELD_TYPE(CODE),
  DEFAULT_SOURCE_TYPE_CODE character varying(10) references CODE_SOURCE_TYPE(CODE),
  SOURCE character varying(32000),
  DEFAULT_SOURCE character varying(32000),
  NOTES character varying(4000)
);

-- TODO: this seems like the better method than including Source on FIELD and REGION
create table FIELD_SOURCE
(
  ID bigint primary key,
  FIELD_ID bigint not null references FIELD(ID),
  SOURCE_ID bigint not null references SOURCE(ID),
  FLAG_DEFAULT_VALUE character varying(1)
);

-- Regions may be a report region, hence needing source.
create table REGION
(
  ID bigint primary key,
  NAME character varying(1000),
  CODE_REGION_POSITION character varying(10) references CODE_REGION_POSITION(CODE),
  CODE_REGION_TYPE character varying(10) references CODE_REGION_TYPE(CODE),
  SOURCE_TYPE_CODE character varying(10) references CODE_SOURCE_TYPE(CODE)
  --SOURCE character varying(32000)
);

create table REGION_SOURCE
(
  ID bigint primary key,
  REGION_ID bigint not null references REGION(ID),
  SOURCE_ID bigint not null references SOURCE(ID)
);

create table PAGE_REGION
(
  ID bigint primary key,
  PAGE_ID bigint not null references PAGE(ID),
  REGION_ID bigint not null references REGION(ID),
  REGION_NUM bigint not null
);


create table REGION_FIELD
(
  ID bigint primary key,
  REGION_ID bigint references REGION(ID) not null,
  FIELD_ID bigint references FIELD(ID) not null,
  FIELD_NUM bigint not null
);

create sequence application_seq start 1;
create sequence page_seq start 1;
create sequence region_seq start 1;
create sequence field_seq start 1;
create sequence spare_seq start 1;


