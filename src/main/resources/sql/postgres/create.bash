#!/bin/bash
#
# Create summit using psql

let RUN_DEMO_SQL=1
let DROP_FIRST=1
DATABASE=postgres
# Must match the tablespace for summit defined in dba_ddl.sql
# Must also be created by the root user before running this script.
TABLESPACE_LOCATION=/var/lib/postgres/tablespaces/summit

[[ $(whoami) != "postgres" ]] && echo "Must be run as postgres user." >&2 && exit 1

which psql >/dev/null
[[ $? -ne 0 ]] && echo "psql must be on your PATH. Exiting..." >&2 && exit 2

mkdir -p "${TABLESPACE_LOCATION}"
if [[ ! -d "${TABLESPACE_LOCATION}" ]]; then
  echo "Please run the following as root, and then try again:"
  echo "mkdir -p ${TABLESPACE_LOCATION}"
  echo "chmod 0700 ${TABLESPACE_LOCATION}"
  echo "chown postgres: ${TABLESPACE_LOCATION}"
  exit 3
else
  chmod 0700 "${TABLESPACE_LOCATION}"
fi

destroyAll() {
  psql -d postgres -c 'drop database summit;'
  psql -d postgres -c 'drop tablespace summit;'
  psql -d postgres -c 'drop role summit;'
}

runScript() {
  local FILE=${1:-}
  if [[ -n "${FILE}" ]]; then
    echo "Running ${FILE} on ${DATABASE}"
    psql -d ${DATABASE} -f "${FILE}"
    RETVAL=$?
    [[ $? -ne 0 ]] && echo "Got back $RETVAL" >&2
  fi
  return $RETVAL
}

if [[ ${DROP_FIRST} == 1 ]]; then
  destroyAll
fi

runScript dba_ddl.sql
if [[ $? -eq 0 ]]; then
  DATABASE=summit
  runScript ddl.sql
  runScript setup-codetables.sql
  runScript setup-backend.sql
  if [[ ${RUN_DEMO_SQL} == 1 ]]; then
    runScript test_dml.sql
    runScript test_form_dml.sql
  fi
fi
