#!/usr/bin/env bash
# The storefront reads Redis keys "0".."9".
# TopProductTask only writes keys 0-4. With fewer than 10 keys the page pads
# from MySQL product ids that have no contact row, and /recommand returns 500.
# It also looks up each id by position in the contact/product lists. Those
# queries return one row per id, so a repeated id makes the lists shorter
# than the Redis list and the page throws IndexOutOfBoundsException.
# Fill only empty keys, and replace a key whose id is already used by an
# earlier rank. Replacement ids start at 20, which exist in product and contact.
# Ranks the hot-list job already stored are left as they are.
set -euo pipefail
source "$(dirname "$0")/common.sh"

used=" "
next_id=20

for i in $(seq 0 9); do
  val="$(compose exec -T redis redis-cli GET "$i" | tr -d '\r')"
  if [ -n "$val" ] && [ "$val" != "(nil)" ] && [[ "$used" != *" $val "* ]]; then
    echo "keep $i=$val"
    used="$used$val "
    continue
  fi
  while [[ "$used" == *" $next_id "* ]]; do
    next_id=$((next_id + 1))
  done
  compose exec -T redis redis-cli SET "$i" "$next_id" >/dev/null
  echo "SET $i $next_id"
  used="$used$next_id "
  next_id=$((next_id + 1))
done
