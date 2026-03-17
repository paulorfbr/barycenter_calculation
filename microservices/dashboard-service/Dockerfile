# =============================================================================
# Dashboard Service Dockerfile
# =============================================================================
FROM eclipse-temurin:17-jdk-alpine AS build

WORKDIR /workspace

COPY ../pom.xml                ./parent-pom.xml
COPY ../shared/pom.xml         ./shared/pom.xml
COPY pom.xml                   ./dashboard-service/pom.xml
COPY ../shared/src             ./shared/src

RUN --mount=type=cache,target=/root/.m2 \
    mvn -f parent-pom.xml -pl shared -am dependency:go-offline -B -q

COPY src                       ./dashboard-service/src
RUN --mount=type=cache,target=/root/.m2 \
    mvn -f parent-pom.xml -pl shared,dashboard-service -am clean package -DskipTests -B -q

RUN java -Djarmode=layertools \
         -jar dashboard-service/target/dashboard-service.jar extract \
         --destination /workspace/layers

# ---------------------------------------------------------------------------
FROM eclipse-temurin:17-jre-alpine AS runtime

RUN addgroup -S logistics && adduser -S logistics -G logistics
WORKDIR /app

COPY --from=build --chown=logistics:logistics /workspace/layers/dependencies/          ./
COPY --from=build --chown=logistics:logistics /workspace/layers/spring-boot-loader/    ./
COPY --from=build --chown=logistics:logistics /workspace/layers/snapshot-dependencies/ ./
COPY --from=build --chown=logistics:logistics /workspace/layers/application/           ./

USER logistics
EXPOSE 8084
EXPOSE 8094

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+ExitOnOutOfMemoryError"
ENV SPRING_PROFILES_ACTIVE="production"

HEALTHCHECK --interval=20s --timeout=5s --start-period=60s --retries=3 \
  CMD wget -qO- http://localhost:8094/actuator/health/readiness \
      | grep -q '"status":"UP"' || exit 1

ENTRYPOINT ["sh", "-c", \
  "exec java ${JAVA_OPTS} org.springframework.boot.loader.launch.JarLauncher"]
