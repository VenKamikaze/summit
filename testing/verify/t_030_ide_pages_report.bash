#!/bin/bash
# Verifies IDE pages-in-application report (app -20000, page -20002) after the
# 2026-07-02 fix-up of summitdev-20241102.sql (BUTTON->SUBMIT FK fix, missing
# default_source_type_code, pageParams URL format on the Create button).

source "$(dirname "$0")/verify-lib.bash"
require_app

PAGE="/run/-20000/-20002"

echo "Fixed metadata is loaded"
assert_sql "Create button uses SUBMIT type + static default source" "SUBMIT|static" \
  "select field_type_code, default_source_type_code from field where id = -20102"
assert_sql "create-page stub -20102 exists in IDE app" "1" \
  "select count(*) from application_page where application_id = -20000 and page_id = -20102"

echo "Pages report renders for the IDE application"
http_get "${PAGE}?pageParams=applicationId:-20000"
assert_http_ok "GET pages report"
assert_contains "hidden applicationId field populated from pageParams" \
  'name="applicationId" id="-20002" value="-20000"'
assert_contains "Create button passes applicationId in pageParams format" \
  "location.href='/run/-20000/-20102?pageParams=applicationId:' + document.getElementById('-20002').value;"
assert_contains "fields render outside the JS-owned report div" \
  'id="regionFields--20000"'

echo "Report API returns the IDE app pages"
http_get_region_json "-20000" "applicationId:-20000"
assert_http_ok "GET region JSON"
assert_json "first column is 'id'" '.header.cells[0].value' "id"
assert_json "row count matches application_page for app -20000" '.body | length' \
  "$(sql 'select count(*) from application_page where application_id = -20000')"

verify_summary
