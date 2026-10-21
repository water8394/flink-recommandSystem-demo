#!/usr/bin/env bash
# Start the storefront on http://localhost:8082
set -euo pipefail
source "$(dirname "$0")/common.sh"
cd "$ROOT"

if [ -z "${JAVA_HOME:-}" ] && [ -d /usr/lib/jvm/java-17-openjdk-amd64 ]; then
  export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
  export PATH="$JAVA_HOME/bin:$PATH"
fi

major="$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d. -f1)"
echo "Using Java $major (${JAVA_HOME:-PATH})"
if [ "$major" != "11" ] && [ "$major" != "17" ]; then
  echo "Warning: this app and the HBase client were checked on JDK 11 and 17. JDK $major may fail at runtime." >&2
fi

# The aggregator POM is not a Spring Boot project, so `mvn spring-boot:run`
# from the repository root cannot see the plugin. Install the Flink module,
# then run only the web module.
mvn -pl flink-2-hbase -am install -DskipTests
exec mvn -pl web org.springframework.boot:spring-boot-maven-plugin:2.7.18:run -DskipTests
