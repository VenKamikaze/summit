#!/bin/bash
# verify-lib.bash - reusable verification library for summit pages.
#
# Verifies a running summit instance end-to-end:
#   - GET rendered pages and assert on the HTML
#   - GET the report region JSON API and assert on the data (jq)
#   - POST forms (handles the CSRF cookie/token dance) and assert on redirects
#   - Run SQL against the summit database and assert on results
#
# Usage: source this file from a t_*.bash test script, then call verify_summary
# at the end. Run all tests via run-verify.bash.
#
# Configuration (environment overrides):
#   SUMMIT_URL      base URL of the running app  (default http://localhost:8080)
#   SUMMIT_DB_HOST  database host                (default localhost)
#   SUMMIT_DB_PORT  database port                (default 5432)
#   SUMMIT_DB_USER  database user                (default summit)
#   SUMMIT_DB_NAME  database name                (default summit)
#   PGPASSWORD      respected by psql if set

SUMMIT_URL="${SUMMIT_URL:-http://localhost:8080}"
SUMMIT_DB_HOST="${SUMMIT_DB_HOST:-localhost}"
SUMMIT_DB_PORT="${SUMMIT_DB_PORT:-5432}"
SUMMIT_DB_USER="${SUMMIT_DB_USER:-summit}"
SUMMIT_DB_NAME="${SUMMIT_DB_NAME:-summit}"

_PASS_COUNT=0
_FAIL_COUNT=0
_COOKIE_JAR="$(mktemp /tmp/summit-verify-cookies.XXXXXX)"
trap 'rm -f "${_COOKIE_JAR}"' EXIT

# Populated by http_get / http_post_form
RESPONSE_BODY=""
RESPONSE_CODE=""
REDIRECT_URL=""

_pass() { _PASS_COUNT=$((_PASS_COUNT + 1)); echo "  PASS: $1"; }
_fail() { _FAIL_COUNT=$((_FAIL_COUNT + 1)); echo "  FAIL: $1" >&2; }

# ---------------------------------------------------------------- app / db --

# Fail fast (exit) if the app is not reachable.
require_app() {
  if ! curl -s -o /dev/null --max-time 5 "${SUMMIT_URL}"; then
    echo "ERROR: summit is not reachable at ${SUMMIT_URL}." >&2
    echo "Start it with e.g.: mvn spring-boot:run   (or java -jar target/summit-sb-*.jar)" >&2
    exit 2
  fi
}

# Run a query, print tuples-only unaligned output. Usage: sql "select ..."
sql() {
  psql -h "${SUMMIT_DB_HOST}" -p "${SUMMIT_DB_PORT}" -U "${SUMMIT_DB_USER}" \
       -d "${SUMMIT_DB_NAME}" -v ON_ERROR_STOP=1 -tA -c "$1"
}

# ----------------------------------------------------------------- http -----

# GET a path (e.g. "/run/-20000/-21000"). Sets RESPONSE_BODY and RESPONSE_CODE.
# Maintains the cookie jar so the CSRF token cookie is available for POSTs.
http_get() {
  local path="$1"
  RESPONSE_BODY="$(curl -s -w '\n%{http_code}' -b "${_COOKIE_JAR}" -c "${_COOKIE_JAR}" "${SUMMIT_URL}${path}")"
  RESPONSE_CODE="${RESPONSE_BODY##*$'\n'}"
  RESPONSE_BODY="${RESPONSE_BODY%$'\n'*}"
}

# Current CSRF token from the cookie jar (empty if no page fetched yet).
csrf_token() {
  awk '$6 == "XSRF-TOKEN" { print $7 }' "${_COOKIE_JAR}" | tail -1
}

# POST form data to a path. Automatically appends the CSRF token.
# A page must have been fetched with http_get first so the token cookie exists.
# Usage: http_post_form "/run/-20000/-21000" "__SUMMIT_FORM_ID__=form--21000" "name=abc" ...
# Sets RESPONSE_CODE and REDIRECT_URL (summit answers POSTs with a 302).
http_post_form() {
  local path="$1"; shift
  local args=()
  local kv
  for kv in "$@"; do
    args+=(--data-urlencode "${kv}")
  done
  local out
  out="$(curl -s -o /dev/null -w '%{http_code}\n%{redirect_url}' \
      -b "${_COOKIE_JAR}" -c "${_COOKIE_JAR}" \
      --data-urlencode "_csrf=$(csrf_token)" "${args[@]}" \
      "${SUMMIT_URL}${path}")"
  RESPONSE_CODE="${out%%$'\n'*}"
  REDIRECT_URL="${out#*$'\n'}"
  RESPONSE_BODY=""
}

# GET the report region JSON API. Sets RESPONSE_BODY (JSON) and RESPONSE_CODE.
# Usage: http_get_region_json <regionId> [pageParams e.g. "applicationId:-20000"]
http_get_region_json() {
  local region_id="$1"
  local page_params="${2:-}"
  local path="/api/filter/json/${region_id}"
  [ -n "${page_params}" ] && path="${path}?pageParams=${page_params}"
  http_get "${path}"
}

# ------------------------------------------------------------- assertions ---

# assert_eq <description> <expected> <actual>
assert_eq() {
  if [ "$2" = "$3" ]; then _pass "$1"; else _fail "$1 (expected: '$2', actual: '$3')"; fi
}

# assert_http_ok <description>  - last http_get returned 200
assert_http_ok() {
  assert_eq "$1 [HTTP 200]" "200" "${RESPONSE_CODE}"
}

# assert_contains <description> <needle>  - needle in last RESPONSE_BODY
assert_contains() {
  if [[ "${RESPONSE_BODY}" == *"$2"* ]]; then _pass "$1"; else _fail "$1 (response does not contain: '$2')"; fi
}

# assert_not_contains <description> <needle>
assert_not_contains() {
  if [[ "${RESPONSE_BODY}" != *"$2"* ]]; then _pass "$1"; else _fail "$1 (response unexpectedly contains: '$2')"; fi
}

# assert_sql <description> <expected> <query>  - compare psql -tA output
assert_sql() {
  local actual
  if ! actual="$(sql "$3")"; then _fail "$1 (query failed: $3)"; return; fi
  assert_eq "$1" "$2" "${actual}"
}

# assert_json <description> <jq-expression> <expected>  - against last RESPONSE_BODY
assert_json() {
  local actual
  if ! actual="$(printf '%s' "${RESPONSE_BODY}" | jq -r "$2" 2>/dev/null)"; then
    _fail "$1 (jq expression failed or response not JSON: $2)"; return
  fi
  assert_eq "$1" "$3" "${actual}"
}

# ---------------------------------------------------------------- summary ---

# Print totals; exit 1 if anything failed. Call at the end of every test script.
verify_summary() {
  echo "----------------------------------------"
  echo "  ${_PASS_COUNT} passed, ${_FAIL_COUNT} failed"
  [ "${_FAIL_COUNT}" -eq 0 ]
}
