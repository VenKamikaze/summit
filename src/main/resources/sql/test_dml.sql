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

insert into template select nextval('spare_seq'), 'HTML Page with Mustache Templating, style 1', 'org.awiki.kamikaze.summit.dto.entry.PageDto', '<!DOCTYPE html><html><head><script src="/summit/js/mustache.js" type="text/javascript"></script><script src="/summit/js/jQuery-1.11.1.js" type="text/javascript"></script></head><body>##__DATA__##</body></html>';
insert into template select nextval('spare_seq'), 'HTML Region with Mustache Templating', 'org.awiki.kamikaze.summit.dto.entry.RegionDto', '<div class="region" id="mustacheReportRegion">##__DATA__##</div>';
insert into template select nextval('spare_seq'), 'HTML for a table in a tabular report with Mustache Templating', 'org.awiki.kamikaze.summit.service.processor.result.SourceProcessorResultTable', '<div class="region" id="mustacheReportRegion-##__ID__##">##__DATA__##</div><script type="text/javascript">function doReport() { var template="<table id=\"mustache_table\"><thead>{{#header}}<tr class=\"tablerowheader\">{{#cells}}<td class=\"tablecell\">{{value}}</td>{{/cells}}</tr>{{/header}}</thead><tbody>{{#body}}<tr class=\"tablerowdata\">{{#cells}}<td class=\"tablecell\">{{value}}</td>{{/cells}}</tr>{{/body}}</tbody></table>"; var data = $.ajax({url: "/summit/api/filter/##__ID__##.json", cache:false , success: function(response){ var html = Mustache.to_html(template, response); $("#mustacheReportRegion-##__ID__##").html(html); }} ); }; $( document ).ready(doReport()); </script>';

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
