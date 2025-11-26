#!/bin/bash
# Script to start backend with correct Java version

export JAVA_HOME="/opt/homebrew/opt/openjdk@17"
export PATH="/opt/homebrew/opt/openjdk@17/bin:$PATH"

echo "✅ Starting backend with Java 17..."
echo "JAVA_HOME: $JAVA_HOME"
echo "Java version: $(java -version 2>&1 | head -1)"
echo ""

cd "$(dirname "$0")"
./mvnw spring-boot:run

