# =============================================================================
# Company Service — multi-stage Dockerfile
#
# Stage 1 (build):    Maven layered JAR build
# Stage 2 (runtime):  Minimal JRE with extracted layers for optimal cache reuse
#
# Layer extraction strategy (Spring Boot layered JAR):
#   Layer 1 — dependencies          (3rd-party libraries, rarely changed)
#   Layer 2 — spring-boot-loader    (loader classes, stable)
#   Layer 3 — snapshot-dependencies (SNAPSHOT jars, occasionally changed)
#   Layer 4 — application           (your compiled classes, changes on every build)
#
# This means Docker only re-pulls Layer 4 on most builds, greatly reducing
# CI/CD image push time and storage costs.
# =============================================================================

# ─────────────────────────────────────────────────────────────────────────────
# Stage 1 — Maven build
# ─────────────────────────────────────────────────────────────────────────────
FROM eclipse-temurin:17-jdk-alpine AS build

WORKDIR /workspace

# Cache Maven dependencies before copying source
COPY ../pom.xml                 ./parent-pom.xml
COPY ../shared/pom.xml          ./shared/pom.xml
COPY pom.xml                    ./company-service/pom.xml

# Pre-fetch dependencies
COPY ../shared/src              ./shared/src
RUN --mount=type=cache,target=/root/.m2 \
    mvn -f parent-pom.xml -pl shared -am dependency:go-offline -B -q

# Build shared library and company service
COPY src                        ./company-service/src
RUN --mount=type=cache,target=/root/.m2 \
    mvn -f parent-pom.xml -pl shared,company-service -am clean package -DskipTests -B -q

# Extract layered JAR
RUN java -Djarmode=layertools \
         -jar company-service/target/company-service.jar extract \
         --destination /workspace/layers

# ─────────────────────────────────────────────────────────────────────────────
# Stage 2 — Minimal runtime
# ─────────────────────────────────────────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine AS runtime

ARG BUILD_DATE
ARG GIT_COMMIT
ARG VERSION="1.0.0-SNAPSHOT"

LABEL org.opencontainers.image.title="Logistics Company Service" \
      org.opencontainers.image.created="${BUILD_DATE}"           \
      org.opencontainers.image.revision="${GIT_COMMIT}"          \
      org.opencontainers.image.version="${VERSION}"

# Non-root user
RUN addgroup -S logistics && adduser -S logistics -G logistics

WORKDIR /app

# Copy layers in order of change frequency (least → most)
COPY --from=build --chown=logistics:logistics /workspace/layers/dependencies/          ./
COPY --from=build --chown=logistics:logistics /workspace/layers/spring-boot-loader/    ./
COPY --from=build --chown=logistics:logistics /workspace/layers/snapshot-dependencies/ ./
COPY --from=build --chown=logistics:logistics /workspace/layers/application/           ./

USER logistics

EXPOSE 8081
EXPOSE 8091

ENV JAVA_OPTS="\
  -XX:+UseContainerSupport \
  -XX:MaxRAMPercentage=75.0 \
  -XX:+ExitOnOutOfMemoryError \
  -Djava.security.egd=file:/dev/./urandom \
  -Dfile.encoding=UTF-8"
ENV SPRING_PROFILES_ACTIVE="production"

HEALTHCHECK --interval=20s --timeout=5s --start-period=60s --retries=3 \
  CMD wget -qO- http://localhost:8091/actuator/health/readiness \
      | grep -q '"status":"UP"' || exit 1

ENTRYPOINT ["sh", "-c", \
  "exec java ${JAVA_OPTS} org.springframework.boot.loader.launch.JarLauncher"]
