#!/bin/bash
# Start Backend Locally

echo "🚀 Starting Spring Boot Backend..."
echo ""

# Set API key
export AI_API_KEY="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"
export APP_URL="http://localhost:3000"

echo "✅ Environment variables set"
echo ""

# Start backend
./mvnw spring-boot:run
