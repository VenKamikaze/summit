/* Summit IDE (application -20000) - Page 5: Regions-on-page report + region
 * create/edit form.
 *
 * Report:  /run/-20000/-23000?pageParams=pageId:<pageId>
 *   Reached from the page form's (-20102) Regions button. Row click -> region
 *   form in edit mode (id:), Create button -> create mode (id:0,pageId:<id>).
 * Form:    /run/-20000/-23100?pageParams=id:0,pageId:<pageId>  (create)
 *          /run/-20000/-23100?pageParams=id:<regionId>         (edit)
 *
 * ID blocks: report -23000..-23099, form -23100..-23199. Re-runnable.
 *
 * Save creates REGION + PAGE_REGION + SOURCE + REGION_SOURCE in one
 * dml_modify data-modifying CTE (4 tables; the page form's 2-table CTE
 * pattern from summitdev-20260702-ide-p4-page-form.sql, extended). Update
 * updates REGION, PAGE_REGION.region_num and the linked SOURCE in one CTE.
 * All quoted literals that follow binds are sequence names (word-char start),
 * so the bind scraper sees every bind - see doc/ide-plan.md.
 *
 * First-pass limitations (documented, not bugs):
 *  - A SOURCE row is always created on Save, even when empty (e.g. for a
 *    static Form region that doesn't need one). Harmless: form region source
 *    processing is unimplemented ("Found a Form ! FIXME" noise).
 *  - Update only touches a SOURCE reachable via REGION_SOURCE; editing a
 *    hand-authored region that has no REGION_SOURCE link leaves the source
 *    field unsaved (0 rows updated).
 *  - Region type / source type are chosen independently; picking Report +
 *    static (or a report source whose binds have no matching fields) produces
 *    a region that won't render - same freedom (and rope) as hand-written SQL.
 */

START TRANSACTION;

-- Textarea field template for the multi-line SQL source. Owned by
-- setup-backend.sql on fresh databases; created here if missing so this file
-- can be applied to existing databases. Insert-if-missing rather than
-- delete-and-insert because fields outside this file may reference it later.
insert into template
select -64, null, 'Input Item - TextArea', 'org.awiki.kamikaze.summit.dto.render.FieldDto', '<div class="field">##__LABEL-LEFT__##<textarea name="##__NAME__##" id="##__ID__##" rows="10" cols="80">##__DATA__##</textarea>##__LABEL-RIGHT__##</div>'
 where not exists (select 1 from template where id = -64);

---------------------------------------------------------------------------
-- Deletes (children first), both pages
---------------------------------------------------------------------------

delete from field_conditional where id in (-23100, -23101, -23102);
delete from page_processing_conditional where id in (-23100, -23101);
delete from conditional where id in (-23100, -23101, -23102, -23103);
delete from page_processing_source_select where id in (-23100, -23101, -23102, -23103, -23104, -23105, -23106, -23107, -23108);
delete from page_processing_source where id in (-23100, -23101, -23102);
delete from page_processing where id in (-23100, -23101, -23102);
delete from field_label where id in (-23100, -23101, -23102, -23103, -23104, -23105, -23106);
delete from label where id in (-23100, -23101, -23102, -23103, -23104, -23105, -23106);
delete from field_source where id in (-23001, -23002, -23101, -23102, -23103, -23104, -23105);
delete from region_field where id in (-23000, -23001, -23002, -23100, -23101, -23102, -23103, -23104, -23105, -23106, -23107, -23108, -23109, -23110, -23111);
delete from field where id in (-23000, -23001, -23002, -23100, -23101, -23102, -23103, -23104, -23105, -23106, -23107, -23108, -23109, -23110, -23111);
delete from region_source where id in (-23000);
delete from source where id in (-23000, -23001, -23002, -23100, -23101, -23102, -23103, -23104, -23105, -23106, -23107, -23108, -23109, -23110);
delete from page_region where id in (-23000, -23100);
delete from region where id in (-23000, -23100);
delete from application_page where id in (-23000, -23100);
delete from page where id in (-23000, -23100);

-- The Save CTE generates ids from these sequences.
select setval('region_seq',        greatest((select coalesce(max(id), 1) from region), 1));
select setval('page_region_seq',   greatest((select coalesce(max(id), 1) from page_region), 1));
select setval('source_seq',        greatest((select coalesce(max(id), 1) from source), 1));
select setval('region_source_seq', greatest((select coalesce(max(id), 1) from region_source), 1));

---------------------------------------------------------------------------
-- Page -23000: Regions-on-page report
---------------------------------------------------------------------------

insert into page (id, template_id, "name")
values (-23000, -100, 'Page Regions Report');

insert into application_page (id, application_id, page_id, page_num)
values (-23000, -20000, -23000, 5);

insert into region (id, template_id, "name", code_region_position, code_region_type, source_type_code)
values (-23000, -200, 'Regions on Page', 'body1', 'Report', 'dml_report');

insert into page_region (id, page_id, region_id, region_num)
values (-23000, -23000, -23000, 1);

-- Report source: binds come from the region's FIELDS (pageId is a NUMBER
-- field -> numeric bind), so compare the numeric column directly.
-- First column MUST be aliased plain lowercase 'id' for row links.
insert into source (id, "source")
values (-23000, 'select r.id, r.name as "Region Name", pr.region_num as "Region Number", r.code_region_type as "Region Type" from page_region pr join region r on pr.region_id = r.id where pr.page_id = :pageId order by pr.region_num');

insert into region_source (id, region_id, source_id)
values (-23000, -23000, -23000);

-- Hidden field holding the page id from the request parameter
insert into field (id, template_id, "name", source_type_code, field_type_code, default_source_type_code)
values (-23000, -60, 'pageId', 'static', 'NUMBER', null);

insert into region_field (id, region_id, field_id, field_num)
values (-23000, -23000, -23000, 1);

-- Row link -> region form in edit mode (the form derives pageId from the
-- region id, so only id: is passed)
insert into field (id, template_id, "name", source_type_code, field_type_code, default_source_type_code)
values (-23001, -50, 'regions-sprt-link', 'static', 'TEXT', 'static');

insert into region_field (id, region_id, field_id, field_num)
values (-23001, -23000, -23001, 2);

insert into source (id, "source")
values (-23001, '##__CONTEXTPATH__##/run/-20000/-23100?pageParams=id:');

insert into field_source (id, field_id, source_id, flag_default_value)
values (-23001, -23001, -23001, 'Y');

-- Create button -> region form in create mode
insert into field (id, template_id, "name", source_type_code, field_type_code, default_source_type_code)
values (-23002, -81, 'Create', 'static', 'SUBMIT', 'static');

insert into region_field (id, region_id, field_id, field_num)
values (-23002, -23000, -23002, 3);

insert into source (id, "source")
values (-23002, 'location.href=''##__CONTEXTPATH__##/run/-20000/-23100?pageParams=id:0,pageId:'' + document.getElementById(''-23000'').value;');

insert into field_source (id, field_id, source_id, flag_default_value)
values (-23002, -23002, -23002, 'Y');

---------------------------------------------------------------------------
-- Page -23100: Region create/edit form
---------------------------------------------------------------------------

insert into page (id, template_id, "name")
values (-23100, -100, 'Region - Create / Edit');

insert into application_page (id, application_id, page_id, page_num)
values (-23100, -20000, -23100, 6);

insert into region (id, template_id, "name", code_region_position, code_region_type, source_type_code)
values (-23100, -103, 'Region Form', 'body1', 'Form', 'static');

insert into page_region (id, page_id, region_id, region_num)
values (-23100, -23100, -23100, 1);

-- GET: populate the form from REGION + PAGE_REGION + (optional) SOURCE.
-- coalesce keeps the textarea sane for regions without a REGION_SOURCE link.
-- The '' literal is safe here because it comes BEFORE the only bind (:id) -
-- the scraper trap is a quoted literal starting with a non-word char AFTER
-- a bind.
insert into source (id, "source")
values (-23100, 'select r.id, pr.page_id, r.name, pr.region_num, r.template_id, r.code_region_position, r.code_region_type, r.source_type_code, coalesce(s.source, '''') from region r join page_region pr on pr.region_id = r.id left join region_source rs on rs.region_id = r.id left join source s on s.id = rs.source_id where CAST(r.id as VARCHAR) = :id');

insert into page_processing (id, page_id, processing_type_code, processing_num)
values (-23100, -23100, 'RENDER_PG1', 1);

insert into page_processing_source (id, page_processing_id, source_id, source_type_code)
values (-23100, -23100, -23100, 'dml_selrow');

insert into page_processing_source_select (id, page_processing_source_id, field_index, field_name)
values (-23100, -23100, 0, 'id'),
       (-23101, -23100, 1, 'pageId'),
       (-23102, -23100, 2, 'name'),
       (-23103, -23100, 3, 'region_num'),
       (-23104, -23100, 4, 'template_id'),
       (-23105, -23100, 5, 'code_region_position'),
       (-23106, -23100, 6, 'code_region_type'),
       (-23107, -23100, 7, 'source_type_code'),
       (-23108, -23100, 8, 'source');

-- Fields
insert into field (id, template_id, "name", source_type_code, field_type_code, default_source_type_code) values
  (-23100, -60, 'id',                   'static',     'NUMBER',   null),
  (-23101, -60, 'pageId',               'static',     'NUMBER',   null),
  (-23102, -61, 'name',                 'static',     'TEXT',     null),
  (-23103, -62, 'region_num',           'static',     'NUMBER',   null),
  (-23104, -63, 'template_id',          'dml_select', 'DROPDOWN', null),
  (-23105, -63, 'code_region_position', 'dml_select', 'DROPDOWN', null),
  (-23106, -63, 'code_region_type',     'dml_select', 'DROPDOWN', null),
  (-23107, -63, 'source_type_code',     'dml_select', 'DROPDOWN', null),
  (-23108, -64, 'source',               'static',     'TEXT',     null),
  (-23109, -80, 'Save',                 'static',     'SUBMIT',   null),
  (-23110, -80, 'Update',               'static',     'SUBMIT',   null),
  (-23111, -81, 'Fields',               'static',     'SUBMIT',   'static');

insert into region_field (id, region_id, field_id, field_num) values
  (-23100, -23100, -23100, 1),
  (-23101, -23100, -23101, 2),
  (-23102, -23100, -23102, 3),
  (-23103, -23100, -23103, 4),
  (-23104, -23100, -23104, 5),
  (-23105, -23100, -23105, 6),
  (-23106, -23100, -23106, 7),
  (-23107, -23100, -23107, 8),
  (-23108, -23100, -23108, 9),
  (-23109, -23100, -23109, 10),
  (-23110, -23100, -23110, 11),
  (-23111, -23100, -23111, 12);

-- Labels
insert into label (id, template_id, label_type_code, text, notes) values
  (-23100, -1000, 'LEFT_OPT',  'Region Name',         'Human readable region name'),
  (-23101, -1000, 'LEFT_MAND', 'Region Number',       'Render order within the page'),
  (-23102, -1000, 'LEFT_MAND', 'Region Template',     'Report or form region template'),
  (-23103, -1000, 'LEFT_MAND', 'Position',            'Template position slot'),
  (-23104, -1000, 'LEFT_MAND', 'Region Type',         'Report / Form'),
  (-23105, -1000, 'LEFT_MAND', 'Source Type',         'How the region source is processed'),
  (-23106, -1000, 'LEFT_OPT',  'Region Source (SQL)', 'e.g. the report query; first column aliased id for row links');

insert into field_label (id, field_id, label_id) values
  (-23100, -23102, -23100),
  (-23101, -23103, -23101),
  (-23102, -23104, -23102),
  (-23103, -23105, -23103),
  (-23104, -23106, -23104),
  (-23105, -23107, -23105),
  (-23106, -23108, -23106);

-- Dropdown option sources (two columns: key, display value; bind-free SQL -
-- the dropdown processor passes no bind variables).
-- Region templates have no common class_name (-200 is SourceProcessorResultTable,
-- -103 is EditRegionDto), so list the two usable ones explicitly.
insert into source (id, "source") values
  (-23101, 'select id, description from template where id in (-200, -103) order by id desc'),
  (-23102, 'select code, description from code_region_position order by sort_order'),
  (-23103, 'select code, description from code_region_type order by sort_order'),
  (-23104, 'select code, description from code_source_type order by sort_order');

insert into field_source (id, field_id, source_id, flag_default_value) values
  (-23101, -23104, -23101, 'N'),
  (-23102, -23105, -23102, 'N'),
  (-23103, -23106, -23103, 'N'),
  (-23104, -23107, -23104, 'N');

-- Fields button: navigate to the fields-on-region report (IDE page 6) for
-- this region. Element id '-23100' is the hidden id field.
insert into source (id, "source")
values (-23110, 'location.href=''##__CONTEXTPATH__##/run/-20000/-24000?pageParams=regionId:'' + document.getElementById(''-23100'').value;');

insert into field_source (id, field_id, source_id, flag_default_value)
values (-23105, -23111, -23110, 'Y');

-- POST: Save = one CTE inserting REGION + PAGE_REGION + SOURCE + REGION_SOURCE,
--       Update = one CTE updating REGION, PAGE_REGION.region_num and SOURCE.
insert into source (id, "source") values
  (-23105, 'with new_source as (insert into source (id, source) select nextval(''source_seq''), :source returning id), new_region as (insert into region (id, template_id, name, code_region_position, code_region_type, source_type_code) select nextval(''region_seq''), CAST(:template_id as NUMERIC), :name, :code_region_position, :code_region_type, :source_type_code returning id), new_pr as (insert into page_region (id, page_id, region_id, region_num) select nextval(''page_region_seq''), CAST(:pageId as NUMERIC), nr.id, CAST(:region_num as NUMERIC) from new_region nr returning id) insert into region_source (id, region_id, source_id) select nextval(''region_source_seq''), nr.id, ns.id from new_region nr, new_source ns'),
  (-23106, 'with upd_region as (update region set template_id = CAST(:template_id as NUMERIC), name = :name, code_region_position = :code_region_position, code_region_type = :code_region_type, source_type_code = :source_type_code where CAST(id as VARCHAR) = :id returning id), upd_pr as (update page_region set region_num = CAST(:region_num as NUMERIC) where region_id in (select id from upd_region) returning id) update source set source = :source where id in (select rs.source_id from region_source rs join upd_region ur on rs.region_id = ur.id)');

insert into page_processing (id, page_id, processing_type_code, processing_num) values
  (-23101, -23100, 'POST1', 1),
  (-23102, -23100, 'POST1', 2);

insert into page_processing_source (id, page_processing_id, source_id, source_type_code) values
  (-23101, -23101, -23105, 'dml_modify'),
  (-23102, -23102, -23106, 'dml_modify');

insert into source (id, "source") values
  (-23107, 'select ''true'' where :REQUEST = ''Save'''),
  (-23108, 'select ''true'' where :REQUEST = ''Update''');

insert into conditional (id, source_id, source_type_code, conditional_type_code) values
  (-23100, -23107, 'dml_selcel', 'TEXT_TRUE'),
  (-23101, -23108, 'dml_selcel', 'TEXT_TRUE');

insert into page_processing_conditional (id, page_processing_id, conditional_id) values
  (-23100, -23101, -23100),
  (-23101, -23102, -23101);

-- Conditional button display: Save when creating, Update + Fields when editing.
insert into source (id, "source")
values (-23109, 'select 1 from region where id = :id');

insert into conditional (id, source_id, source_type_code, conditional_type_code) values
  (-23102, -23109, 'dml_selcel', 'NOTEXISTS'),
  (-23103, -23109, 'dml_selcel', 'EXISTS');

insert into field_conditional (id, field_id, conditional_id) values
  (-23100, -23109, -23102),  -- Save   : shown when the region does not exist
  (-23101, -23110, -23103),  -- Update : shown when the region exists
  (-23102, -23111, -23103);  -- Fields : shown when the region exists

COMMIT;
