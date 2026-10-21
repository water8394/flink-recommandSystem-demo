#!/bin/bash
# Sample producer for topic "con".
# Message format: userId,productId,epochSeconds,action
# action: 1 browse, 2 favorite, 3 purchase.
#
# The compose file mounts this script in the kafka container at /opt/generator.sh.
# Continuous random logs (original behaviour):
#   docker compose -f resources/docker/docker-compose/docker-compose.yml exec kafka bash /opt/generator.sh
# One finite burst whose timestamps already cover the 60s hot-rank window:
#   MODE=once ./scripts/generate-logs.sh
#
# Inside the compose network the broker is kafka:19092. From the host it is localhost:9092.

set -u
KAFKA_HOME="${KAFKA_HOME:-/opt/kafka}"
BOOTSTRAP="${KAFKA_BOOTSTRAP:-kafka:19092}"
TOPIC="${TOPIC:-con}"
MODE="${MODE:-follow}"
COUNT="${COUNT:-120}"

create_kafka_topic() {
  "$KAFKA_HOME/bin/kafka-topics.sh" --create --if-not-exists \
    --bootstrap-server "$BOOTSTRAP" --replication-factor 1 --partitions 1 --topic "$1"
}

rand() {
  min=$1
  max=$(($2 - min + 1))
  num=$(date +%s%N)
  echo $((num % max + min))
}

generator_message() {
  uid=$(rand 1 20)
  product_id=$(rand 10 30)
  timestamp=$(date '+%s')
  action=$(rand 1 3)
  echo "${uid},${product_id},${timestamp},${action}"
}

create_kafka_topic "$TOPIC"

if [ "$MODE" = "once" ]; then
  now=$(date +%s)
  start=$((now - 100))
  i=0
  while [ "$i" -lt "$COUNT" ]; do
    # Heavier weight on a few ids that exist in both MySQL tables.
    bucket=$((i % 10))
    if [ "$bucket" -lt 4 ]; then
      product_id=11
    elif [ "$bucket" -lt 7 ]; then
      product_id=12
    elif [ "$bucket" -lt 8 ]; then
      product_id=13
    elif [ "$bucket" -lt 9 ]; then
      product_id=14
    else
      product_id=15
    fi
    uid=$(( (i % 5) + 1 ))
    action=$(( (i % 3) + 1 ))
    ts=$((start + (100 * i / COUNT)))
    echo "${uid},${product_id},${ts},${action}"
    i=$((i + 1))
  done | "$KAFKA_HOME/bin/kafka-console-producer.sh" --bootstrap-server "$BOOTSTRAP" --topic "$TOPIC"
  echo "sent ${COUNT} messages to ${TOPIC}"
  exit 0
fi

while true
do
  generator_message | "$KAFKA_HOME/bin/kafka-console-producer.sh" --bootstrap-server "$BOOTSTRAP" --topic "$TOPIC"
  sleep 0.1
done
