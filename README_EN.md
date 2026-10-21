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

| Tool | Version |
| --- | --- |
| JDK | **11 or 17** to run jobs and the web app. A newer JDK can compile (`maven.compiler.release` is 11). **Do not run the Flink 1.18 jobs on JDK 21.** CI uses Temurin 17. |
| Maven | 3.8+ |
| Docker | Docker Engine and Compose v2 (`docker compose`, not the `docker-compose` binary). Only required for the bundled stack. |
| Memory | Leave at least 6 GB for the containers and the jobs. |
| Ports | `9092` Kafka, `2181` / `16000` / `16010` / `16020` / `16030` HBase, `6379` Redis, `3308` MySQL, `8081` Flink UI, `8082` web. |

Flink stays on 1.18, the newest 1.x line that still accepts the demo's DataStream calls (`keyBy(String)`, `timeWindow`, `TimeCharacteristic`). Flink 1.19+ and Flink 2.0 remove those APIs.

You can point `flink-2-hbase/src/main/resources/config.properties` and `web/src/main/resources/application.yml` at Kafka, HBase, Redis, and MySQL you already run. The steps below use the compose file in this repo.

## Quick start

The old README sent people to an external "article" (`xinze.fun`) for deployment. That URL now redirects to an unrelated site, so it is gone. The Jianshu and CSDN notes described a different, manual image setup and are not linked here either. This section is the deployment guide.

Chinese: [README.md](README.md).

### 1. Start dependencies (one command)

From the repository root:

```bash
./scripts/bootstrap.sh
```

That runs `docker compose -f resources/docker/docker-compose/docker-compose.yml up -d --build` and waits until:

| Service | Address from the host | What the first boot does |
| --- | --- | --- |
| Kafka 3.9.2 (KRaft) | `localhost:9092` | `kafka-init` creates topic `con` (1 partition). Inside the compose network the broker is `kafka:19092`. |
| HBase 2.6.7 | ZooKeeper port `2181`, UI <http://localhost:16010> | Standalone, ZooKeeper embedded. `hbase_ini.sql` creates `con`, `prod`, `user`, `u_history`, `p_history`, `ps`, `px`, `u_interest`. The client quorum is `127.0.0.1`. `localhost` often also resolves to `::1`, the IPv6 connection is reset, and the client never comes up. ZooKeeper inside the container listens on `0.0.0.0` so the published port is reachable. |
| Redis 7.4 | `localhost:6379` | |
| MySQL 8.0 | `localhost:3308` | Database `con`, user `root`, password `123456`. `contact.sql` loads `contact`, `product`, and `user`. The host port is **3308**; inside the container MySQL still listens on 3306. |
| Flink 1.18.1 | UI <http://localhost:8081> | JobManager and one TaskManager. |

There are no data volumes. `docker compose down` deletes the data; the next `up` initializes again. `restart` keeps it.

Kafka, Redis, and MySQL in the config files are `localhost` on the host. The HBase ZooKeeper address is `127.0.0.1` (see the table). **Do not `flink run` the jobs into the compose TaskManager.** Inside that container `localhost` is the container itself, so the job cannot see Kafka, HBase, Redis, or MySQL. The script in the next section runs the jobs on the host network instead. The Flink UI is still useful to see that the cluster process is up.

### 2. Build

```bash
mvn -B package
```

The reactor builds `flink-2-hbase`, then `web`. Unit tests for age buckets, log parsing, and the two scoring formulas run with the build. Older tests that open MySQL, Redis, or HBase are excluded. See [CONTRIBUTING.md](CONTRIBUTING.md).

### 3. Start the six Flink jobs

Start the jobs before you produce logs. Consumers begin at the **latest** offset, so earlier messages are skipped.

```bash
./scripts/run-flink-jobs.sh
```

On Linux the script starts one `flink:1.18.1` container with `--network host` (Java 11) and launches all six mains. On other systems, with JDK 11 or 17 installed, it uses `java -cp` on the host. Logs:

```bash
docker logs -f flink-demo-jobs
```

| Class | Role | Output |
| --- | --- | --- |
| `com.demo.task.LogTask` | Raw logs | HBase `con` |
| `com.demo.task.UserHistoryTask` | User-product and product-user history | HBase `u_history`, `p_history` |
| `com.demo.task.UserInterestTask` | Interest | HBase `u_interest` |
| `com.demo.task.UserPortraitTask` | User portrait (reads MySQL `product`) | HBase `user` |
| `com.demo.task.ProductProtaritTask` | Product portrait (reads MySQL `user`) | HBase `prod` |
| `com.demo.task.TopProductTask` | 60s sliding hot list, 5s slide | Redis keys `0`..`4` |

A log line is `userId,productId,epochSeconds,action`. `action` is `1` browse, `2` favorite, `3` purchase.

The hot list uses event time. A window is 60 seconds, so a handful of messages all stamped "now" will not fill a window. The default generator spreads timestamps across the last 100 seconds.

Related-product scores (`px`, `ps`) come from `com.demo.scheduler.SchedulerJob`, a local timer, not a Flink job. It reads `p_history`. Until it runs, the second and third columns on the page still list the hot products, without extra neighbors. Optional:

```bash
java -cp flink-2-hbase/target/flink-2-hbase-1.0-SNAPSHOT.jar com.demo.scheduler.SchedulerJob
```

Stop the streaming jobs with `./scripts/stop-flink-jobs.sh`.

### 4. Generate logs

Wait until the job logs show the tasks starting (about half a minute), then:

```bash
./scripts/generate-logs.sh
```

That sends 200 messages. Product ids are concentrated on 11–15, which exist in both MySQL tables, and id 11 is the most frequent, so Redis key `0` should become `11`. Continuous random logs:

```bash
./scripts/generate-logs.sh --follow
```

`resources/docker/docker-compose/generator.sh` is mounted into the Kafka container at `/opt/generator.sh` (`MODE=follow` loops, `MODE=once` sends one burst).

The browse / favorite / buy buttons call `GET /log?id=&prod=&action=`, and the web app publishes that line to topic `con`.

### 5. Fill the hot-list keys and open the site

`TopProductTask` writes Redis keys `0` through `4`. The page reads `0` through `9`. With fewer than 10 keys the page pads from the `product` table and then indexes into `contact` with that longer list. Product ids 1 and 2 have no `contact` row, and the page returns 500. The page also matches each id by position in the contact and product query results, and those queries return one row per id. A repeated id makes those lists shorter than Redis and throws `IndexOutOfBoundsException`. The script fills empty keys and replaces an id that already appears in an earlier rank (starting at 20, which exists in both tables). Ranks the hot-list job already wrote stay as they are:

```bash
./scripts/seed-hotlist.sh
./scripts/run-web.sh
```

`run-web.sh` prefers JDK 17 when `JAVA_HOME` is unset and that JDK is installed. Do not run `mvn spring-boot:run` from the repository root. The aggregator POM is not a Spring Boot project, so the plugin is missing, and invoking it with `-am` also targets `flink-2-hbase`, which has many `main` methods. The script installs the Flink module, then runs Spring Boot only on `web`.

Open:

- Recommendations: <http://localhost:8082/recommand?userId=1>
- Admin hot list: <http://localhost:8082/index>

The page has three columns (hot list, item-CF, product portrait) and product names. Picture URLs are old external links; a broken image does not mean the list failed.

```bash
./scripts/verify-demo.sh http://localhost:8082
```

After the hot-list job finishes, `GET 0` in Redis should be `11`, and HBase `con` should contain log rows.

### Troubleshooting

1. **The old article redirects somewhere unrelated.** Ignore it. Start at `./scripts/bootstrap.sh`.
2. **`docker compose up` returns, and the site has no data.** `up -d` does not wait for the MySQL import or the HBase shell. Use the bootstrap script, or check `docker ps` health and `docker logs mysql`, `docker logs hbase`, `docker logs kafka-init`.
3. **Port already allocated.** MySQL is published on **3308**, not 3306. Stop the other process using the port in the table above.
4. **HBase client times out, port `2181` resets, or it dials `localhost:9000`.** RPC and scanner timeouts in `config.properties` are 60s, the quorum is `127.0.0.1`, and `hbase.rootdir` is no longer aimed at a NameNode this stack does not run. `echo ruok | nc -4 127.0.0.1 2181` should print `imok`. The RegionServer advertises `localhost`, so clients must run on the host (or with `--network host` on Linux), not on the compose bridge network.
5. **A job submitted in the Flink UI never prints anything.** `localhost:9092` inside the TaskManager is not the host's Kafka. Use `./scripts/run-flink-jobs.sh`.
6. **Jobs exit immediately on JDK 21.** Run them on JDK 11 or 17. Compiling with 21 is fine.
7. **Logs were sent, but Redis has no key `0`.** The jobs must be running before the producer. Timestamps must cover about 60 seconds of event time. Read `docker logs flink-demo-jobs` for Kafka or HBase errors.
8. **`/recommand` returns 500** (`IndexOutOfBoundsException`, or a null `contact`). Run `./scripts/seed-hotlist.sh`. All 10 keys must be set, the ids must be unique, and each id must exist in both `product` and `contact` (sample ids start at 3; the filler starts at 20).
9. **`/recommand` returns 500 because Thymeleaf rejects string concatenation in `th:onclick`.** Boot 2.7's Thymeleaf does not allow that in an event attribute. The buttons now use `data-*` attributes and still call the same `sendLog`. Do not put `th:onclick="'javascript:sendLog('+${userId}+..."` back.
10. **Names show, pictures do not.** The image hosts are external and many of those URLs are dead.
11. **Edits to `contact.sql` or the HBase script did nothing.** Init runs only on an empty data directory. `docker compose -f resources/docker/docker-compose/docker-compose.yml down`, then `./scripts/bootstrap.sh`.
12. **A firewall or security group blocks the ports.** Compose publishes the ports on `0.0.0.0`. Open the ones you actually use.
13. **`kafka-init` never exits, its logs stay empty, and the Flink UI shows no TaskManager.** Containers cannot reach each other. Published host ports still answer, so this looks like a Kafka or Flink failure. On Linux check both `iptables` and `iptables-legacy` `FORWARD`. A `DROP` policy that only accepts `docker0` drops the compose bridge. Allow that bridge, then `docker restart kafka-init`, and confirm `docker logs flink-taskmanager` shows the worker registered.
14. **`mvn spring-boot:run` fails.** The root POM has no such plugin. With full coordinates and `-am`, the plugin also runs on `flink-2-hbase` and reports that it cannot pick a single main class. Use `./scripts/run-web.sh`.

## What is intentionally unchanged

Recommendation rules, table layouts, and the web flows are the same. Dependency and connector upgrades are listed in the pull request that introduced this file. Spring Boot stays on 2.7 because Boot 3 switches the application to Jakarta EE. Scoring formulas still live in `Similarity.productPortraitScore` and `Similarity.itemCfScore`. The hot-rank sink still writes `SET <rank> <productId>` in Redis.
