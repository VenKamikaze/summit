#!/bin/bash
# Verifies IDE page 6: the fields-on-region report (-24000) and the field
# create/edit form (-24100).
# Built by src/main/resources/sql/postgres/summitdev/summitdev-20260702-ide-p6-fields.sql
#
# Lifecycle: report render -> create-mode form render (template/field-type/
# source-type dropdowns, optional default source type with a 'none' sentinel
# option, textarea for the default value source) -> Save WITH a default source
# (one dml_modify CTE INSERTs FIELD + REGION_FIELD + SOURCE + FIELD_SOURCE) ->
# Save WITHOUT one (CTE inserts FIELD + REGION_FIELD only) -> the created
# field renders on its host page with its static default value -> edit mode ->
# Update -> cleanup.
#
# The test creates its own scratch page + region to hang fields off.

source "$(dirname "$0")/verify-lib.bash"
require_app

REPORT="/run/-20000/-24000"
FORM="/run/-20000/-24100"
TEST_NAME="zzverifyfield"

cleanup() {
  sql "delete from field_source where field_id in (select id from field where name like 'zzverifyfield%')" > /dev/null
  sql "delete from source where source like 'ZZ Verify Field Default%'" > /dev/null
  sql "delete from region_field where field_id in (select id from field where name like 'zzverifyfield%')" > /dev/null
  sql "delete from field where name like 'zzverifyfield%'" > /dev/null
  sql "delete from page_region where region_id in (select id from region where name = 'ZZ Verify Field Host Region')" > /dev/null
  sql "delete from region where name = 'ZZ Verify Field Host Region'" > /dev/null
  sql "delete from application_page where page_id in (select id from page where name = 'ZZ Verify Field Host Page')" > /dev/null
  sql "delete from page where name = 'ZZ Verify Field Host Page'" > /dev/null
}
cleanup
trap 'cleanup; rm -f "${_COOKIE_JAR}"' EXIT

# Scratch page + form region to attach fields to
sql "with new_page as (insert into page (id, template_id, name) select nextval('page_seq'), -100, 'ZZ Verify Field Host Page' returning id), new_ap as (insert into application_page (id, application_id, page_id, page_num) select nextval('application_page_seq'), -20000, id, 999070 from new_page returning id), new_region as (insert into region (id, template_id, name, code_region_position, code_region_type, source_type_code) select nextval('region_seq'), -103, 'ZZ Verify Field Host Region', 'body1', 'Form', 'static' returning id) insert into page_region (id, page_id, region_id, region_num) select nextval('page_region_seq'), np.id, nr.id, 1 from new_page np, new_region nr" > /dev/null
HOST_PAGE="$(sql "select id from page where name = 'ZZ Verify Field Host Page'")"
HOST_REGION="$(sql "select id from region where name = 'ZZ Verify Field Host Region'")"

echo "Fields report renders for an existing region (applications list report -21000)"
http_get "${REPORT}?pageParams=regionId:-21000"
assert_http_ok "GET fields report"
assert_contains "hidden regionId field populated from pageParams" \
  'name="regionId" id="-24000" value="-21000"'
assert_contains "row link targets the field form in edit mode" \
  'id="region--24000-sprt-link" value="/run/-20000/-24100?pageParams=id:"'
assert_contains "Create button passes id:0 + regionId in pageParams format" \
  "location.href='/run/-20000/-24100?pageParams=id:0,regionId:' + document.getElementById('-24000').value;"

echo "Fields report API lists the region's fields"
http_get_region_json "-24000" "regionId:-21000"
assert_http_ok "GET region JSON"
assert_json "first column is 'id'" '.header.cells[0].value' "id"
assert_json "row count matches region_field for region -21000" '.body | length' \
  "$(sql 'select count(*) from region_field where region_id = -21000')"

echo "Create mode (pageParams=id:0,regionId:${HOST_REGION})"
http_get "${FORM}?pageParams=id:0,regionId:${HOST_REGION}"
assert_http_ok "GET form in create mode"
assert_contains "Save button shown" 'name="Save"'
assert_not_contains "Update button hidden" 'name="Update"'
assert_contains "regionId hidden field populated from pageParams" \
  "name=\"regionId\" id=\"-24101\" value=\"${HOST_REGION}\""
assert_contains "template dropdown offers field templates" \
  '<option  value="-61">Input Item - Text</option>'
assert_contains "field type dropdown from CODE_FIELD_TYPE" \
  '<option  value="TEXT">'
assert_contains "source type dropdown from CODE_SOURCE_TYPE" \
  '<option  value="static">'
assert_contains "default source type has the (none) sentinel option" \
  '<option  value="none">(none)</option>'
assert_contains "textarea for the default value source" \
  '<textarea name="default_source" id="-24108" rows="10" cols="80">'

echo "Edit mode for an existing field (applications list row link -21001)"
http_get "${FORM}?pageParams=id:-21001"
assert_http_ok "GET form in edit mode"
assert_contains "name populated" 'value="applications-sprt-link"'
assert_contains "field_num populated" 'name="field_num" id="-24103" value="1"'
assert_contains "regionId derived from the field id (row link passes id only)" \
  'name="regionId" id="-24101" value="-21000"'
assert_contains "template pre-selected" 'selected="selected" value="-50"'
assert_contains "field type pre-selected" 'selected="selected" value="TEXT"'
assert_contains "default value source shown in the textarea" \
  'run/-20000/-21100?pageParams=id:</textarea>'
assert_contains "Update button shown" 'name="Update"'
assert_not_contains "Save button hidden" 'name="Save"'

echo "POST Save with a default source inserts FIELD + REGION_FIELD + SOURCE + FIELD_SOURCE"
http_get "${FORM}?pageParams=id:0,regionId:${HOST_REGION}"   # refresh CSRF token
http_post_form "${FORM}" \
  "__SUMMIT_FORM_ID__=form--24100" \
  "id=0" "regionId=${HOST_REGION}" "name=${TEST_NAME}" "field_num=1" \
  "template_id=-61" "field_type_code=TEXT" "source_type_code=static" \
  "default_source_type_code=static" "default_source=ZZ Verify Field Default" \
  "Save=Save"
assert_eq "POST Save redirects" "302" "${RESPONSE_CODE}"
assert_eq "Save branches to the fields report for the host region" \
  "${SUMMIT_URL}/run/-20000/-24000?regionId=${HOST_REGION}" "${REDIRECT_URL}"
assert_sql "field row created with codes intact (NULLIF kept 'static')" \
  "-61|TEXT|static|static" \
  "select template_id, field_type_code, source_type_code, default_source_type_code from field where name = '${TEST_NAME}'"
assert_sql "field id came from field_seq (positive)" "t" \
  "select id > 0 from field where name = '${TEST_NAME}'"
assert_sql "region_field row created in the same statement" "${HOST_REGION}|1" \
  "select rf.region_id, rf.field_num from region_field rf join field f on rf.field_id = f.id where f.name = '${TEST_NAME}'"
assert_sql "default source + field_source (flag Y) created in the same statement" \
  "ZZ Verify Field Default|Y" \
  "select s.source, fs.flag_default_value from source s join field_source fs on fs.source_id = s.id join field f on f.id = fs.field_id where f.name = '${TEST_NAME}'"

echo "POST Save with (none) + empty default source inserts FIELD + REGION_FIELD only"
http_get "${FORM}?pageParams=id:0,regionId:${HOST_REGION}"
http_post_form "${FORM}" \
  "__SUMMIT_FORM_ID__=form--24100" \
  "id=0" "regionId=${HOST_REGION}" "name=${TEST_NAME}2" "field_num=2" \
  "template_id=-60" "field_type_code=TEXT" "source_type_code=static" \
  "default_source_type_code=none" "default_source=" \
  "Save=Save"
assert_eq "POST Save redirects" "302" "${RESPONSE_CODE}"
assert_sql "field row created with NULL default source type ('none' sentinel)" "t" \
  "select default_source_type_code is null from field where name = '${TEST_NAME}2'"
assert_sql "no field_source row was created" "0" \
  "select count(*) from field_source fs join field f on f.id = fs.field_id where f.name = '${TEST_NAME}2'"

echo "The field the IDE just created actually renders with its default value"
http_get "/run/-20000/${HOST_PAGE}"
assert_http_ok "GET the host page with the new fields"
assert_contains "text field renders with its static default value" \
  "name=\"${TEST_NAME}\""
assert_contains "default value rendered" 'ZZ Verify Field Default'

NEW_ID="$(sql "select id from field where name = '${TEST_NAME}'")"

echo "Edit mode for the new field"
http_get "${FORM}?pageParams=id:${NEW_ID}"
assert_http_ok "GET form for new field"
assert_contains "form populated with new field name" "value=\"${TEST_NAME}\""
assert_contains "default source shown" '>ZZ Verify Field Default</textarea>'
assert_contains "Update button shown for new field" 'name="Update"'

echo "POST Update modifies FIELD, REGION_FIELD and the default SOURCE via one CTE"
http_post_form "${FORM}" \
  "__SUMMIT_FORM_ID__=form--24100" \
  "id=${NEW_ID}" "regionId=${HOST_REGION}" "name=${TEST_NAME}renamed" "field_num=3" \
  "template_id=-61" "field_type_code=TEXT" "source_type_code=static" \
  "default_source_type_code=static" "default_source=ZZ Verify Field Default Renamed" \
  "Update=Update"
assert_eq "POST Update redirects" "302" "${RESPONSE_CODE}"
assert_eq "Update branches to the fields report for the host region" \
  "${SUMMIT_URL}/run/-20000/-24000?regionId=${HOST_REGION}" "${REDIRECT_URL}"
assert_sql "field row updated" "${TEST_NAME}renamed" \
  "select name from field where id = ${NEW_ID}"
assert_sql "region_field row updated in the same statement" "3" \
  "select field_num from region_field where field_id = ${NEW_ID}"
assert_sql "default source updated in the same statement" "ZZ Verify Field Default Renamed" \
  "select s.source from source s join field_source fs on fs.source_id = s.id where fs.field_id = ${NEW_ID}"
assert_sql "still exactly two test fields (INSERT was skipped on Update)" "2" \
  "select count(*) from field where name like 'zzverifyfield%'"

echo "Delete button shown in edit mode (fields are the leaf: no child gate)"
http_get "${FORM}?pageParams=id:${NEW_ID}"
assert_contains "Delete button shown for an existing field" 'name="Delete"'

echo "POST Delete removes FIELD + REGION_FIELD + FIELD_SOURCE + SOURCE"
http_post_form "${FORM}" \
  "__SUMMIT_FORM_ID__=form--24100" \
  "id=${NEW_ID}" "regionId=${HOST_REGION}" "name=${TEST_NAME}renamed" "field_num=3" \
  "template_id=-61" "field_type_code=TEXT" "source_type_code=static" \
  "default_source_type_code=static" "default_source=x" "Delete=Delete"
assert_eq "POST Delete redirects" "302" "${RESPONSE_CODE}"
assert_eq "Delete branches to the fields report for the host region" \
  "${SUMMIT_URL}/run/-20000/-24000?regionId=${HOST_REGION}" "${REDIRECT_URL}"
assert_sql "field row deleted" "0" \
  "select count(*) from field where id = ${NEW_ID}"
assert_sql "region_field row deleted in the same statement" "0" \
  "select count(*) from region_field where field_id = ${NEW_ID}"
assert_sql "default source deleted in the same statement" "0" \
  "select count(*) from source where source like 'ZZ Verify Field Default%'"

verify_summary
