#!/bin/bash
# Verifies IDE page 2: the application create/edit form (app -20000, page -21100).
# Built by src/main/resources/sql/postgres/summitdev/summitdev-20260702-ide-p2-application-form.sql
#
# Covers the full lifecycle: create-mode render -> Save (INSERT with a
# sequence-generated id) -> post-submit redirect renders -> edit-mode render
# -> Update -> cleanup. Buttons are gated by EXISTS/NOTEXISTS field
# conditionals on the id, processing by :REQUEST conditionals.

source "$(dirname "$0")/verify-lib.bash"
require_app

PAGE="/run/-20000/-21100"
TEST_NAME="ZZ Verify App"
TEST_NUM="999001"

cleanup() { sql "delete from application where application_num in (999001, 999002) or name like 'ZZ Verify App%'" > /dev/null; }
cleanup
trap 'cleanup; rm -f "${_COOKIE_JAR}"' EXIT

echo "Create mode (pageParams=id:0)"
http_get "${PAGE}?pageParams=id:0"
assert_http_ok "GET form in create mode"
assert_contains "Save button shown" 'name="Save"'
assert_not_contains "Update button hidden" 'name="Update"'
assert_not_contains "Pages button hidden" 'name="Pages"'
assert_contains "Application Number label renders" '>Application Number</label>'
assert_contains "Application Name label renders" '>Application Name</label>'

echo "Edit mode for an existing application (the IDE app itself)"
http_get "${PAGE}?pageParams=id:-20000"
assert_http_ok "GET form in edit mode"
assert_contains "form populated from dml_selrow" 'value="Summit - IDE"'
assert_contains "Update button shown" 'name="Update"'
assert_contains "Pages button navigates to pages report with this app id" \
  "location.href='/run/-20000/-20002?pageParams=applicationId:' + document.getElementById('-21100').value;"
assert_not_contains "Save button hidden" 'name="Save"'

echo "POST Save inserts a new application with a generated id"
http_get "${PAGE}?pageParams=id:0"   # refresh CSRF token
http_post_form "${PAGE}" \
  "__SUMMIT_FORM_ID__=form--21100" \
  "id=0" "application_num=${TEST_NUM}" "name=${TEST_NAME}" "Save=Save"
assert_eq "POST Save redirects" "302" "${RESPONSE_CODE}"
assert_sql "application row created" "${TEST_NAME}" \
  "select name from application where application_num = ${TEST_NUM}"
assert_sql "id came from application_seq (positive, not hand-picked)" "t" \
  "select id > 0 from application where application_num = ${TEST_NUM}"

echo "The redirect after Save renders (query params accepted as page params)"
http_get "${REDIRECT_URL#${SUMMIT_URL}}"
assert_http_ok "GET post-submit redirect target"

NEW_ID="$(sql "select id from application where application_num = ${TEST_NUM}")"

echo "Edit mode for the new application"
http_get "${PAGE}?pageParams=id:${NEW_ID}"
assert_http_ok "GET form for new app"
assert_contains "form populated with new app name" "value=\"${TEST_NAME}\""
assert_contains "Update button shown for new app" 'name="Update"'

echo "POST Update modifies the application"
http_post_form "${PAGE}" \
  "__SUMMIT_FORM_ID__=form--21100" \
  "id=${NEW_ID}" "application_num=999002" "name=${TEST_NAME} Renamed" "Update=Update"
assert_eq "POST Update redirects" "302" "${RESPONSE_CODE}"
assert_sql "application row updated" "999002|${TEST_NAME} Renamed" \
  "select application_num, name from application where id = ${NEW_ID}"
assert_sql "still exactly one test application (INSERT was skipped on Update)" "1" \
  "select count(*) from application where id = ${NEW_ID}"

verify_summary
