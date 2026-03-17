# =============================================================================
# Multi-stage Dockerfile — Logistics Barycenter Application
#
# Stage 1 (angular-build): compiles the Angular 17 SPA to a production bundle.
# Stage 2 (spring-build):  copies the Angular dist into Spring Boot static
#                           resources and packages the fat JAR with Maven.
# Stage 3 (runtime):       minimal JRE-only image with the fat JAR.
#
# The Angular SPA is served by Spring Boot's ResourceHttpRequestHandler from
# classpath:/static/ — no separate Nginx container is needed for production.
# A standalone Nginx container is provided via docker-compose for local dev
# if a developer prefers the Angular CLI dev-server workflow.
#
# Build arguments (all optional; consumed as OCI image labels):
#   BUILD_DATE   — ISO-8601 build timestamp
#   GIT_COMMIT   — full Git SHA
#   GIT_BRANCH   — branch name
#   VERSION      — release version tag
# =============================================================================

# ─────────────────────────────────────────────────────────────────────────────
# Stage 1 — Angular production build
# ─────────────────────────────────────────────────────────────────────────────
FROM node:20.14-alpine AS angular-build

WORKDIR /app/angular

# Copy package manifests first for layer-cached npm install
COPY src/ui/angular-app/package.json \
     src/ui/angular-app/package-lock.json ./

# Install exact dependencies from lock file; skip audit to keep CI fast
RUN npm ci --prefer-offline --no-audit

# Copy the rest of the Angular sources
COPY src/ui/angular-app/ .

# Production build — output hashing enabled via angular.json production config
RUN npx ng build --configuration production \
        --output-path dist/logistics-barycenter-spa \
    && echo "Angular build complete." \
    && du -sh dist/logistics-barycenter-spa/browser

# ─────────────────────────────────────────────────────────────────────────────
# Stage 2 — Spring Boot fat JAR build
# ─────────────────────────────────────────────────────────────────────────────
FROM eclipse-temurin:17-jdk-alpine AS spring-build

WORKDIR /app/spring

# Copy Maven wrapper and POM first for dependency cache layer
COPY logistics-api/.mvn .mvn
COPY logistics-api/mvnw logistics-api/pom.xml ./

# Pre-fetch all dependencies (layer cached unless pom.xml changes)
RUN ./mvnw dependency:go-offline -B --no-transfer-progress -q

# Copy application source
COPY logistics-api/src ./src

# Copy Angular build output into Spring Boot static resources
COPY --from=angular-build \
     /app/angular/dist/logistics-barycenter-spa/browser/ \
     ./src/main/resources/static/

# Package fat JAR — tests are skipped here (run separately in CI test stage)
RUN ./mvnw clean package -DskipTests -B --no-transfer-progress \
    && echo "Fat JAR size:" \
    && du -sh target/logistics-api.jar

# ─────────────────────────────────────────────────────────────────────────────
# Stage 3 — Minimal runtime image
# ─────────────────────────────────────────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine AS runtime

# Build-time labels (OCI image spec)
ARG BUILD_DATE
ARG GIT_COMMIT
ARG GIT_BRANCH
ARG VERSION="1.0.0-SNAPSHOT"

LABEL org.opencontainers.image.title="Logistics Barycenter API" \
      org.opencontainers.image.description="Spring Boot REST API + Angular SPA for logistics barycenter calculation" \
      org.opencontainers.image.vendor="Define Company" \
      org.opencontainers.image.created="${BUILD_DATE}" \
      org.opencontainers.image.revision="${GIT_COMMIT}" \
      org.opencontainers.image.version="${VERSION}"

# Create a non-root user — never run as root in production
RUN addgroup -S logistics && adduser -S logistics -G logistics

WORKDIR /app

# Copy only the fat JAR from the build stage
COPY --from=spring-build --chown=logistics:logistics \
     /app/spring/target/logistics-api.jar ./logistics-api.jar

# Switch to non-root user
USER logistics

# Expose the port defined in application.yml
EXPOSE 8080

# Health check using the Spring Boot Actuator health endpoint
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD wget -qO- http://localhost:8080/actuator/health | grep -q '"status":"UP"' || exit 1

# JVM tuning for containerised environments:
#   - UseContainerSupport: honour cgroup memory/CPU limits
#   - MaxRAMPercentage:    use up to 75 % of container memory for heap
#   - ExitOnOutOfMemoryError: crash fast instead of degrading silently
#   - SPRING_PROFILES_ACTIVE: activate production profile by default;
#     override with -e SPRING_PROFILES_ACTIVE=dev for local dev containers
ENV JAVA_OPTS="-XX:+UseContainerSupport \
               -XX:MaxRAMPercentage=75.0 \
               -XX:+ExitOnOutOfMemoryError \
               -Djava.security.egd=file:/dev/./urandom \
               -Dfile.encoding=UTF-8"
ENV SPRING_PROFILES_ACTIVE="production"

ENTRYPOINT ["sh", "-c", "exec java ${JAVA_OPTS} -jar /app/logistics-api.jar"]
