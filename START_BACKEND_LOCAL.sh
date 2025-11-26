#!/bin/bash
# Start Backend Locally - Simple Script

cd "$(dirname "$0")/backend"

echo "🚀 Starting Spring Boot Backend Locally..."
echo ""

# Set API key for local development
export AI_API_KEY="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"
export APP_URL="http://localhost:3002"

echo "✅ Environment variables set:"
echo "   - AI_API_KEY: ${AI_API_KEY:0:20}..."
echo "   - APP_URL: $APP_URL"
echo ""

# Check if port 8080 is in use
if lsof -i :8080 > /dev/null 2>&1; then
    echo "⚠️  Port 8080 is already in use!"
    echo "   Stop the existing process or use a different port"
    exit 1
fi

echo "📦 Starting backend with Maven wrapper..."
echo "   This may take 30-60 seconds on first run"
echo ""

# Start backend
./mvnw spring-boot:run

