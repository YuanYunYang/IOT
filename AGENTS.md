# AGENTS.md

## Cursor Cloud specific instructions

### Overview

This is a Java 8 / Maven multi-module IoT Platform using Spring Boot 2.7, Spring Cloud 2021, and Spring Cloud Alibaba. All runtime configuration is externalized to **Nacos** (service discovery + config center) — there are no `application.yml` files in `src/main/resources`.

### System prerequisites

- **JDK 8** (`openjdk-8-jdk`) — must be the active Java; set `JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64`
- **Maven 3.6+** (system package `maven`)
- **Docker** — used to run MySQL 8, Redis 7, and Nacos v2.3.2

### Infrastructure (Docker containers)

Start infrastructure before any service:

```bash
docker start mysql8 redis nacos 2>/dev/null || true
```

If containers don't exist yet, create them:

```bash
docker run -d --name mysql8 -e MYSQL_ROOT_PASSWORD=123456 -p 3306:3306 mysql:8.0 --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci
docker run -d --name redis -p 6379:6379 redis:7
docker run -d --name nacos -e MODE=standalone -e NACOS_AUTH_ENABLE=false -p 8848:8848 -p 9848:9848 nacos/nacos-server:v2.3.2
```

Required MySQL databases: `iot_user`, `iot_device`, `iot_alarm`, `iot_core`, `iot_aiot`.

### Nacos configuration

Configs are published to Nacos default (public) namespace, group `iot-group`. Template files are in `nacos-config/*.yml`. For local dev, all service addresses must point to `127.0.0.1` (MySQL on 3306, Redis on 6379, Nacos on 8848). Use `--data-urlencode` when publishing via `curl` to avoid JDBC URL truncation from `&` characters.

Kafka, ClickHouse, XXL-JOB, MQTT, and Milvus are all **disabled** in local dev configs (`iot.middleware.kafka.enabled: false`, `iot.clickhouse.enabled: false`, etc.).

### Building

```bash
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
mvn -DskipTests clean package -pl '!iot-service-aiot'
```

The `iot-service-aiot` module has a Milvus SDK compatibility issue (`SearchResults` class missing in `milvus-sdk-java:2.3.11`). Exclude it from builds with `-pl '!iot-service-aiot'`.

### Running services

Each service must override bootstrap Nacos address (defaults point to external `192.168.1.37`):

```bash
java -jar <module>/target/<module>-1.0.0-SNAPSHOT.jar \
  --spring.profiles.active=dev \
  --spring.cloud.nacos.discovery.server-addr=127.0.0.1:8848 \
  --spring.cloud.nacos.config.server-addr=127.0.0.1:8848 \
  --spring.cloud.nacos.discovery.namespace= \
  --spring.cloud.nacos.config.namespace= \
  --spring.cloud.nacos.username=nacos \
  --spring.cloud.nacos.password=nacos
```

**Service ports** (from Nacos config): gateway=8180, user=8081, device=8182, alarm=8183, core=8184, aiot=8185.

### Testing

No automated test suite exists in this repository (`src/test/` is empty). Verify services via:
- Health check: `curl http://localhost:<port>/actuator/health`
- Login: `POST http://localhost:8180/api/v1/user/auth/login` with `{"tenantCode":"demo-corp","username":"admin","password":"admin123"}`

### Key gotchas

- The `bootstrap-dev.yml` files in each service hardcode Nacos address `192.168.1.37:8848` and namespace UUID `b3335e90-1fde-4ed3-b12e-03fa11b9c8dc`. Always override these via command-line args for local development.
- Nacos configs must be URL-encoded when publishing via `curl -X POST` to avoid JDBC `&` truncation.
- Redis runs without a password in local dev (unlike the template configs which specify a password).
- The gateway's `RewritePath` strips the service prefix: e.g. `/api/v1/user/auth/login` becomes `/api/v1/auth/login` in the user service.
