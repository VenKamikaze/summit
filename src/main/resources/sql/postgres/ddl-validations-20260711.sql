/* DDL delta 2026-07-11: page validations + process success messages.
 *
 * APEX-3.2-style validations: a VALIDATION row runs on POST after the submit
 * button is determined but BEFORE any POST1 processing. If any validation
 * fails, processing and branching are skipped and the page re-renders inline
 * with the error messages in the notification area (##__NOTIFICATION__## in
 * the page template) and the fields repopulated from the submitted values.
 *
 * Validation types (CODE_VALIDATION_TYPE):
 *   NOT_NULL  - the submitted value of FIELD_NAME must be non-empty (no source).
 *   TEXT_TRUE - SOURCE (VARCHAR binds from the submitted form) must return 'true'.
 *   EXISTS    - SOURCE must return a value / row.
 *   NOTEXISTS - SOURCE must return no value / rows.
 * VALIDATION_CONDITIONAL optionally gates when a validation runs (same
 * pattern as PAGE_PROCESSING_CONDITIONAL, typically on :REQUEST).
 *
 * PAGE_PROCESSING.SUCCESS_MESSAGE is the APEX "process success message":
 * collected from each POST1 processing that actually ran, stashed as a flash
 * attribute across the post-POST redirect, and rendered once in the
 * notification area of the target page.
 *
 * Re-runnable. ddl.sql contains the same DDL for fresh databases.
 */

create table if not exists CODE_VALIDATION_TYPE
(
  CODE character varying(10) primary key,
  DESCRIPTION character varying(200) not null,
  SORT_ORDER bigint not null
);

create table if not exists VALIDATION
(
  ID bigint primary key,
  PAGE_ID bigint not null references PAGE(ID),
  NAME character varying(200) not null,
  VALIDATION_NUM bigint not null,
  VALIDATION_TYPE_CODE character varying(10) not null references CODE_VALIDATION_TYPE(CODE),
  FIELD_NAME character varying(200),
  ERROR_MESSAGE character varying(4000) not null,
  SOURCE_ID bigint references SOURCE(ID),
  SOURCE_TYPE_CODE character varying(10) references CODE_SOURCE_TYPE(CODE)
);

create table if not exists VALIDATION_CONDITIONAL
(
  ID bigint primary key,
  VALIDATION_ID bigint not null references VALIDATION(ID),
  CONDITIONAL_ID bigint not null references CONDITIONAL(ID)
);

alter table PAGE_PROCESSING add column if not exists SUCCESS_MESSAGE character varying(4000);

create sequence if not exists validation_seq start 1;
create sequence if not exists validation_conditional_seq start 1;

insert into CODE_VALIDATION_TYPE
select 'NOT_NULL', 'Submitted field value must be present', 1
where not exists (select 1 from CODE_VALIDATION_TYPE where code = 'NOT_NULL');

insert into CODE_VALIDATION_TYPE
select 'TEXT_TRUE', 'Source must return "true" as text', 2
where not exists (select 1 from CODE_VALIDATION_TYPE where code = 'TEXT_TRUE');

insert into CODE_VALIDATION_TYPE
select 'EXISTS', 'Source must return some value or record', 3
where not exists (select 1 from CODE_VALIDATION_TYPE where code = 'EXISTS');

insert into CODE_VALIDATION_TYPE
select 'NOTEXISTS', 'Source must return no records', 4
where not exists (select 1 from CODE_VALIDATION_TYPE where code = 'NOTEXISTS');

-- Notification area for validation errors / success messages in the default
-- page template (fresh databases get this in setup-backend.sql).
update template
set source = replace(source,
  '<div class="main-content" id="main">##__DATA__##</div>',
  '<div class="main-content" id="main"><div class="notification" id="summit-notification">##__NOTIFICATION__##</div>##__DATA__##</div>')
where id = -100 and source not like '%##__NOTIFICATION__##%';
