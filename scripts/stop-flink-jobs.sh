#!/usr/bin/env bash
set -euo pipefail
source "$(dirname "$0")/common.sh"

if command -v docker >/dev/null 2>&1 && docker ps -a --format '{{.Names}}' | grep -qx "$JOB_CONTAINER"; then
  docker rm -f "$JOB_CONTAINER" >/dev/null
  echo "removed container $JOB_CONTAINER"
fi

log_dir="$ROOT/logs/flink-jobs"
if [ -d "$log_dir" ]; then
  for pidfile in "$log_dir"/*.pid; do
    [ -e "$pidfile" ] || continue
    pid="$(cat "$pidfile")"
    if kill -0 "$pid" 2>/dev/null; then
      kill "$pid" 2>/dev/null || true
      echo "stopped pid $pid ($(basename "$pidfile" .pid))"
    fi
    rm -f "$pidfile"
  done
fi
