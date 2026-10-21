FROM eclipse-temurin:11-jre-jammy

ARG HBASE_VERSION=2.6.7
ENV HBASE_HOME=/opt/hbase
ENV PATH=${HBASE_HOME}/bin:${PATH}

RUN apt-get update \
    && apt-get install -y --no-install-recommends curl ca-certificates \
    && curl -fsSL "https://archive.apache.org/dist/hbase/${HBASE_VERSION}/hbase-${HBASE_VERSION}-bin.tar.gz" -o /tmp/hbase.tgz \
    && tar -xzf /tmp/hbase.tgz -C /opt \
    && mv "/opt/hbase-${HBASE_VERSION}" "${HBASE_HOME}" \
    && rm /tmp/hbase.tgz \
    && apt-get purge -y curl \
    && apt-get autoremove -y \
    && rm -rf /var/lib/apt/lists/*

COPY hbase-site.xml ${HBASE_HOME}/conf/hbase-site.xml
COPY docker-entrypoint.sh /docker-entrypoint.sh
RUN chmod +x /docker-entrypoint.sh

EXPOSE 2181 16000 16010 16020 16030

ENTRYPOINT ["/docker-entrypoint.sh"]
