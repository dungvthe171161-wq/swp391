#!/usr/bin/env bash
set -Eeuo pipefail

case "${MYSQL_DATABASE}" in
  ""|*[!a-zA-Z0-9_]*)
    echo >&2 "MYSQL_DATABASE may only contain letters, numbers, and underscores."
    exit 1
    ;;
esac

sql_files=(
  /docker-entrypoint-source/data.sql
  /docker-entrypoint-source/migrations/*.sql
)

for sql_file in "${sql_files[@]}"; do
  if [[ ! -f "${sql_file}" ]]; then
    echo >&2 "Required database script not found: ${sql_file}"
    exit 1
  fi

  echo "Initializing ${MYSQL_DATABASE} from $(basename "${sql_file}")"
  sed \
    -e '1s/^\xEF\xBB\xBF//' \
    -e 's/^data base /-- data base /' \
    -e "s/hrm_db/${MYSQL_DATABASE}/g" "${sql_file}" \
    | MYSQL_PWD="${MYSQL_ROOT_PASSWORD}" mysql --protocol=socket -uroot "${MYSQL_DATABASE}"
done
