#!/bin/bash
# Quick environment setup script
# Run this before starting the backend: source setup-env.sh

export JAVA_HOME="/opt/homebrew/opt/openjdk@17"
export PATH="/opt/homebrew/opt/openjdk@17/bin:$PATH"

echo "✅ Java environment configured!"
echo "JAVA_HOME: $JAVA_HOME"
echo "Java version: $(java -version 2>&1 | head -1)"
echo "Javac version: $(javac -version 2>&1)"

