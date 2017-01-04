--test_dml.sql

--unused at this time
--insert into applicatio_schemas values ();

set autocommit to off;

insert into application select nextval('application_seq'), 1, 'Test Application';

--insert into template select nextval('spare_seq'), 'Test Report Template', 'header', 'body', 'footer';

insert into template select nextval('spare_seq'), 'Static HTML for a table in a tabular report', 'org.awiki.kamikaze.summit.service.processor.result.SourceProcessorResultTable', '<table>##__DATA__##</table>';
insert into template select nextval('spare_seq'), id, 'Static HTML for HeaderRow in a tabular report', 'org.awiki.kamikaze.summit.service.processor.result.SourceProcessorResultTable.HeaderRow', '<tr class="tablerowheader">##__DATA__##</tr>', 'text/html' from template where class_name = 'org.awiki.kamikaze.summit.service.processor.result.SourceProcessorResultTable';
insert into template select nextval('spare_seq'), id, 'Static HTML for a data row in a tabular report', 'org.awiki.kamikaze.summit.service.processor.result.SourceProcessorResultTable.Row', '<tr class="tablerowdata">##__DATA__##</tr>', 'text/html' from template where class_name = 'org.awiki.kamikaze.summit.service.processor.result.SourceProcessorResultTable';
insert into template select nextval('spare_seq'), id, 'Static HTML for a cell in a tabular report', 'org.awiki.kamikaze.summit.service.processor.result.SourceProcessorResultTable.Cell', '<td class="tablecell">##__DATA__##</td>', 'text/html' from template where class_name = 'org.awiki.kamikaze.summit.service.processor.result.SourceProcessorResultTable.Row';
insert into template select nextval('spare_seq'), 'Basic Static HTML Page', 'org.awiki.kamikaze.summit.dto.entry.PageDto', '<!DOCTYPE html><html><body>##__DATA__##</body></html>';
insert into template select nextval('spare_seq'), 'Basic Static HTML Region','org.awiki.kamikaze.summit.dto.entry.RegionDto', '<div class="region">##__DATA__##</div>';
insert into template select nextval('spare_seq'), 'Basic Static HTML Field','org.awiki.kamikaze.summit.dto.entry.FieldDto', '<div class="field">##__DATA__##</div>';

insert into template select nextval('spare_seq'), 'HTML Page with Mustache Templating, style 1', 'org.awiki.kamikaze.summit.dto.entry.PageDto', '<!DOCTYPE html> <html> <head> <script src="##__CONTEXTPATH__##/js/mustache.js" type="text/javascript"></script> <script src="##__CONTEXTPATH__##/js/jQuery-1.11.1.js" type="text/javascript"></script> <script type="text/javascript" src="##__CONTEXTPATH__##/js/jquery-ui/jquery-ui.js"></script> <script type="text/javascript" src="##__CONTEXTPATH__##/js/summit-page.js"></script> <script type="text/javascript" src="##__CONTEXTPATH__##/js/summit-report.js"></script> <script type="text/javascript" src="##__CONTEXTPATH__##/js/summit-report-reportinstance.js"></script> <link type="text/css" rel="stylesheet" media="all" href="##__CONTEXTPATH__##/styles/main.css" /> <link type="text/css" rel="stylesheet" media="all" href="##__CONTEXTPATH__##/styles/jquery-ui.css" /> <script type="text/javascript"> /* initialise summit page variables. */ $( function() { Summit.Page.initialise("##__CONTEXTPATH__##", "##__APIPATH__##"); } ); </script> </head> <body class="thebody"> <div id="content"> <div class="main-menu" id="sidebar"></div> <div class="main-content" id="main">##__DATA__##</div> </div> </body> </html>';
insert into template select nextval('spare_seq'), 'HTML Region with Mustache Templating', 'org.awiki.kamikaze.summit.dto.entry.RegionDto', '<div class="region" id="mustacheReportRegion">##__DATA__##</div>';
insert into template select nextval('spare_seq'), 'HTML for a table in a tabular report with Mustache Templating', 'org.awiki.kamikaze.summit.service.processor.result.SourceProcessorResultTable', '<link type="text/css" rel="stylesheet" media="all" href="##__CONTEXTPATH__##/styles/table.css" /> <div style="clear: both;"></div> <div class="searchBar"> <div class="search"> <form id="searchForm-##__ID__##" action="##__APIPATH__##/filter/##__ID__##.json" method="post"> <div class="search-image">&nbsp;</div> <div class="fieldgroup"> <div class="field"><select class="filterType" id="filterType-##__ID__##" name="filterType"><option value="exact" selected="selected">Exact</option><option value="contains">Contains</option><option value="startsWith">Starts With</option><option value="endsWith">Ends With</option></select> <label for="search-##__ID__##">Search:</label><input name="search" type="text" id="searchInput-##__ID__##" /> <input name="page" type="hidden" id="searchPage-##__ID__##" value="1"/> <label class="rowsLabel" for="searchRows-##__ID__##">Rows:</label><select id="searchRows-##__ID__##" name="rows"><option value="5">5</option><option value="10">10</option><option selected="selected" value="15">15</option><option value="20">20</option><option value="30">30</option><option value="50">50</option><option value="100">100</option><option value="200">200</option><option value="500">500</option><option value="1000">1000</option><option value="5000">5000</option><option selected="selected" value="0">All</option></select> <div class="searchButton"> <input value="Go" name="action" type="submit" id="searchButton" /> <input value="Clear" name="action" type="submit" id="clearSearchButton" /> </div> </div> </div> </form> </div> </div> <div class="listContent"> <div class="region"> <div class="regionContent" id="mustacheReportRegion-##__ID__##">##__DATA__##</div> <div id="mustacheReportNav-##__ID__##" class="pagination"></div></div> <script type="text/javascript"> var searchUrl = "##__APIPATH__##/filter/##__ID__##.json"; var viewUrl = "##__CONTEXTPATH__##/run/1/3/"; /* initialise summit report, create and setup the report instance */ $(document).ready( function() { Summit.Report.initialise(); /* var template = Summit.Report.doTemplateRequest("##__ID__##"); */ /* Until we implement the line above, just use default template string by passing in null template */ var rInstance = Summit.Report.createNewReportInstance("##__ID__##", "searchForm-##__ID__##", null, viewUrl, searchUrl, true); $("#searchForm-##__ID__##").on("submit", function(event) { event.preventDefault(); Summit.Report.doRequest(rInstance); }); } ); $("#clearSearchButton").click(function() { $("#searchForm-##__ID__##").trigger("reset"); return true; }) </script> </div> ';

insert into page (select nextval('page_seq'), id, 'Test Report Page' from template where class_name = 'org.awiki.kamikaze.summit.dto.entry.PageDto' and description not like '%Mustache%');
insert into page (select nextval('page_seq'), id, 'Test Report Page with Mustache' from template where class_name = 'org.awiki.kamikaze.summit.dto.entry.PageDto' and description like '%Mustache%');

insert into application_page (select nextval('spare_seq'), id, (select id from page where name = 'Test Report Page'), 1 from application where name = 'Test Application');
insert into application_page select nextval('spare_seq'), 1, id, id from page where name like '%Mustache';

-- TODO review this, seems odd being the same code as source_type
insert into code_source_type values ('dml_report', 'text/plain; sql; dml; report', 1, 'dml_report');

-- TODO review this, seems odd being the same code as source_type
--insert into code_processing_type values ('dml_report', 'text/plain; sql; dml; report', 1, 'dml_report');

insert into code_region_type values ('Report', 'Report Region', 1, 'dml_report');

insert into code_region_position values ('body1', 'Top most region on template body', 3);

insert into region  (select nextval('region_seq'), id, 'Test Report Region', 'body1', 'Report', 'dml_report' from template where class_name = 'org.awiki.kamikaze.summit.service.processor.result.SourceProcessorResultTable' and description not like '%Mustache%');
insert into region  (select nextval('region_seq'), id, 'Test Report Region with Mustache', 'body1', 'Report', 'dml_report' from template where class_name = 'org.awiki.kamikaze.summit.service.processor.result.SourceProcessorResultTable' and description like '%Mustache%');

insert into page_region select nextval('spare_seq'), (select id from page where description like '%Mustache'), id, id from region where name like '%Mustache';
insert into page_region (select nextval('spare_seq'), (select id from page where name = 'Test Report Page'), id, 1 from region where name ='Test Report Region');

insert into source values (1, 'select id, page_id, region_id, region_num from page_region');

insert into region_source (select nextval('spare_seq'), id, 1 from region where name = 'Test Report Region with Mustache');
insert into region_source (select nextval('spare_seq'), id, 1 from region where name = 'Test Report Region');

commit;
