# This script checks if the MySQL container is running, starts it if not,
# fetches its port, and runs the DataPopulationServiceIntegrationTest.

# ==============================================================================
# WARNING: THIS SCRIPT WAS AI GENERATED, AND THE HUMAN WHO VALIDATED DOESN'T HAVE
# THE EXPERTISE IN POWERSHELL TO ENSURE ITS CORRECTNESS. USE WITH CAUTION.
# ==============================================================================


# Reference: https://docs.docker.com/compose/reference/
# Reference: https://maven.apache.org/surefire/maven-surefire-plugin/examples/single-test.html

$containerName = "autodiagnostico-db"

# Get the root directory
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$rootDir = (Get-Item "$scriptDir\..\..").FullName
$backendDir = "$rootDir\backend"

Write-Host "Checking if MySQL container '$containerName' is running..."

# Check if container is running
$isRunning = docker inspect -f '{{.State.Running}}' $containerName 2>$null

if ($isRunning -ne "true") {
    Write-Host "MySQL is not running. Starting it via Docker Compose..."
    Set-Location $rootDir
    docker compose up -d mysql
    
    # Wait for the container to start
    Write-Host "Waiting for MySQL to initialize..."
    Start-Sleep -Seconds 10
} else {
    Write-Host "MySQL is already running."
}

# Fetch the host port mapped to 3306
# Reference: https://docs.docker.com/engine/reference/commandline/port/
$portInfo = docker port $containerName 3306
if ($portInfo -match ':(?<port>\d+)') {
    $port = $Matches.port
} else {
    Write-Host "Error: Could not fetch MySQL port. Is the container running?"
    exit 1
}

Write-Host "MySQL is accessible on localhost:$port"

# Run the integration test using Maven
# We override the datasource URL to ensure it matches the fetched port
Write-Host "Running DataPopulationServiceIntegrationTest..."
Set-Location $backendDir
mvn clean test -Dtest=DataPopulationServiceIntegrationTest "-Dspring.datasource.url=jdbc:mysql://localhost:$port/autodiagnostico?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true"
