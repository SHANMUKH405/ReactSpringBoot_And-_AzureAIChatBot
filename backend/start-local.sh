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
