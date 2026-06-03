#!/usr/bin/env bash
set -euo pipefail

echo "=== Paper Crown - Setup ==="

# Check Java
if ! command -v java &> /dev/null; then
    echo "ERROR: Java 21+ is required. Install it via: brew install openjdk@21"
    exit 1
fi

JAVA_VER=$(java -version 2>&1 | head -1 | sed 's/.*version "//; s/\..*//')
if [ "$JAVA_VER" -lt 21 ]; then
    echo "ERROR: Java 21+ required, found $JAVA_VER"
    exit 1
fi
echo "[OK] Java version: $(java -version 2>&1 | head -1)"

# Check Docker
if ! command -v docker &> /dev/null; then
    echo "ERROR: Docker is required. Install from https://docker.com"
    exit 1
fi
echo "[OK] Docker available"

# Start database
echo ""
echo "Starting PostgreSQL via Docker Compose..."
docker compose -f docker/docker-compose.yml up -d postgres

echo ""
echo "Waiting for database to be ready..."
until docker compose -f docker/docker-compose.yml exec -T postgres pg_isready -U papercrown &> /dev/null; do
    sleep 1
done
echo "[OK] Database ready"

echo ""
echo "=== Setup complete ==="
echo ""
echo "Start the backend:  ./gradlew :backend-service:bootRun"
echo "Start the desktop:  ./gradlew :desktop-client:run"
echo "pgAdmin:           http://localhost:5050 (admin@papercrown.local / admin)"
echo ""
