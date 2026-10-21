#!/usr/bin/env bash
# Shared paths for the demo scripts. Source this file; do not execute it.
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
COMPOSE_FILE="$ROOT/resources/docker/docker-compose/docker-compose.yml"
JOB_JAR="$ROOT/flink-2-hbase/target/flink-2-hbase-1.0-SNAPSHOT.jar"
JOB_CONTAINER="flink-demo-jobs"

compose() {
  docker compose -f "$COMPOSE_FILE" "$@"
}
