#!/bin/bash
# run-verify.bash - run all (or selected) summit page verification tests.
#
# Usage:
#   ./run-verify.bash                 # run every t_*.bash in this directory
#   ./run-verify.bash t_010*.bash     # run selected tests
#
# Requires a running summit instance and access to its database.
# See verify-lib.bash for SUMMIT_URL / SUMMIT_DB_* environment overrides.

cd "$(dirname "$0")" || exit 2

tests=("$@")
[ ${#tests[@]} -eq 0 ] && tests=(t_*.bash)

failures=0
for t in "${tests[@]}"; do
  echo "=== ${t}"
  if [ ! -f "${t}" ]; then
    echo "ERROR: no such test script: ${t}" >&2
    failures=$((failures + 1))
    continue
  fi
  if ! bash "${t}"; then
    failures=$((failures + 1))
  fi
  echo
done

if [ ${failures} -gt 0 ]; then
  echo "RESULT: ${failures} test script(s) FAILED"
  exit 1
fi
echo "RESULT: all test scripts passed"
