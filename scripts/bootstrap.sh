#!/usr/bin/env bash
# Start Kafka, HBase, Redis, MySQL, and Flink, then wait until schemas and topic "con" exist.
set -euo pipefail
source "$(dirname "$0")/common.sh"

if ! command -v docker >/dev/null 2>&1; then
  echo "Docker is not installed. Install Docker Engine and the Compose v2 plugin, then rerun this script." >&2
  exit 1
fi
if ! docker info >/dev/null 2>&1; then
  echo "The Docker daemon is not running or this user cannot talk to it." >&2
  exit 1
fi

echo "Starting the compose stack (first run builds HBase and imports MySQL; that takes several minutes)..."
compose up -d --build

wait_healthy() {
  local name="$1"
  local i status
  for i in $(seq 1 60); do
    status="$(docker inspect -f '{{if .State.Health}}{{.State.Health.Status}}{{else}}none{{end}}' "$name" 2>/dev/null || echo missing)"
    if [ "$status" = "healthy" ]; then
      echo "$name is healthy"
      return 0
    fi
    echo "waiting for $name ($status)"
    sleep 5
  done
  echo "$name did not become healthy. Last logs:" >&2
  docker logs --tail 80 "$name" >&2 || true
  return 1
}

wait_healthy mysql
wait_healthy redis
wait_healthy kafka
wait_healthy hbase
wait_healthy flink-jobmanager

echo "Waiting for kafka-init to create topic con..."
init_ok=0
for _ in $(seq 1 60); do
  state="$(docker inspect -f '{{.State.Status}} {{.State.ExitCode}}' kafka-init 2>/dev/null || echo missing)"
  if [ "$state" = "exited 0" ]; then
    init_ok=1
    break
  fi
  if [[ "$state" == exited* ]]; then
    echo "kafka-init failed ($state). Logs:" >&2
    docker logs kafka-init >&2 || true
    exit 1
  fi
  sleep 5
done
if [ "$init_ok" -ne 1 ]; then
  echo "kafka-init did not finish (last state: ${state:-unknown}). Logs:" >&2
  docker logs kafka-init >&2 || true
  echo "If those logs are empty and the container is still running, containers cannot reach each other." >&2
  echo "On Linux check the filter FORWARD chain (iptables and iptables-legacy). A DROP policy that only accepts docker0 blocks the compose bridge: Flink's TaskManager also fails to register, and kafka-init never exits. Allow that bridge, then: docker restart kafka-init" >&2
  exit 1
fi

echo "Checking MySQL tables..."
product_count="$(compose exec -T mysql mysql -N -uroot -p123456 -e 'SELECT COUNT(*) FROM con.product;' | tr -d '\r')"
contact_count="$(compose exec -T mysql mysql -N -uroot -p123456 -e 'SELECT COUNT(*) FROM con.contact;' | tr -d '\r')"
user_count="$(compose exec -T mysql mysql -N -uroot -p123456 -e 'SELECT COUNT(*) FROM con.user;' | tr -d '\r')"
echo "MySQL con.product=$product_count con.contact=$contact_count con.user=$user_count"
if [ "${product_count:-0}" -lt 1 ] || [ "${contact_count:-0}" -lt 1 ] || [ "${user_count:-0}" -lt 1 ]; then
  echo "MySQL schema is missing rows. See: docker logs mysql" >&2
  exit 1
fi

echo "Checking HBase tables..."
hbase_list="$(compose exec -T hbase bash -lc 'echo list | /opt/hbase/bin/hbase shell -n' || true)"
missing=""
for table in con prod user u_history p_history ps px u_interest; do
  if ! printf '%s\n' "$hbase_list" | grep -Eq "^[[:space:]]*${table}[[:space:]]*$"; then
    missing="$missing $table"
  fi
done
if [ -n "$missing" ]; then
  echo "HBase is missing:$missing" >&2
  echo "Init log:" >&2
  docker logs hbase >&2 || true
  exit 1
fi
echo "HBase tables: con prod user u_history p_history ps px u_interest"

echo "Checking Kafka topic con..."
compose exec -T kafka /opt/kafka/bin/kafka-topics.sh --bootstrap-server kafka:19092 --describe --topic con

cat <<EOF

Stack is up.
  Kafka   localhost:9092   (topic con)
  HBase   ZooKeeper 127.0.0.1:2181   UI http://localhost:16010
  Redis   localhost:6379
  MySQL   localhost:3308   database con   user root   password 123456
  Flink   UI http://localhost:8081

Next, from the repository root:
  mvn -B package
  ./scripts/run-flink-jobs.sh
  ./scripts/generate-logs.sh
  ./scripts/seed-hotlist.sh
  ./scripts/run-web.sh
Then open http://localhost:8082/recommand?userId=1
EOF
