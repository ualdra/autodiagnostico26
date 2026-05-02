#!/bin/bash

# This script checks if the MySQL container is running, starts it if not,
# fetches its port, and runs the DataPopulationServiceIntegrationTest.

# Reference: https://docs.docker.com/compose/reference/
# Reference: https://maven.apache.org/surefire/maven-surefire-plugin/examples/single-test.html

# Get the directory where the script is located
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "$SCRIPT_DIR/../../" && pwd)"
BACKEND_DIR="$ROOT_DIR"

CONTAINER_NAME="autodiagnostico-db"

echo "Checking if MySQL container '$CONTAINER_NAME' is running..."

# Check if the container is running
IS_RUNNING=$(sudo docker inspect -f '{{.State.Running}}' "$CONTAINER_NAME" 2>/dev/null)

if [ "$IS_RUNNING" != "true" ]; then
    echo "MySQL is not running. Starting it via Docker Compose..."
    cd "$ROOT_DIR"
    sudo docker compose up -d mysql
    
    # Wait for the container to start
    echo "Waiting for MySQL to initialize..."
    sleep 10
else
    echo "MySQL is already running."
fi

# Fetch the host port mapped to 3306
# Reference: https://docs.docker.com/engine/reference/commandline/port/
PORT_INFO=$(sudo docker port "$CONTAINER_NAME" 3306)
PORT=$(echo "$PORT_INFO" | grep -oE '[0-9]+$' | head -1)

if [ -z "$PORT" ]; then
    echo "Error: Could not fetch MySQL port. Is the container running?"
    exit 1
fi

echo "MySQL is accessible on localhost:$PORT"

# Run the integration test using Maven
# We override the datasource URL to ensure it matches the fetched port
echo "Running DataPopulationServiceIntegrationTest..."
cd "$BACKEND_DIR"
mvn clean test -Dtest=DataPopulationServiceIntegrationTest \
    -Dspring.datasource.url="jdbc:mysql://localhost:$PORT/autodiagnostico?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true"
