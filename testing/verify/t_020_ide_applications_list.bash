#!/bin/bash
# Verifies IDE page 1: the Applications list report (app -20000, page -21000).
# Built by src/main/resources/sql/postgres/summitdev/summitdev-20260702-ide-p1-applications.sql
#
# Checks:
#  - metadata is loaded and wired (application_page -> page -> region -> source)
#  - the page renders: report region, row-link hidden field, Create button
#  - the report JSON API returns every application with an 'id' column
#    (required by summit-report.js for row linking)

source "$(dirname "$0")/verify-lib.bash"
require_app

PAGE="/run/-20000/-21000"

echo "IDE metadata is wired up"
assert_sql "page -21000 is page 1 of IDE app -20000" "-21000|1" \
  "select page_id, page_num from application_page where application_id = -20000 and page_id = -21000"
assert_sql "report region linked to page with a dml_report source" "-21000|Report|dml_report|1" \
  "select r.id, r.code_region_type, r.source_type_code, count(rs.source_id)
     from page_region pr join region r on r.id = pr.region_id
     left join region_source rs on rs.region_id = r.id
    where pr.page_id = -21000 group by r.id, r.code_region_type, r.source_type_code"

echo "Applications list page renders"
http_get "${PAGE}"
assert_http_ok "GET applications list"
assert_contains "report region rendered" 'id="mustacheReportRegion--21000"'
assert_contains "row-link field points at application form page" \
  'id="region--21000-sprt-link" value="/run/-20000/-21100?pageParams=id:"'
assert_contains "Create button navigates to application form in create mode" \
  "onclick=\"location.href='/run/-20000/-21100?pageParams=id:0';\""

# summit-report.js replaces the entire contents of mustacheReportRegion-<id>
# with the AJAX-rendered table. Fields must therefore render in the separate
# regionFields-<id> div and the JS-owned div must be empty, or the fields get
# clobbered client-side (bug found 2026-07-02; fixed in templates -200/-102).
echo "Region fields are outside the JS-owned report div (clobber regression check)"
assert_contains "fields render in their own regionFields div" \
  '<div class="regionContent" id="regionFields--21000"><input type="hidden" name="applications-sprt-link"'
assert_contains "JS-owned report div is empty in static HTML" \
  '<div class="regionContent" id="mustacheReportRegion--21000"></div>'

echo "Report API returns the applications"
http_get_region_json "-21000"
assert_http_ok "GET region JSON"
assert_json "first column is 'id' (needed for row links)" '.header.cells[0].value' "id"
assert_json "row count matches application table" '.body | length' \
  "$(sql 'select count(*) from application')"
assert_json "IDE app itself is listed" \
  '[.body[].cells[2].value] | contains(["Summit - IDE"])' "true"

verify_summary
