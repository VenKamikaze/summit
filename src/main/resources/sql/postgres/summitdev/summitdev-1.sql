START TRANSACTION;

-- wip, won't run in order, dependency issues. clean up later 2019-10

delete from region_source where region_id in ( select region_id from page_region where page_id in ( select page_id from application_page where application_id in (-1)));
delete from region_field where region_id in ( select region_id from page_region where page_id in ( select page_id from application_page where application_id in (-1))); 
delete from source where id in ( select source_id from region_source where region_id in ( select region_id from page_region where page_id in ( select page_id from application_page where application_id in (-1))) );
delete from field where id in ( select field_id from region_field where region_id in (select region_id from page_region where page_id in ( select page_id from application_page where application_id in (-1))));
delete from page_region where page_id in ( select page_id from application_page where application_id in (-1));

--careful, next line clears out all orphans
delete from region where id not in ( select region_id from page_region );
delete from application_page where application_id in ( -1);

--careful, next line clears out all orphans
delete from page where page_id not in ( select page_id from application_page );
delete from application where id in ( -1);

commit;

START TRANSACTION;

-- Create the internal application that allows a summitdev to maintain pages.
insert into application (ID, APPLICATION_NUM, NAME) select -1, -1, 'Summit - Internal Application';

---------------------------------------------
-- Applications
---------------------------------------------

-- template id 8 is mustache report region
insert into page (ID, TEMPLATE_ID, NAME) (select -1, id, 'Summit Applications' from template where name like 'Summit Standard Page Style 1');

insert into application_page (ID, APPLICATION_ID, PAGE_ID, PAGE_NUM (select -1, id, (select id from page where name = 'Summit Applications'), -1 from application where name = 'Summit - Internal Application');

insert into region (ID, TEMPLATE_ID, NAME, CODE_REGION_POSITION, CODE_REGION_TYPE, SOURCE_TYPE_CODE) (select -1, id, 'Applications', 'body1', 'Report', 'dml_report' from template where class_name = 'org.awiki.kamikaze.summit.service.processor.result.SourceProcessorResultTable' and description like 'Summit Standard Interactive Report Style 1');

insert into region (select -2, id, 'Applications Buttons', 'body1', 'Form', 'static' from template where class_name = 'org.awiki.kamikaze.summit.dto.render.RegionDto' and description like 'Summit Stanard Region Style 1');

insert into page_region (ID, PAGE_ID, REGION_ID, REGION_NUM ) select -1, (select id from page where name like 'Summit Applications'), id, id from region where name like 'Applications';
insert into page_region (ID, PAGE_ID, REGION_ID, REGION_NUM ) select -2, (select id from page where name like 'Summit Applications'), id, id from region where name like 'Applications Buttons';

insert into source (ID, SOURCE) values (-1, 'select ID as "Application ID", APPLICATION_NUM as "Application Number", NAME as "Application Name" from application');



-- Show regions on page
insert into source values (-10, 'select r.name, crp.description region_position, crt.description region_type, cst.description source_type from region r inner join page_region pr on r.id = pr.region_id inner join     application_page ap on ap.page_id = pr.page_id left outer join code_region_position crp on r.CODE_REGION_POSITION = crp.code left outer join code_region_type crt on r.CODE_REGION_TYPE = crt.code left outer join CODE_SOURCE_TYPE cst on r.SOURCE_TYPE_CODE = cst.code where ap.page_id = :hiddenPageId and ap.application_id = :hiddenApplicationId');

-- Show fields on page
insert into source values (-11, 'select f.name, ftc.description, fcst.description source_type, r.name, rcrt.description region_type from field f inner join region_field rf on f.id = rf.field_id inner join region r on rf.region_id = r.id inner join page_region pr on r.id = pr.region_id inner join     application_page ap on ap.page_id = pr.page_id left outer join code_field_type ftc on f.FIELD_TYPE_CODE = ftc.code left outer join code_region_position crp on r.CODE_REGION_POSITION = crp.code left outer join code_region_type rcrt on r.CODE_REGION_TYPE = rcrt.code left outer join CODE_SOURCE_TYPE fcst on f.SOURCE_TYPE_CODE = fcst.code where ap.page_id = :hiddenPageId and ap.application_id = :hiddenApplicationId order by r.name, rf.field_num asc');

insert into region_source (select -10, id, -10 from region where name = 'Summit - Internal Edit - Regions Region');
insert into region_source (select -11, id, -11 from region where name = 'Summit - Internal Edit - Fields Region');

insert into FIELD values (-10, -60, 'hiddenPageId', 'static', 'NUMBER', null, null);
insert into FIELD values (-11, -60, 'hiddenApplicationId', 'static', 'NUMBER', null, null);
insert into FIELD values (-12, -50, 'region-table-sprt-link', 'static', 'TEXT', 'static', null); --Regions region
insert into FIELD values (-13, -50, 'region-table-sprt-link', 'static', 'TEXT', 'static', null); --Fields region

insert into REGION_FIELD values (-10, -10, -10, 1);
insert into REGION_FIELD values (-11, -10, -11, 2);
insert into REGION_FIELD values (-14, -10, -12, 3);

-- Need to duplicate hiddenPageId,hiddenApplicationId fields in each region that uses them to search due to current summit limitations.
insert into REGION_FIELD values (-12, -11, -10, 1);
insert into REGION_FIELD values (-13, -11, -11, 2);
insert into REGION_FIELD values (-15, -11, -13, 3);

insert into source values (-13, '##__CONTEXTPATH__##/run/-1/-101'); -- link regions region to page -101
insert into source values (-12, '##__CONTEXTPATH__##/run/-1/-100'); -- link fields region to page -100

insert into FIELD_SOURCE values (-12, -12, -12, 'Y');
insert into FIELD_SOURCE values (-13, -13, -13, 'Y');

commit;
