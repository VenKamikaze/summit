---------- tables - run as summit
create table CODE_SOURCE_TYPE
(
  CODE varchar2(10) primary key,
  DESCRIPTION varchar2(200) not null,
  SORT_ORDER number(19) not null,
  SOURCE_IDENTIFIER varchar2(200) not null unique
);


/*
 * Appears unused as of July 2017
create table CODE_FIELD_SOURCE_TYPE
(
  CODE varchar2(10) primary key,
  DESCRIPTION varchar2(200) not null,
  SORT_ORDER number(19) not null,
  SOURCE_TYPE_CODE varchar2(10) references CODE_SOURCE_TYPE(CODE)
);
*/

create table CODE_REGION_TYPE
(
  CODE varchar2(10) primary key,
  DESCRIPTION varchar2(200) not null,
  SORT_ORDER number(19) not null,
  SOURCE_TYPE_CODE varchar2(10) references CODE_SOURCE_TYPE(CODE)
);

create table CODE_REGION_POSITION
(
  CODE varchar2(10) primary key,
  DESCRIPTION varchar2(200) not null,
  SORT_ORDER number(19) not null
);

-- Used by page processing regions.
--Specifies both the TYPE of processing, e.g. clear session state, redirect to page, run DML
-- and also the type of source code expected for this particular processing (can be null).
create table CODE_PROCESSING_TYPE
(
  CODE varchar2(10) primary key,
  DESCRIPTION varchar2(200) not null,
  SORT_ORDER number(19) not null,
);

-- use compared to CODE_PROCESSING_TYPE?
--create table CODE_PROCESSING_SOURCE_TYPE
--(
--  CODE varchar2(10) primary key,
--  DESCRIPTION varchar2(200) not null,
--  SORT_ORDER number(19) not null,
--  SOURCE_TYPE_CODE varchar2(10) references CODE_SOURCE_TYPE(CODE)
--);

-- use?
create table CODE_FIELD_TYPE
(
  CODE varchar2(10) primary key,
  DESCRIPTION varchar2(200) not null,
  SORT_ORDER number(19) not null
);


------- Main tables

create table APPLICATION
(
  ID number(19) primary key,
  APPLICATION_NUM number(19) not null unique,
  NAME varchar2(200)
);

-- how do we handle setting up the appropriate datasources per schema in an app?
-- maybe limit to one schema per app at this time.
create table APPLICATION_SCHEMAS
(
  ID number(19) primary key,
  APPLICATION_ID number(19) not null references APPLICATION(ID),
  SCHEMA_NAME varchar2(30) not null
);


create table TEMPLATE
(
  ID number(19) primary key,
  PARENT_ID number(19) references TEMPLATE(ID),  -- used exclusively for items on a page that are sub-elements and do not have their own tables to represent them, e.g. the ROWS and CELLS within a table.
  DESCRIPTION varchar2(255),
  CLASS_NAME varchar2(4000) not null,
  -- WAS: SOURCE varchar2(32000),
  --SOURCE CLOB,
  SOURCE varchar2(4000), -- TODO: find a way to use CLOB in Oracle and TEXT in postgres - cannot find a portable way in hibernate to make this work.
  MIME_TYPE varchar2(255)  -- e.g. text/html, application/json, text/csv etc
);


/*
 Template instances:
  eg

  TEMPLATE_PAGE_INSTANCE (
  id,
  page_id references page.id
  template_id references template.id
  mimetype  -- e.g. text/html, application/json, text/csv etc
)


  TEMPLATE_REGION_INSTANCE (
  id,
  region_id references region.id
  template_id references template.id
  )


  TEMPLATE_FIELD_INSTANCE (
  id,
  field_id references field.id
  template_id references template.id
  )
*/


/*
 Template ideas:

 TEMPLATE (
  id,
  class_name,
  pre_element,
  post_element
)

 table itself, <table> </table>
 header row (needs its own class) <th> </th>
 row <tr></tr>
 cell <td></td>

 OR:
 TEMPLATE (
  id,
  class_name,
  source
)
 where source like:
 table itself, <table>#DATA#</table>
 header row (needs its own class) <th>#DATA#</th>
 row <tr>#DATA#</tr>
 cell <td>#DATA#</td>
  etc

*/

/* INSTANCES */
create table PAGE
(
  ID number(19) primary key,
  TEMPLATE_ID number(19) not null references TEMPLATE(ID),
  NAME varchar2(200)
);

-- so pages could be shared across applications.
create table APPLICATION_PAGE
(
  ID number(19) primary key,
  APPLICATION_ID number(19) not null references APPLICATION(ID),
  PAGE_ID number(19) not null references PAGE(ID),
  PAGE_NUM number(19) not null
);


-- e.g. actual source is stored in here
create table SOURCE
(
  ID number(19) primary key,
  --WAS: SOURCE varchar2(32000)
  -- SOURCE CLOB
  SOURCE varchar2(4000)  -- TODO: find a way to use CLOB in Oracle and TEXT in postgres - cannot find a portable way in hibernate to make this work.
);

-- These metadata tables allow us to know the types that we can cast Bind Variables as
--  when dealing with user entered queries. Note that it is illegal for a bind variable
--  to be a text type and containing alphabetical characters, when binding to a column
--  that is expecting a Number type.
create table SOURCE_METADATA
(
  ID number(19) primary key,
  SOURCE_ID number(19) not null references SOURCE(ID),
  DATE_CREATED date,
  LAST_UPDATED date,
  UPDATED_BY varchar2(200)
);

create table SOURCE_METADATA_COLS
(
  ID number(19) primary key,
  SOURCE_METADATA_ID number(19) not null references SOURCE_METADATA(ID),
  COLUMN_ORDER number(19) not null,
  COLUMN_NAME varchar2(200) not null, -- aliases and real columns
  COLUMN_TYPE number(19) not null
);

-- e.g. the request processing objects on a page.
create table PAGE_PROCESSING
(
  ID number(19) primary key,
  PAGE_ID number(19) not null references PAGE(ID),
  PROCESSING_TYPE_CODE varchar2(10) not null references CODE_PROCESSING_TYPE(CODE),
  PROCESSING_NUM number(19) not null
);

create table PAGE_PROCESSING_SOURCE
(
  ID number(19) primary key,
  PAGE_PROCESSING_ID number(19) not null references PAGE_PROCESSING(ID),
  SOURCE_ID number(19) not null references SOURCE(ID)
  SOURCE_TYPE_CODE varchar2(10) references CODE_SOURCE_TYPE(CODE)
);

-- Used when we have a page_processing_source type of 'dml_selrow'
--   This contains the field names we wish to select values into
-- Note: it is order based on index, e.g. index 0 is the first field selected by the corresponding pageProcessingSource.source
--   FIELD_NAME must corresponding with a field name on the associated page.
create table PAGE_PROCESSING_SOURCE_SELECT
(
  ID number(19) primary key,
  PAGE_PROCESSING_SOURCE_ID number(19) not null references PAGE_PROCESSING_SOURCE(ID),
  FIELD_INDEX number(19) not null,
  FIELD_NAME varchar2(200) not null
);

-- e.g. each field in a region
-- Seems like this should not need SOURCE as a field since it joins off with FIELD_SOURCE?
create table FIELD
(
  ID number(19) primary key,
  TEMPLATE_ID number(19) not null references TEMPLATE(ID),
  NAME varchar2(200),
  SOURCE_TYPE_CODE varchar2(10) references CODE_SOURCE_TYPE(CODE),
  FIELD_TYPE_CODE varchar2(10) references CODE_FIELD_TYPE(CODE),
  DEFAULT_SOURCE_TYPE_CODE varchar2(10) references CODE_SOURCE_TYPE(CODE),
  NOTES varchar2(4000)
);

-- TODO: this seems like the better method than including Source on FIELD and REGION
create table FIELD_SOURCE
(
  ID number(19) primary key,
  FIELD_ID number(19) not null references FIELD(ID),
  SOURCE_ID number(19) not null references SOURCE(ID),
  FLAG_DEFAULT_VALUE varchar2(1)
);

-- Regions may be a report region, hence needing source.
create table REGION
(
  ID number(19) primary key,
  TEMPLATE_ID number(19) not null references TEMPLATE(ID),
  NAME varchar2(1000),
  CODE_REGION_POSITION varchar2(10) references CODE_REGION_POSITION(CODE),
  CODE_REGION_TYPE varchar2(10) references CODE_REGION_TYPE(CODE),
  SOURCE_TYPE_CODE varchar2(10) references CODE_SOURCE_TYPE(CODE)
);

create table REGION_SOURCE
(
  ID number(19) primary key,
  REGION_ID number(19) not null references REGION(ID),
  SOURCE_ID number(19) not null references SOURCE(ID)
);

create table PAGE_REGION
(
  ID number(19) primary key,
  PAGE_ID number(19) not null references PAGE(ID),
  REGION_ID number(19) not null references REGION(ID),
  REGION_NUM number(19) not null
);


create table REGION_FIELD
(
  ID number(19) primary key,
  REGION_ID number(19) references REGION(ID) not null,
  FIELD_ID number(19) references FIELD(ID) not null,
  FIELD_NUM number(19) not null
);

-- Whether the linked items should be processed or not
create table CONDITIONAL
(
  ID number(19) primary key,
  SOURCE_ID number(19) not null references SOURCE(ID)
);

create table PAGE_PROCESSING_CONDITIONAL
(
  ID number(19) primary key,
  PAGE_PROCESSING_ID number(19) not null references PAGE_PROCESSING(ID),
  CONDITIONAL_ID number(19) not null references CONDITIONAL(ID)
);

create sequence application_seq start with 1;
create sequence page_seq start with 1;
create sequence region_seq start with 1;
create sequence field_seq start with 1;
create sequence spare_seq start with 1;

