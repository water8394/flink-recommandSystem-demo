#!/usr/bin/env bash
# Checks that do not start the web app. Pass the storefront URL as the first argument
# after run-web.sh is up, for example:
#   ./scripts/verify-demo.sh http://localhost:8082
set -euo pipefail
source "$(dirname "$0")/common.sh"

echo "Redis hot-list keys:"
for i in $(seq 0 9); do
  val="$(compose exec -T redis redis-cli GET "$i" | tr -d '\r')"
  echo "  $i=${val}"
done

echo "HBase con row count (scan is capped by the shell):"
compose exec -T hbase bash -lc "echo \"count 'con', INTERVAL => 100000, CACHE => 1000\" | /opt/hbase/bin/hbase shell -n" || true

base="${1:-}"
if [ -n "$base" ]; then
  url="${base%/}/recommand?userId=1"
  echo "GET $url"
  code="$(curl -sS -o /tmp/recommand-page.html -w '%{http_code}' "$url")"
  echo "HTTP $code"
  if [ "$code" != "200" ]; then
    echo "Recommendation page did not return 200. Body:" >&2
    head -c 2000 /tmp/recommand-page.html >&2 || true
    exit 1
  fi
  if ! grep -q "热度榜" /tmp/recommand-page.html; then
    echo "Page did not contain 热度榜" >&2
    exit 1
  fi
  if ! grep -q "product-item" /tmp/recommand-page.html; then
    echo "Page did not render any product cards" >&2
    exit 1
  fi
  echo "Recommendation page rendered product cards."
fi
