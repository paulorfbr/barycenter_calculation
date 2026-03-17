# =============================================================================
# API Gateway Dockerfile — reactive, uses WebFlux (no Tomcat)
# =============================================================================
FROM eclipse-temurin:17-jdk-alpine AS build

WORKDIR /workspace

COPY ../pom.xml          ./parent-pom.xml
COPY pom.xml             ./api-gateway/pom.xml

RUN --mount=type=cache,target=/root/.m2 \
    mvn -f parent-pom.xml -pl api-gateway -am dependency:go-offline -B -q

COPY src                 ./api-gateway/src
RUN --mount=type=cache,target=/root/.m2 \
    mvn -f parent-pom.xml -pl api-gateway -am clean package -DskipTests -B -q

RUN java -Djarmode=layertools \
         -jar api-gateway/target/api-gateway.jar extract \
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
EXPOSE 8080
EXPOSE 8090

ENV JAVA_OPTS="\
  -XX:+UseContainerSupport \
  -XX:MaxRAMPercentage=65.0 \
  -XX:+ExitOnOutOfMemoryError \
  -Djava.security.egd=file:/dev/./urandom"
ENV SPRING_PROFILES_ACTIVE="production"

HEALTHCHECK --interval=15s --timeout=5s --start-period=30s --retries=3 \
  CMD wget -qO- http://localhost:8090/actuator/health/readiness \
      | grep -q '"status":"UP"' || exit 1

ENTRYPOINT ["sh", "-c", \
  "exec java ${JAVA_OPTS} org.springframework.boot.loader.launch.JarLauncher"]
