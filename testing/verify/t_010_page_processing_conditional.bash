#!/bin/bash
# Verifies PAGE_PROCESSING_CONDITIONAL and FIELD_CONDITIONAL at runtime using the
# test form page from test_dml.sql / test_form_dml.sql (app -10000, page -10000).
#
# The page has two POST processings gated by conditionals on :REQUEST
# ('Save' -> INSERT, 'Update' -> UPDATE against CODE_SOURCE_TYPE), and the two
# submit buttons are shown/hidden by EXISTS/NOTEXISTS conditionals.
# If processing conditionals were broken, the Update POST would also run the
# INSERT (sequence 1) and fail with a PK violation.

source "$(dirname "$0")/verify-lib.bash"
require_app

TEST_CODE="zz_verify"
PAGE="/run/-10000/-10000"

cleanup() { sql "delete from code_source_type where code = '${TEST_CODE}'" > /dev/null; }
cleanup
trap 'cleanup; rm -f "${_COOKIE_JAR}"' EXIT

echo "Test form page renders with NOTEXISTS conditional (row absent -> Save button only)"
http_get "${PAGE}?pageParams=code:${TEST_CODE}"
assert_http_ok "GET form page"
assert_contains "form is present" "__SUMMIT_FORM_ID__"
assert_contains "CSRF token is present" 'name="_csrf"'
assert_contains "Save button shown when code does not exist" 'name="Save"'
assert_not_contains "Update button hidden when code does not exist" 'name="Update"'

echo "POST REQUEST=Save runs only the INSERT processing"
http_post_form "${PAGE}" \
  "__SUMMIT_FORM_ID__=form--10000" \
  "code=${TEST_CODE}" "description=inserted-by-save" "sort_order=99" \
  "source_identifier=zz" "Save=Save" "hiddencode="
assert_eq "POST Save redirects" "302" "${RESPONSE_CODE}"
assert_sql "row inserted by Save" "${TEST_CODE}|inserted-by-save|99|zz" \
  "select code, description, sort_order, source_identifier from code_source_type where code = '${TEST_CODE}'"

echo "EXISTS conditional flips buttons once the row exists"
http_get "${PAGE}?pageParams=code:${TEST_CODE}"
assert_http_ok "GET form page again"
assert_contains "Update button shown when code exists" 'name="Update"'
assert_not_contains "Save button hidden when code exists" 'name="Save"'
assert_contains "dml_selrow populated description field" 'value="inserted-by-save"'

echo "POST REQUEST=Update runs only the UPDATE processing (no PK violation from INSERT)"
http_post_form "${PAGE}" \
  "__SUMMIT_FORM_ID__=form--10000" \
  "code=${TEST_CODE}" "description=updated-by-update" "sort_order=88" \
  "source_identifier=zz2" "Update=Update" "hiddencode=${TEST_CODE}"
assert_eq "POST Update redirects" "302" "${RESPONSE_CODE}"
assert_sql "row updated by Update" "${TEST_CODE}|updated-by-update|88|zz2" \
  "select code, description, sort_order, source_identifier from code_source_type where code = '${TEST_CODE}'"
assert_sql "still exactly one row (INSERT was skipped)" "1" \
  "select count(*) from code_source_type where code = '${TEST_CODE}'"

verify_summary
