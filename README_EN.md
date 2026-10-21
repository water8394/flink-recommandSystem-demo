# Real-time product recommendation demo

[中文](README.md) · [Contributing](CONTRIBUTING.md) · [Apache-2.0](LICENSE)

Flink demo that turns product behavior logs into live recommendations. Kafka carries the logs. Flink jobs build user and product portraits, a hot list, and history for item-based collaborative filtering. Portraits and scores land in HBase. The hot list is cached in Redis. A Spring Boot site re-ranks that list for the caller and attaches related products.

Copyright 2019-2026 **water8394 (XInZE)**. Licensed under the [Apache License 2.0](LICENSE).

## Modules

| Module | Role |
| --- | --- |
| `flink-2-hbase` | Six Flink jobs: browse history, interest, user portrait, product portrait, hot ranking, and raw log import. Offline helpers score product relatedness. |
| `web` | Storefront recommendations and an admin screen. MySQL holds the product catalog. |

The hot-rank job windows events by product, keeps a top list, and writes `SET <rank> <productId>` into Redis. Related-product scores use the portrait formula in `Similarity.productPortraitScore` and the item-CF formula in `Similarity.itemCfScore`.

## Requirements

- JDK 11 or newer (CI uses Temurin 17). Flink stays on 1.18, the newest 1.x line that still accepts the demo's DataStream calls (`keyBy(String)`, `timeWindow`, `TimeCharacteristic`). Flink 1.19+ and Flink 2.0 remove those APIs.
- Maven 3.8+
- Docker, only if you want the local dependency stack

## Build

From the repository root:

```bash
mvn -B package
```

The reactor builds `flink-2-hbase` first, then `web`. Unit tests for age buckets, log parsing, and the two scoring formulas run with the build. Older tests that open MySQL, Redis, or HBase are excluded by default because they need those services. See [CONTRIBUTING.md](CONTRIBUTING.md).

## Local dependencies

```bash
docker compose -f resources/docker/docker-compose/docker-compose.yml up -d --build
```

| Service | Address from the host | Notes |
| --- | --- | --- |
| Kafka (KRaft) | `localhost:9092` | Inside the compose network use `kafka:19092`. Topic `con` is auto-created. |
| HBase 2.6 | ZooKeeper `localhost:2181`, UI `localhost:16010` | Standalone, so ZooKeeper is embedded. Tables come from `hbase_ini.sql` on first boot. |
| Redis 7.4 | `localhost:6379` | |
| MySQL 8.0 | `localhost:3308` | Database `con`, user `root`, password `123456`. `contact.sql` is loaded on first start. |
| Flink 1.18 | UI `localhost:8081` | JobManager and one TaskManager. |

`config.properties` and `application.yml` point Kafka at `localhost:9092` so a process on the host matches this compose file. A job submitted inside the Flink container needs `kafka:19092` instead.

HBase shell statements also live in `resources/sql/`. The web app listens on port `8082`.

## What is intentionally unchanged

Recommendation rules, table layouts, and the web flows are the same. Dependency and connector upgrades are listed in the pull request that introduced this file. Spring Boot stays on 2.7 because Boot 3 switches the application to Jakarta EE.
