#!/usr/bin/env bash
# Publish sample logs to topic con.
# Default is one burst (MODE=once) whose event times already span the 60s hot-rank window.
#   ./scripts/generate-logs.sh
#   ./scripts/generate-logs.sh --follow     # random logs until you stop it
# Start the Flink jobs first. They begin at the latest offset and will miss earlier messages.
set -euo pipefail
source "$(dirname "$0")/common.sh"

mode="once"
if [ "${1:-}" = "--follow" ]; then
  mode="follow"
fi

if command -v docker >/dev/null 2>&1 && docker ps --format '{{.Names}}' | grep -qx "$JOB_CONTAINER"; then
  echo "Job container $JOB_CONTAINER is running."
else
  echo "Warning: $JOB_CONTAINER is not running. These jobs start at the latest offset, so messages sent before they start are skipped." >&2
fi

echo "Producing to topic con (MODE=$mode)."
compose exec -T \
  -e MODE="$mode" \
  -e COUNT="${COUNT:-200}" \
  kafka bash /opt/generator.sh
