# Contributing

Thanks for helping with this demo. The project is maintained by **water8394 (XInZE)** and licensed under the [Apache License 2.0](LICENSE).

## 贡献说明

欢迎提交 issue 和 pull request。请保持推荐逻辑、表结构和页面行为不变，除非改动本身就是为了修复缺陷。讨论和提交说明使用中文或英文都可以。

## Build

Java 11 or newer and Maven 3.8 or newer:

```bash
mvn -B package
```

GitHub Actions runs the same command on every push and pull request (Temurin 17).

## Tests

Default `mvn package` runs unit tests that do not need Kafka, HBase, Redis, or MySQL:

- `SimilarityTest` — product-portrait score and item-CF score
- `AgeUtilTest` — age buckets
- `LogToEntityTest` — log line parsing

`HbaseTest`, `ProductDaoTest`, and `RedisClientTest` start clients or the Spring context and are excluded from Surefire. Run them only after the compose stack and the web datasource are up, by deleting the `<excludes>` block in the module pom for that run.

## Local stack

```bash
./scripts/bootstrap.sh
```

That starts the compose stack and waits until MySQL, HBase, and Kafka topic `con` are ready. The full path (jobs, sample logs, web app, and the failures people actually hit) is in the Chinese [README.md](README.md) and in [README_EN.md](README_EN.md). Start the site with `./scripts/run-web.sh`, not `mvn spring-boot:run` from the repository root. Do not follow the old external deployment article; that link has been removed.

## Pull requests

- Keep the diff focused. Do not reformat unrelated files.
- If you bump a dependency, say which version changed and why a newer major was skipped.
- `mvn -B package` should pass before you open the request.
