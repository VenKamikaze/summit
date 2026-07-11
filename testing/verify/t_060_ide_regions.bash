#!/bin/bash
# Verifies IDE page 5: the regions-on-page report (-23000) and the region
# create/edit form (-23100).
# Built by src/main/resources/sql/postgres/summitdev/summitdev-20260702-ide-p5-regions.sql
#
# Lifecycle: report render -> create-mode form render (dropdowns from TEMPLATE
# and CODE_* tables, textarea for the SQL source) -> Save (one dml_modify CTE
# INSERTs REGION + PAGE_REGION + SOURCE + REGION_SOURCE) -> the created report
# region renders on its page AND serves rows through the JSON API -> edit mode
# -> Update (one CTE updates REGION, PAGE_REGION and SOURCE) -> cleanup.
#
# The test creates its own scratch page to hang regions off, so no IDE page
# is polluted.

source "$(dirname "$0")/verify-lib.bash"
require_app

REPORT="/run/-20000/-23000"
FORM="/run/-20000/-23100"
TEST_NAME="ZZ Verify Region"

cleanup() {
  sql "delete from region_source where region_id in (select id from region where name like 'ZZ Verify Region%')" > /dev/null
  sql "delete from source where source like 'select 42 as id, %ZZ Verify%'" > /dev/null
  sql "delete from page_region where region_id in (select id from region where name like 'ZZ Verify Region%')" > /dev/null
  sql "delete from region where name like 'ZZ Verify Region%'" > /dev/null
  sql "delete from application_page where page_id in (select id from page where name = 'ZZ Verify Region Host Page')" > /dev/null
  sql "delete from page where name = 'ZZ Verify Region Host Page'" > /dev/null
}
cleanup
trap 'cleanup; rm -f "${_COOKIE_JAR}"' EXIT

# Scratch page to attach regions to (rendered later to prove the region works)
sql "with new_page as (insert into page (id, template_id, name) select nextval('page_seq'), -100, 'ZZ Verify Region Host Page' returning id) insert into application_page (id, application_id, page_id, page_num) select nextval('application_page_seq'), -20000, id, 999060 from new_page" > /dev/null
HOST_ID="$(sql "select id from page where name = 'ZZ Verify Region Host Page'")"

echo "Regions report renders for an existing page (applications list -21000)"
http_get "${REPORT}?pageParams=pageId:-21000"
assert_http_ok "GET regions report"
assert_contains "hidden pageId field populated from pageParams" \
  'name="pageId" id="-23000" value="-21000"'
assert_contains "row link targets the region form in edit mode" \
  'id="region--23000-sprt-link" value="/run/-20000/-23100?pageParams=id:"'
assert_contains "Create button passes id:0 + pageId in pageParams format" \
  "location.href='/run/-20000/-23100?pageParams=id:0,pageId:' + document.getElementById('-23000').value;"

echo "Regions report API lists the page's regions"
http_get_region_json "-23000" "pageId:-21000"
assert_http_ok "GET region JSON"
assert_json "first column is 'id'" '.header.cells[0].value' "id"
assert_json "applications list region listed" '.body[0].cells[1].value' "Applications"

echo "Create mode (pageParams=id:0,pageId:${HOST_ID})"
http_get "${FORM}?pageParams=id:0,pageId:${HOST_ID}"
assert_http_ok "GET form in create mode"
assert_contains "Save button shown" 'name="Save"'
assert_not_contains "Update button hidden" 'name="Update"'
assert_not_contains "Fields button hidden" 'name="Fields"'
assert_contains "pageId hidden field populated from pageParams" \
  "name=\"pageId\" id=\"-23101\" value=\"${HOST_ID}\""
assert_contains "template dropdown offers the report region template" \
  '<option  value="-200">Summit Standard Interactive Report Style 1</option>'
assert_contains "position dropdown from CODE_REGION_POSITION" \
  '<option  value="body1">'
assert_contains "type dropdown from CODE_REGION_TYPE" \
  '<option  value="Report">'
assert_contains "source type dropdown from CODE_SOURCE_TYPE" \
  '<option  value="dml_report">'
assert_contains "textarea for the region SQL source" \
  '<textarea name="source" id="-23108" rows="10" cols="80">'

echo "Edit mode for an existing region (the applications list report -21000)"
http_get "${FORM}?pageParams=id:-21000"
assert_http_ok "GET form in edit mode"
assert_contains "name populated" 'value="Applications"'
assert_contains "region_num populated" 'name="region_num" id="-23103" value="1"'
assert_contains "pageId derived from the region id (row link passes id only)" \
  'name="pageId" id="-23101" value="-21000"'
assert_contains "template pre-selected" 'selected="selected" value="-200"'
assert_contains "position pre-selected" 'selected="selected" value="body1"'
assert_contains "type pre-selected" 'selected="selected" value="Report"'
assert_contains "source type pre-selected" 'selected="selected" value="dml_report"'
assert_contains "region SQL shown in the textarea" \
  '>select id, application_num as "Application Number", name as "Application Name" from application order by name</textarea>'
assert_contains "Update button shown" 'name="Update"'
assert_contains "Fields button navigates to fields report with this region id" \
  "location.href='/run/-20000/-24000?pageParams=regionId:' + document.getElementById('-23100').value;"
assert_not_contains "Save button hidden" 'name="Save"'

echo "POST Save inserts REGION + PAGE_REGION + SOURCE + REGION_SOURCE via one CTE"
http_get "${FORM}?pageParams=id:0,pageId:${HOST_ID}"   # refresh CSRF token
http_post_form "${FORM}" \
  "__SUMMIT_FORM_ID__=form--23100" \
  "id=0" "pageId=${HOST_ID}" "name=${TEST_NAME}" "region_num=1" \
  "template_id=-200" "code_region_position=body1" "code_region_type=Report" \
  "source_type_code=dml_report" \
  "source=select 42 as id, 'ZZ Verify' as \"Verify Column\"" "Save=Save"
assert_eq "POST Save redirects" "302" "${RESPONSE_CODE}"
assert_eq "Save branches to the regions report for the host page" \
  "${SUMMIT_URL}/run/-20000/-23000?pageId=${HOST_ID}" "${REDIRECT_URL}"
assert_sql "region row created" "-200|body1|Report|dml_report" \
  "select template_id, code_region_position, code_region_type, source_type_code from region where name = '${TEST_NAME}'"
assert_sql "region id came from region_seq (positive)" "t" \
  "select id > 0 from region where name = '${TEST_NAME}'"
assert_sql "page_region row created in the same statement" "${HOST_ID}|1" \
  "select pr.page_id, pr.region_num from page_region pr join region r on pr.region_id = r.id where r.name = '${TEST_NAME}'"
assert_sql "source + region_source rows created in the same statement" \
  "select 42 as id, 'ZZ Verify' as \"Verify Column\"" \
  "select s.source from source s join region_source rs on rs.source_id = s.id join region r on r.id = rs.region_id where r.name = '${TEST_NAME}'"

echo "The redirect after Save renders"
http_get "${REDIRECT_URL#${SUMMIT_URL}}"
assert_http_ok "GET post-submit redirect target (regions report)"

NEW_ID="$(sql "select id from region where name = '${TEST_NAME}'")"

echo "The region the IDE just created actually works"
http_get "/run/-20000/${HOST_ID}"
assert_http_ok "GET the host page with the new region"
assert_contains "new report region scaffolding renders" "mustacheReportRegion-${NEW_ID}"
http_get_region_json "${NEW_ID}"
assert_http_ok "GET the new region's JSON API"
assert_json "new region serves its query" '.body[0].cells[1].value' "ZZ Verify"

echo "Edit mode for the new region"
http_get "${FORM}?pageParams=id:${NEW_ID}"
assert_http_ok "GET form for new region"
assert_contains "form populated with new region name" "value=\"${TEST_NAME}\""
assert_contains "Update button shown for new region" 'name="Update"'

echo "POST Update modifies REGION, PAGE_REGION and SOURCE via one CTE"
http_post_form "${FORM}" \
  "__SUMMIT_FORM_ID__=form--23100" \
  "id=${NEW_ID}" "pageId=${HOST_ID}" "name=${TEST_NAME} Renamed" "region_num=2" \
  "template_id=-200" "code_region_position=body1" "code_region_type=Report" \
  "source_type_code=dml_report" \
  "source=select 42 as id, 'ZZ Verify Renamed' as \"Verify Column\"" "Update=Update"
assert_eq "POST Update redirects" "302" "${RESPONSE_CODE}"
assert_eq "Update branches to the regions report for the host page" \
  "${SUMMIT_URL}/run/-20000/-23000?pageId=${HOST_ID}" "${REDIRECT_URL}"
assert_sql "region row updated" "${TEST_NAME} Renamed" \
  "select name from region where id = ${NEW_ID}"
assert_sql "page_region row updated in the same statement" "2" \
  "select region_num from page_region where region_id = ${NEW_ID}"
assert_sql "source row updated in the same statement" \
  "select 42 as id, 'ZZ Verify Renamed' as \"Verify Column\"" \
  "select s.source from source s join region_source rs on rs.source_id = s.id where rs.region_id = ${NEW_ID}"
assert_sql "still exactly one test region (INSERT was skipped on Update)" "1" \
  "select count(*) from region where name like 'ZZ Verify Region%'"

echo "Delete button gating (deletes are bottom-up: no fields allowed)"
http_get "${FORM}?pageParams=id:${NEW_ID}"
assert_contains "Delete button shown for a region with no fields" 'name="Delete"'
http_get "${FORM}?pageParams=id:-21000"
assert_not_contains "Delete button hidden for a region with fields" 'name="Delete"'

echo "POST Delete removes REGION + PAGE_REGION + SOURCE and branches to the report"
http_get "${FORM}?pageParams=id:${NEW_ID}"   # refresh CSRF token
http_post_form "${FORM}" \
  "__SUMMIT_FORM_ID__=form--23100" \
  "id=${NEW_ID}" "pageId=${HOST_ID}" "name=${TEST_NAME} Renamed" "region_num=2" \
  "template_id=-200" "code_region_position=body1" "code_region_type=Report" \
  "source_type_code=dml_report" "source=x" "Delete=Delete"
assert_eq "POST Delete redirects" "302" "${RESPONSE_CODE}"
assert_eq "Delete branches to the regions report for the host page" \
  "${SUMMIT_URL}/run/-20000/-23000?pageId=${HOST_ID}" "${REDIRECT_URL}"
assert_sql "region row deleted" "0" \
  "select count(*) from region where id = ${NEW_ID}"
assert_sql "page_region row deleted in the same statement" "0" \
  "select count(*) from page_region where region_id = ${NEW_ID}"
assert_sql "region's source deleted in the same statement" "0" \
  "select count(*) from source where source like 'select 42 as id, %ZZ Verify%'"

verify_summary
