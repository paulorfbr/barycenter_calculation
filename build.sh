#!/bin/bash
# =============================================================================
# Full project build script
#
# Builds the Angular SPA and copies the output into the Spring Boot static
# resources directory, then builds the fat JAR.
#
# Usage:
#   ./build.sh           — build Angular + Spring Boot
#   ./build.sh --api     — build Spring Boot only (Angular already built)
#   ./build.sh --ui      — build Angular only
#   ./build.sh --run     — build everything then start the server on :8080
# =============================================================================

set -e

PROJECT_ROOT="$(cd "$(dirname "$0")" && pwd)"
ANGULAR_DIR="$PROJECT_ROOT/src/ui/angular-app"
SPRING_DIR="$PROJECT_ROOT/logistics-api"
STATIC_DIR="$SPRING_DIR/src/main/resources/static"
ANGULAR_DIST="$ANGULAR_DIR/dist/logistics-barycenter-spa/browser"

BUILD_ANGULAR=true
BUILD_SPRING=true
RUN_SERVER=false

for arg in "$@"; do
  case $arg in
    --api)  BUILD_ANGULAR=false ;;
    --ui)   BUILD_SPRING=false  ;;
    --run)  RUN_SERVER=true     ;;
  esac
done

# ---- 1. Build Angular SPA ----
if [ "$BUILD_ANGULAR" = true ]; then
  echo ""
  echo ">>> Building Angular SPA..."
  cd "$ANGULAR_DIR"
  npm ci --silent
  npx ng build --configuration production --output-path "dist/logistics-barycenter-spa"
  echo ">>> Angular build complete."
fi

# ---- 2. Copy Angular build output to Spring Boot static resources ----
if [ "$BUILD_ANGULAR" = true ] && [ "$BUILD_SPRING" = true ]; then
  echo ""
  echo ">>> Copying Angular build to Spring Boot static resources..."
  rm -rf "$STATIC_DIR"
  mkdir -p "$STATIC_DIR"
  cp -r "$ANGULAR_DIST/." "$STATIC_DIR/"
  echo ">>> Copied $(ls "$STATIC_DIR" | wc -l) files to $STATIC_DIR"
fi

# ---- 3. Build Spring Boot fat JAR ----
if [ "$BUILD_SPRING" = true ]; then
  echo ""
  echo ">>> Building Spring Boot application..."
  cd "$SPRING_DIR"
  ./mvnw clean package -DskipTests
  echo ">>> Spring Boot build complete: $SPRING_DIR/target/logistics-api.jar"
fi

# ---- 4. Optionally start the server ----
if [ "$RUN_SERVER" = true ]; then
  echo ""
  echo ">>> Starting Spring Boot server on http://localhost:8080 ..."
  echo ">>> Press Ctrl+C to stop."
  cd "$SPRING_DIR"
  java -jar target/logistics-api.jar
fi
