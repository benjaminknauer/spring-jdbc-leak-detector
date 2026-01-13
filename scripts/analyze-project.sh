#!/bin/bash
set -e

PROJECT_DIR="${1:-.}"
PROJECT_KEY="${2:-my-project}"

# Resolve to absolute path
PROJECT_DIR="$(cd "$PROJECT_DIR" && pwd)"

if [ ! -f "$PROJECT_DIR/pom.xml" ]; then
    echo "Error: No pom.xml found in $PROJECT_DIR"
    echo "Usage: $0 <project-directory> [project-key]"
    exit 1
fi

echo "========================================"
echo "Analyzing project: $PROJECT_DIR"
echo "Project key: $PROJECT_KEY"
echo "========================================"

cd "$PROJECT_DIR"

mvn org.sonarsource.scanner.maven:sonar-maven-plugin:sonar \
    -Dsonar.projectKey="$PROJECT_KEY" \
    -Dsonar.projectName="$PROJECT_KEY" \
    -Dsonar.host.url=http://localhost:9000 \
    -Dsonar.login=admin \
    -Dsonar.password=test

echo ""
echo "========================================"
echo "Analysis complete!"
echo "View results: http://localhost:9000/dashboard?id=$PROJECT_KEY"
echo "========================================"
