/* DDL delta 2026-07-02: sequences for the link/child metadata tables.
 *
 * application_seq / page_seq / region_seq / field_seq have existed since the
 * original ddl.sql, but the link and child tables had none, so SQL running
 * inside the app (e.g. the Summit IDE's dml_modify sources) could not
 * generate ids for application_page, source, etc. Needed from IDE page 4
 * (page create/edit form) onwards -- see doc/ide-plan.md.
 *
 * Re-runnable. The setvals push each sequence past any existing rows;
 * hand-authored metadata ids are negative, but test data has positive ids,
 * so seed from max(id) with a floor of 1.
 *
 * ddl.sql contains the same creates for fresh databases; this file brings
 * existing databases up to date.
 */

create sequence if not exists application_page_seq start 1;
create sequence if not exists source_seq start 1;
create sequence if not exists page_region_seq start 1;
create sequence if not exists region_field_seq start 1;
create sequence if not exists region_source_seq start 1;
create sequence if not exists field_source_seq start 1;
create sequence if not exists label_seq start 1;
create sequence if not exists field_label_seq start 1;

select setval('application_page_seq', greatest((select coalesce(max(id), 1) from application_page), 1));
select setval('source_seq',           greatest((select coalesce(max(id), 1) from source), 1));
select setval('page_region_seq',      greatest((select coalesce(max(id), 1) from page_region), 1));
select setval('region_field_seq',     greatest((select coalesce(max(id), 1) from region_field), 1));
select setval('region_source_seq',    greatest((select coalesce(max(id), 1) from region_source), 1));
select setval('field_source_seq',     greatest((select coalesce(max(id), 1) from field_source), 1));
select setval('label_seq',            greatest((select coalesce(max(id), 1) from label), 1));
select setval('field_label_seq',      greatest((select coalesce(max(id), 1) from field_label), 1));
