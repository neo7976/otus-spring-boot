# syntax=docker/dockerfile:1

# ---------- Стадия 1: сборка JAR внутри контейнера ----------
FROM maven:3.8.7-eclipse-temurin-11 AS build
WORKDIR /build

# Контекст сборки — корень репозитория (нужен общий модуль kafka-config)
COPY kafka-config ./kafka-config
RUN --mount=type=cache,target=/root/.m2,sharing=locked mvn -B -q -f kafka-config/pom.xml install

COPY rating-service/pom.xml .
RUN --mount=type=cache,target=/root/.m2,sharing=locked mvn -B -q dependency:go-offline

COPY rating-service/src ./src
RUN --mount=type=cache,target=/root/.m2,sharing=locked mvn -B -q package -DskipTests \
    && cp target/*.jar app.jar \
    && java -Djarmode=layertools -jar app.jar extract --destination extracted

# ---------- Стадия 2: минимальный runtime-образ ----------
FROM eclipse-temurin:11-jre

RUN groupadd --system spring && useradd --system --gid spring spring
WORKDIR /app

COPY --from=build /build/extracted/dependencies/ ./
COPY --from=build /build/extracted/spring-boot-loader/ ./
COPY --from=build /build/extracted/snapshot-dependencies/ ./
COPY --from=build /build/extracted/application/ ./

USER spring

ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0"

EXPOSE 9100

HEALTHCHECK --interval=10s --timeout=3s --start-period=60s --retries=5 \
    CMD curl -fsS http://localhost:9100/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS org.springframework.boot.loader.JarLauncher"]
