# =============================================================================
# Site Service Dockerfile
# =============================================================================
FROM eclipse-temurin:17-jdk-alpine AS build

WORKDIR /workspace

COPY ../pom.xml            ./parent-pom.xml
COPY ../shared/pom.xml     ./shared/pom.xml
COPY pom.xml               ./site-service/pom.xml
COPY ../shared/src         ./shared/src

RUN --mount=type=cache,target=/root/.m2 \
    mvn -f parent-pom.xml -pl shared -am dependency:go-offline -B -q

COPY src                   ./site-service/src
RUN --mount=type=cache,target=/root/.m2 \
    mvn -f parent-pom.xml -pl shared,site-service -am clean package -DskipTests -B -q

RUN java -Djarmode=layertools \
         -jar site-service/target/site-service.jar extract \
         --destination /workspace/layers

# ---------------------------------------------------------------------------
FROM eclipse-temurin:17-jre-alpine AS runtime

ARG BUILD_DATE
ARG GIT_COMMIT
ARG VERSION="1.0.0-SNAPSHOT"

LABEL org.opencontainers.image.title="Logistics Site Service" \
      org.opencontainers.image.created="${BUILD_DATE}"         \
      org.opencontainers.image.revision="${GIT_COMMIT}"

RUN addgroup -S logistics && adduser -S logistics -G logistics
WORKDIR /app

COPY --from=build --chown=logistics:logistics /workspace/layers/dependencies/          ./
COPY --from=build --chown=logistics:logistics /workspace/layers/spring-boot-loader/    ./
COPY --from=build --chown=logistics:logistics /workspace/layers/snapshot-dependencies/ ./
COPY --from=build --chown=logistics:logistics /workspace/layers/application/           ./

USER logistics
EXPOSE 8082
EXPOSE 8092

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+ExitOnOutOfMemoryError"
ENV SPRING_PROFILES_ACTIVE="production"

HEALTHCHECK --interval=20s --timeout=5s --start-period=60s --retries=3 \
  CMD wget -qO- http://localhost:8092/actuator/health/readiness \
      | grep -q '"status":"UP"' || exit 1

ENTRYPOINT ["sh", "-c", \
  "exec java ${JAVA_OPTS} org.springframework.boot.loader.launch.JarLauncher"]
