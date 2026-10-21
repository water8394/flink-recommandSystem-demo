#!/bin/bash
set -eu
export HBASE_HOME="${HBASE_HOME:-/opt/hbase}"
export PATH="${HBASE_HOME}/bin:${PATH}"

mkdir -p /data/hbase /data/zookeeper "${HBASE_HOME}/logs"
"${HBASE_HOME}/bin/start-hbase.sh"

ready=0
for _ in $(seq 1 90); do
  if echo "status" | "${HBASE_HOME}/bin/hbase" shell -n 2>/dev/null | grep -q "active master"; then
    ready=1
    break
  fi
  sleep 2
done

if [ "${ready}" -eq 1 ] && [ ! -f /data/hbase/.tables-initialized ] && [ -f /opt/hbase_ini.sql ]; then
  "${HBASE_HOME}/bin/hbase" shell -n /opt/hbase_ini.sql
  touch /data/hbase/.tables-initialized
fi

touch "${HBASE_HOME}/logs/.keep"
exec tail -F "${HBASE_HOME}"/logs/*
