#!/usr/bin/env bash
# Start the six streaming jobs where config.properties hostnames (localhost) are correct.
# On Linux this is one Flink 1.18 container on the host network.
# Elsewhere it uses a local JDK 11 or 17. JDK 21 can compile the project but Flink 1.18 will not run the jobs.
set -euo pipefail
source "$(dirname "$0")/common.sh"

if [ ! -f "$JOB_JAR" ]; then
  echo "Missing $JOB_JAR. From the repository root run: mvn -B package" >&2
  exit 1
fi

JOBS=(
  com.demo.task.LogTask
  com.demo.task.UserHistoryTask
  com.demo.task.UserInterestTask
  com.demo.task.UserPortraitTask
  com.demo.task.ProductProtaritTask
  com.demo.task.TopProductTask
)

java_major() {
  java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d. -f1
}

start_with_docker() {
  if docker ps -a --format '{{.Names}}' | grep -qx "$JOB_CONTAINER"; then
    docker rm -f "$JOB_CONTAINER" >/dev/null
  fi
  echo "Starting jobs in container $JOB_CONTAINER (host network, Java 11 from the Flink image)..."
  docker run -d --name "$JOB_CONTAINER" --network host \
    --memory 2048m \
    -v "$JOB_JAR:/opt/job.jar:ro" \
    flink:1.18.1-scala_2.12-java11 \
    bash -lc '
      set -u
      pids=""
      for main in "$@"; do
        echo "launch $main"
        java -Xms64m -Xmx256m -cp /opt/job.jar "$main" &
        pids="$pids $!"
      done
      sleep 25
      alive=0
      for pid in $pids; do
        if kill -0 "$pid" 2>/dev/null; then
          alive=$((alive + 1))
        fi
      done
      echo "jobs still running after 25s: $alive"
      if [ "$alive" -lt 1 ]; then
        exit 1
      fi
      wait
    ' bash "${JOBS[@]}"
  echo "Logs: docker logs -f $JOB_CONTAINER"
  echo "Stop: ./scripts/stop-flink-jobs.sh"
}

start_with_local_java() {
  local major log_dir
  major="$(java_major)"
  if [ "$major" != "11" ] && [ "$major" != "17" ]; then
    echo "java on PATH is major version ${major:-unknown}. Flink 1.18 needs JDK 11 or 17 to run these jobs." >&2
    echo "Install one of those, or on Linux rerun this script with Docker available." >&2
    exit 1
  fi
  log_dir="$ROOT/logs/flink-jobs"
  mkdir -p "$log_dir"
  local main name
  for main in "${JOBS[@]}"; do
    name="${main##*.}"
    if [ -f "$log_dir/$name.pid" ] && kill -0 "$(cat "$log_dir/$name.pid")" 2>/dev/null; then
      echo "$name already running (pid $(cat "$log_dir/$name.pid"))"
      continue
    fi
    echo "launch $main -> $log_dir/$name.log"
    nohup java -Xms128m -Xmx384m -cp "$JOB_JAR" "$main" >"$log_dir/$name.log" 2>&1 &
    echo $! >"$log_dir/$name.pid"
  done
  echo "Logs: $log_dir"
  echo "Stop: ./scripts/stop-flink-jobs.sh"
}

if [ "$(uname -s)" = "Linux" ] && command -v docker >/dev/null 2>&1 && docker info >/dev/null 2>&1; then
  start_with_docker
else
  start_with_local_java
fi
