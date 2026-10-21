#!/bin/bash
# Standalone HBase. Tables are created once from the mounted shell script.
set -u
export HBASE_HOME="${HBASE_HOME:-/opt/hbase}"
export PATH="${HBASE_HOME}/bin:${PATH}"

mkdir -p /data/hbase /data/zookeeper "${HBASE_HOME}/logs"

hn="$(hostname)"
if ! grep -Eq "[[:space:]]${hn}([[:space:]]|$)" /etc/hosts; then
  echo "127.0.0.1 ${hn}" >> /etc/hosts
fi

echo "Starting HBase standalone (hostname ${hn})..."
if ! "${HBASE_HOME}/bin/start-hbase.sh"; then
  echo "start-hbase.sh returned non-zero; container stays up so logs remain readable" >&2
fi

ready=0
for _ in $(seq 1 90); do
  if echo "status" | "${HBASE_HOME}/bin/hbase" shell -n 2>/dev/null | grep -q "active master"; then
    ready=1
    echo "HBase master is active"
    break
  fi
  sleep 2
done

if [ "${ready}" -ne 1 ]; then
  echo "HBase did not report an active master within 180s" >&2
elif [ ! -f /data/hbase/.tables-initialized ] && [ -f /opt/hbase_ini.sql ]; then
  echo "Applying /opt/hbase_ini.sql"
  if "${HBASE_HOME}/bin/hbase" shell -n /opt/hbase_ini.sql; then
    touch /data/hbase/.tables-initialized
    echo "HBase tables initialized"
  else
    echo "HBase table init failed. The script is safe to run again; restart this container after fixing it." >&2
  fi
else
  echo "HBase tables already initialized"
fi

touch "${HBASE_HOME}/logs/.keep"
exec tail -F "${HBASE_HOME}"/logs/*
