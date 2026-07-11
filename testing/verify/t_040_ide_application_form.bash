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

echo "Validation: Save with an empty application number re-renders with the error"
http_get "${PAGE}?pageParams=id:0"   # refresh CSRF token
http_post_form_body "${PAGE}" \
  "__SUMMIT_FORM_ID__=form--21100" \
  "id=0" "application_num=" "name=${TEST_NAME}" "Save=Save"
assert_eq "validation failure renders inline (no redirect)" "200" "${RESPONSE_CODE}"
assert_contains "error message shown in the notification area" \
  '<div class="summit-error">Application Number is required.</div>'
assert_contains "submitted name preserved on the re-render" "value=\"${TEST_NAME}\""
assert_sql "no application row created by the failed submit" "0" \
  "select count(*) from application where name = '${TEST_NAME}'"

echo "POST Save inserts a new application with a generated id"
http_get "${PAGE}?pageParams=id:0"   # refresh CSRF token
http_post_form "${PAGE}" \
  "__SUMMIT_FORM_ID__=form--21100" \
  "id=0" "application_num=${TEST_NUM}" "name=${TEST_NAME}" "Save=Save"
assert_eq "POST Save redirects" "302" "${RESPONSE_CODE}"
assert_eq "Save branches to the applications list" \
  "${SUMMIT_URL}/run/-20000/-21000" "${REDIRECT_URL}"
assert_sql "application row created" "${TEST_NAME}" \
  "select name from application where application_num = ${TEST_NUM}"
assert_sql "id came from application_seq (positive, not hand-picked)" "t" \
  "select id > 0 from application where application_num = ${TEST_NUM}"

echo "The redirect after Save renders with the process success message"
http_get "${REDIRECT_URL#${SUMMIT_URL}}"
assert_http_ok "GET post-submit redirect target (applications list)"
assert_contains "success message flashed across the redirect" \
  '<div class="summit-success">Application created.</div>'
http_get "${REDIRECT_URL#${SUMMIT_URL}}"
assert_not_contains "success message shown only once (flash consumed)" \
  '<div class="summit-success">Application created.</div>'

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
assert_eq "Update branches to the applications list" \
  "${SUMMIT_URL}/run/-20000/-21000" "${REDIRECT_URL}"
assert_sql "application row updated" "999002|${TEST_NAME} Renamed" \
  "select application_num, name from application where id = ${NEW_ID}"
assert_sql "still exactly one test application (INSERT was skipped on Update)" "1" \
  "select count(*) from application where id = ${NEW_ID}"
http_get "${REDIRECT_URL#${SUMMIT_URL}}"
assert_contains "update success message flashed across the redirect" \
  '<div class="summit-success">Application updated.</div>'

echo "Delete button gating (deletes are bottom-up: no pages allowed)"
http_get "${PAGE}?pageParams=id:${NEW_ID}"
assert_contains "Delete button shown for an app with no pages" 'name="Delete"'
http_get "${PAGE}?pageParams=id:-20000"
assert_not_contains "Delete button hidden for an app with pages" 'name="Delete"'

echo "POST Delete removes the application and branches to the list"
http_get "${PAGE}?pageParams=id:${NEW_ID}"   # refresh CSRF token
http_post_form "${PAGE}" \
  "__SUMMIT_FORM_ID__=form--21100" \
  "id=${NEW_ID}" "application_num=999002" "name=${TEST_NAME} Renamed" "Delete=Delete"
assert_eq "POST Delete redirects" "302" "${RESPONSE_CODE}"
assert_eq "Delete branches to the applications list" \
  "${SUMMIT_URL}/run/-20000/-21000" "${REDIRECT_URL}"
assert_sql "application row deleted" "0" \
  "select count(*) from application where id = ${NEW_ID}"
http_get "${REDIRECT_URL#${SUMMIT_URL}}"
assert_contains "delete success message flashed across the redirect" \
  '<div class="summit-success">Application deleted.</div>'

verify_summary
