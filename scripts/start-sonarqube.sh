#!/bin/bash
set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"

cd "$PROJECT_DIR"

echo "Building plugin..."
mvn clean package -DskipTests

echo "Starting SonarQube with plugin..."
echo ""
echo "========================================"
echo "SonarQube is starting (logs below)"
echo "========================================"
echo "URL:   http://localhost:9000"
echo "Login: admin"
echo "Pass:  admin (change on first login)"
echo ""
echo "Press Ctrl+C to stop SonarQube"
echo "========================================"
echo ""

docker-compose -f "$PROJECT_DIR/docker-compose.yml" up
