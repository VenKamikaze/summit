#!/bin/bash
# Verifies IDE page 4: the page create/edit form (app -20000, page -20102).
# Built by src/main/resources/sql/postgres/summitdev/summitdev-20260702-ide-p4-page-form.sql
#
# Covers the full lifecycle: create-mode render (template dropdown sourced from
# TEMPLATE, Save button only) -> Save (a single dml_modify data-modifying CTE
# INSERTs PAGE + APPLICATION_PAGE with sequence-generated ids) -> post-submit
# redirect renders -> the created page itself renders -> edit-mode render
# (fields populated from the PAGE/APPLICATION_PAGE join, dropdown pre-selected)
# -> Update (CTE updates both tables) -> cleanup.

source "$(dirname "$0")/verify-lib.bash"
require_app

PAGE="/run/-20000/-20102"
TEST_NAME="ZZ Verify Page"
TEST_NUM="999001"

cleanup() {
  sql "delete from application_page where page_id in (select id from page where name like 'ZZ Verify Page%')" > /dev/null
  sql "delete from page where name like 'ZZ Verify Page%'" > /dev/null
}
cleanup
trap 'cleanup; rm -f "${_COOKIE_JAR}"' EXIT

echo "Create mode (pageParams=id:0,applicationId:-20000)"
http_get "${PAGE}?pageParams=id:0,applicationId:-20000"
assert_http_ok "GET form in create mode"
assert_contains "Save button shown" 'name="Save"'
assert_not_contains "Update button hidden" 'name="Update"'
assert_not_contains "Regions button hidden" 'name="Regions"'
assert_contains "applicationId hidden field populated from pageParams" \
  'name="applicationId" id="-22001" value="-20000"'
assert_contains "Page Name label renders" '>Page Name</label>'
assert_contains "Page Number label renders" '>Page Number</label>'
assert_contains "Page Template label renders" '>Page Template</label>'
assert_contains "template dropdown sourced from TEMPLATE table" \
  '<select name="template_id" id="-22004"><option  value="-100">Summit Standard Page Style 1</option></select>'

echo "Edit mode for an existing page (the IDE applications list -21000)"
http_get "${PAGE}?pageParams=id:-21000"
assert_http_ok "GET form in edit mode"
assert_contains "name populated from the PAGE/APPLICATION_PAGE join" 'value="Applications"'
assert_contains "page_num populated" 'name="page_num" id="-22003" value="1"'
assert_contains "applicationId derived from the page id (row link passes id only)" \
  'name="applicationId" id="-22001" value="-20000"'
assert_contains "page template pre-selected in the dropdown" \
  '<option selected="selected" value="-100">Summit Standard Page Style 1</option>'
assert_contains "Update button shown" 'name="Update"'
assert_contains "Regions button navigates to regions report with this page id" \
  "location.href='/run/-20000/-23000?pageParams=pageId:' + document.getElementById('-22000').value;"
assert_not_contains "Save button hidden" 'name="Save"'

echo "POST Save inserts PAGE + APPLICATION_PAGE via one data-modifying CTE"
http_get "${PAGE}?pageParams=id:0,applicationId:-20000"   # refresh CSRF token
http_post_form "${PAGE}" \
  "__SUMMIT_FORM_ID__=form--22000" \
  "id=0" "applicationId=-20000" "name=${TEST_NAME}" "page_num=${TEST_NUM}" \
  "template_id=-100" "Save=Save"
assert_eq "POST Save redirects" "302" "${RESPONSE_CODE}"
assert_eq "Save branches to the pages report for this application" \
  "${SUMMIT_URL}/run/-20000/-20002?applicationId=-20000" "${REDIRECT_URL}"
assert_sql "page row created with the chosen template" "${TEST_NAME}|-100" \
  "select name, template_id from page where name = '${TEST_NAME}'"
assert_sql "page id came from page_seq (positive, not hand-picked)" "t" \
  "select id > 0 from page where name = '${TEST_NAME}'"
assert_sql "application_page row created in the same statement" "-20000|${TEST_NUM}" \
  "select ap.application_id, ap.page_num from application_page ap join page p on ap.page_id = p.id where p.name = '${TEST_NAME}'"
assert_sql "application_page id came from application_page_seq" "t" \
  "select ap.id > 0 from application_page ap join page p on ap.page_id = p.id where p.name = '${TEST_NAME}'"

echo "The redirect after Save renders"
http_get "${REDIRECT_URL#${SUMMIT_URL}}"
assert_http_ok "GET post-submit redirect target (pages report)"

NEW_ID="$(sql "select id from page where name = '${TEST_NAME}'")"

echo "The page the IDE just created actually renders"
http_get "/run/-20000/${NEW_ID}"
assert_http_ok "GET the newly created page"

echo "The new page appears in the pages-in-application report API"
http_get_region_json "-20000" "applicationId:-20000"
assert_json "new page listed in report JSON" \
  ".body[] | select(.cells[0].value == \"${NEW_ID}\") | .cells[1].value" "${TEST_NAME}"

echo "Edit mode for the new page"
http_get "${PAGE}?pageParams=id:${NEW_ID}"
assert_http_ok "GET form for new page"
assert_contains "form populated with new page name" "value=\"${TEST_NAME}\""
assert_contains "Update button shown for new page" 'name="Update"'

echo "POST Update modifies PAGE and APPLICATION_PAGE via one CTE"
http_post_form "${PAGE}" \
  "__SUMMIT_FORM_ID__=form--22000" \
  "id=${NEW_ID}" "applicationId=-20000" "name=${TEST_NAME} Renamed" \
  "page_num=999002" "template_id=-100" "Update=Update"
assert_eq "POST Update redirects" "302" "${RESPONSE_CODE}"
assert_eq "Update branches to the pages report for this application" \
  "${SUMMIT_URL}/run/-20000/-20002?applicationId=-20000" "${REDIRECT_URL}"
assert_sql "page row updated" "${TEST_NAME} Renamed" \
  "select name from page where id = ${NEW_ID}"
assert_sql "application_page row updated in the same statement" "999002" \
  "select page_num from application_page where page_id = ${NEW_ID}"
assert_sql "still exactly one test page (INSERT was skipped on Update)" "1" \
  "select count(*) from page where name like 'ZZ Verify Page%'"

echo "Delete button gating (deletes are bottom-up: no regions/processings allowed)"
http_get "${PAGE}?pageParams=id:${NEW_ID}"
assert_contains "Delete button shown for a page with no regions/processings" 'name="Delete"'
http_get "${PAGE}?pageParams=id:-21000"
assert_not_contains "Delete button hidden for a page with regions" 'name="Delete"'

echo "POST Delete removes PAGE + APPLICATION_PAGE and branches to the report"
http_get "${PAGE}?pageParams=id:${NEW_ID}"   # refresh CSRF token
http_post_form "${PAGE}" \
  "__SUMMIT_FORM_ID__=form--22000" \
  "id=${NEW_ID}" "applicationId=-20000" "name=${TEST_NAME} Renamed" \
  "page_num=999002" "template_id=-100" "Delete=Delete"
assert_eq "POST Delete redirects" "302" "${RESPONSE_CODE}"
assert_eq "Delete branches to the pages report for this application" \
  "${SUMMIT_URL}/run/-20000/-20002?applicationId=-20000" "${REDIRECT_URL}"
assert_sql "page row deleted" "0" \
  "select count(*) from page where id = ${NEW_ID}"
assert_sql "application_page row deleted in the same statement" "0" \
  "select count(*) from application_page where page_id = ${NEW_ID}"

verify_summary
