$ErrorActionPreference = 'Stop'

$mavenVersion = '3.9.9'
$toolsDir = Join-Path $env:USERPROFILE 'tools'
$mavenDir = Join-Path $toolsDir "apache-maven-$mavenVersion"
$mavenBin = Join-Path $mavenDir 'bin'
$zipPath = Join-Path $toolsDir "apache-maven-$mavenVersion-bin.zip"

if (-not (Test-Path $mavenBin)) {
    New-Item -ItemType Directory -Path $toolsDir -Force | Out-Null
    Invoke-WebRequest -Uri "https://archive.apache.org/dist/maven/maven-3/$mavenVersion/binaries/apache-maven-$mavenVersion-bin.zip" -OutFile $zipPath
    Expand-Archive -Path $zipPath -DestinationPath $toolsDir -Force
}

$env:Path = "$mavenBin;" + $env:Path
Set-Location $PSScriptRoot

mvn spring-boot:run "-Dspring-boot.run.profiles=local"
