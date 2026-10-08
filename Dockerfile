# syntax=docker/dockerfile:1

# ---------- Стадия 1: сборка JAR внутри контейнера ----------
FROM maven:3.8.7-eclipse-temurin-11 AS build
WORKDIR /build

# Сначала только pom.xml: слой с зависимостями кешируется и не перекачивается,
# пока не изменился pom (правки в src его не инвалидируют)
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2,sharing=locked mvn -B -q dependency:go-offline

COPY src ./src
# Тесты гоняются в CI/локально, в образ их прогон не нужен
RUN --mount=type=cache,target=/root/.m2,sharing=locked mvn -B -q package -DskipTests \
    && cp target/*.jar app.jar \
    # Spring Boot layered jar: зависимости и код раскладываются по отдельным слоям образа
    && java -Djarmode=layertools -jar app.jar extract --destination extracted

# ---------- Стадия 2: минимальный runtime-образ ----------
FROM eclipse-temurin:11-jre

# Приложение не должно работать от root
RUN groupadd --system spring && useradd --system --gid spring spring
WORKDIR /app

# От редко меняющихся слоёв к часто меняющимся — пересобирается только последний
COPY --from=build /build/extracted/dependencies/ ./
COPY --from=build /build/extracted/spring-boot-loader/ ./
COPY --from=build /build/extracted/snapshot-dependencies/ ./
COPY --from=build /build/extracted/application/ ./

USER spring

# JVM подстраивает heap под лимит памяти контейнера
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0"
# Логи только в stdout/stderr — их собирает docker (docker logs / compose logs)
ENV LOGGING_FILE_NAME=""

EXPOSE 9000

HEALTHCHECK --interval=10s --timeout=3s --start-period=60s --retries=5 \
    CMD curl -fsS http://localhost:9000/actuator/health || exit 1

# exec: java становится PID 1 и корректно получает SIGTERM при docker stop
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS org.springframework.boot.loader.JarLauncher"]
