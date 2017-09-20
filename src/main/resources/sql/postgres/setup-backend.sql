START TRANSACTION;

delete from application_page where id < 0;
delete from application where id < 0;
delete from page_region where id < 0;
delete from page where id < 0;
delete from region_source where id < 0;
delete from region_field where id < 0;
delete from region where id < 0;
delete from source where id < 0;
delete from field where id < 0;
delete from template where id < 0;

commit;

START TRANSACTION;

insert into application select -1, -1, 'Summit - Internal Application';

insert into template select -60, null, 'Input Item - Hidden', 'org.awiki.kamikaze.summit.dto.render.FieldDto', '<input type="hidden" name="##__NAME__##" id="##__ID__##" value="##__DATA__##" />';
insert into template select -61, null, 'Input Item - Text', 'org.awiki.kamikaze.summit.dto.render.FieldDto', '<div class="field"><input type="text" name="##__NAME__##" id="##__ID__##" value="##__DATA__##" /></div>';
insert into template select -62, null, 'Input Item - Number', 'org.awiki.kamikaze.summit.dto.render.FieldDto', '<div class="field"><input type="number" name="##__NAME__##" id="##__ID__##" value="##__DATA__##" /></div>'; -- HTML5 and IE10+ only.

-- Numbers -89 through -80 are reserved for button items.
insert into template select -80, null, 'Input Item - Submit', 'org.awiki.kamikaze.summit.dto.render.FieldDto', '<div class="field"><input type="submit" name="__summitaction__" id="##__ID__##" value="##__NAME__##" onclick="Summit.Page.submit(this);" /></div>';
insert into template select -81, null, 'Input Item - Javascript Button', 'org.awiki.kamikaze.summit.dto.render.FieldDto', '<div class="field"><input type="button" name="##__NAME__##" id="##__ID__##" value="##__NAME__##" onclick="##__DATA__##" /></div>';

insert into template select -100, null, 'Summit - Internal Edit - Page', 'org.awiki.kamikaze.summit.dto.edit.EditPageDto', '<!DOCTYPE html> <html> <head> <script src="##__CONTEXTPATH__##/js/mustache.js" type="text/javascript"></script> <script src="##__CONTEXTPATH__##/js/jQuery-1.11.1.js" type="text/javascript"></script> <script type="text/javascript" src="##__CONTEXTPATH__##/js/jquery-ui/jquery-ui.js"></script> <script type="text/javascript" src="##__CONTEXTPATH__##/js/summit-page.js"></script> <script type="text/javascript" src="##__CONTEXTPATH__##/js/summit-report.js"></script> <script type="text/javascript" src="##__CONTEXTPATH__##/js/summit-report-reportinstance.js"></script> <script type="text/javascript" src="##__CONTEXTPATH__##/js/summit-form.js"></script> <script type="text/javascript" src="##__CONTEXTPATH__##/js/summit-form-forminstance.js"></script> <link type="text/css" rel="stylesheet" media="all" href="##__CONTEXTPATH__##/styles/main.css" /> <link type="text/css" rel="stylesheet" media="all" href="##__CONTEXTPATH__##/styles/jquery-ui.css" /> <script type="text/javascript"> /* initialise summit page variables. */ $( function() { Summit.Page.initialise("##__CONTEXTPATH__##", "##__APIPATH__##"); } ); </script> </head> <body class="thebody"> <div id="content"> <div class="main-menu" id="sidebar"></div> <div class="main-content" id="main">##__DATA__##</div> </div> </body> </html>';
insert into template select -101, null, 'Summit - Internal Edit - Region', 'org.awiki.kamikaze.summit.dto.edit.EditRegionDto', '<div class="region" id="mustacheReportRegion">##__DATA__##</div>';
insert into template select -102, null, 'Summit - Internal Edit - Table', 'org.awiki.kamikaze.summit.service.processor.result.SourceProcessorResultTable', '<link type="text/css" rel="stylesheet" media="all" href="##__CONTEXTPATH__##/styles/table.css" /> <div style="clear: both;"></div> <div class="listContent"> <div class="region"> <div class="regionContent" id="mustacheReportRegion-##__REGION-ID__##">##__DATA__##</div> <div id="mustacheReportNav-##__REGION-ID__##" class="pagination"></div></div> <script type="text/javascript"> /* initialise summit report, create and setup the report instance */ $(document).ready( function() { Summit.Report.initialise(); /* var template = Summit.Report.doTemplateRequest("##__REGION-ID__##"); */ /* Until we implement the line above, just use default template string by passing in null template */ var templateHtml = null; var formId = null; var searchUrl = "/summit/api/filter/##__REGION-ID__##.json"; var viewUrl = "##__CONTEXTPATH__##/run/1/3/"; var rInstance = Summit.Report.createNewReportInstance("##__REGION-ID__##", formId, templateHtml, viewUrl, searchUrl, true); }); </script> </div> ';

insert into template select -103, null, 'Summit - Internal Edit - Form Region', 'org.awiki.kamikaze.summit.dto.edit.EditRegionDto', '<link type="text/css" rel="stylesheet" media="all" href="##__CONTEXTPATH__##/styles/form.css" /> <div style="clear: both;"></div><div class="region" id="formRegion-##__REGION-ID__##"><form action="#" method="post" id="form-##__REGION-ID__##"><input type="hidden" id="form-##__REGION-ID__##" name="__SUMMIT_FORM_ID__" value="form-##__REGION-ID__##" />##__DATA__##</form></div><script type="text/javascript"> /* initialise summit form, create and setup form instance */ $(document).ready( function() { Summit.Form.initialise(); var formId = "form-##__REGION-ID__##"; var templateHtml = null; var fInstance = Summit.Form.createNewFormInstance("##__REGION-ID__##", formId, templateHtml); }); </script>';

insert into page (select -10, -100, 'Summit - Internal Edit - Page' from template where class_name = 'org.awiki.kamikaze.summit.dto.edit.EditPageDto');

insert into application_page (select -10, id, (select id from page where name = 'Summit - Internal Edit - Page'), -10 from application where name = 'Summit - Internal Application');

insert into region  (select -10, id, 'Summit - Internal Edit - Regions Region', 'body1', 'Report', 'dml_report' from template where class_name = 'org.awiki.kamikaze.summit.service.processor.result.SourceProcessorResultTable' and description like 'Summit - Internal Edit - Table');
insert into region  (select -11, id, 'Summit - Internal Edit - Fields Region', 'body1', 'Report', 'dml_report' from template where class_name = 'org.awiki.kamikaze.summit.service.processor.result.SourceProcessorResultTable' and description like 'Summit - Internal Edit - Table');

insert into page_region select -10, (select id from page where name like 'Summit - Internal Edit - Page'), id, id from region where name like 'Summit - Internal Edit - Regions Region';
insert into page_region select -11, (select id from page where name like 'Summit - Internal Edit - Page'), id, id from region where name like 'Summit - Internal Edit - Fields Region';

-- Show regions on page
insert into source values (-10, 'select r.name, crp.description region_position, crt.description region_type, cst.description source_type from region r inner join page_region pr on r.id = pr.region_id inner join     application_page ap on ap.page_id = pr.page_id left outer join code_region_position crp on r.CODE_REGION_POSITION = crp.code left outer join code_region_type crt on r.CODE_REGION_TYPE = crt.code left outer join CODE_SOURCE_TYPE cst on r.SOURCE_TYPE_CODE = cst.code where ap.page_id = :hiddenPageId and ap.application_id = :hiddenApplicationId');

-- Show fields on page
insert into source values (-11, 'select f.name, ftc.description, fcst.description source_type, r.name, rcrt.description region_type from field f inner join region_field rf on f.id = rf.field_id inner join region r on rf.region_id = r.id inner join page_region pr on r.id = pr.region_id inner join     application_page ap on ap.page_id = pr.page_id left outer join code_field_type ftc on f.FIELD_TYPE_CODE = ftc.code left outer join code_region_position crp on r.CODE_REGION_POSITION = crp.code left outer join code_region_type rcrt on r.CODE_REGION_TYPE = rcrt.code left outer join CODE_SOURCE_TYPE fcst on f.SOURCE_TYPE_CODE = fcst.code where ap.page_id = :hiddenPageId and ap.application_id = :hiddenApplicationId order by r.name, rf.field_num asc');

insert into region_source (select -10, id, -10 from region where name = 'Summit - Internal Edit - Regions Region');
insert into region_source (select -11, id, -11 from region where name = 'Summit - Internal Edit - Fields Region');

insert into FIELD values (-10, -60, 'hiddenPageId', 'static', 'NUMBER', null, null);
insert into FIELD values (-11, -60, 'hiddenApplicationId', 'static', 'NUMBER', null, null);

insert into REGION_FIELD values (-10, -10, -10, 1);
insert into REGION_FIELD values (-11, -10, -11, 2);

-- Need to duplicate hiddenPageId,hiddenApplicationId fields in each region that uses them to search due to current summit limitations.
insert into REGION_FIELD values (-12, -11, -10, 1);
insert into REGION_FIELD values (-13, -11, -11, 2);


commit;
