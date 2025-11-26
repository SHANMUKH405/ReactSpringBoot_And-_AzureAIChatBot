# ☁️ Azure Deployment - Complete Step-by-Step Guide

## 🎯 Overview

This guide will help you deploy your chat application to Microsoft Azure:
- **Backend** (Spring Boot) → Azure App Service
- **Frontend** (React) → Azure Static Web Apps

**Time Required:** 1-2 hours  
**Difficulty:** Beginner-friendly with detailed instructions

---

## 📋 Prerequisites Checklist

Before starting, make sure you have:

- [ ] **Azure Account** (Free tier available - $200 credit for 30 days)
- [ ] **Azure CLI** installed (we'll install if needed)
- [ ] **Git** installed
- [ ] **Your application working locally** (backend and frontend both running)

---

## 🚀 Step 1: Install Azure CLI

Azure CLI lets you control Azure from your terminal.

### **On macOS:**

```bash
brew install azure-cli
```

### **Verify Installation:**

```bash
az --version
```

You should see the Azure CLI version. If not, install it from: https://docs.microsoft.com/cli/azure/install-azure-cli

---

## 🔐 Step 2: Login to Azure

1. **Open terminal and run:**
   ```bash
   az login
   ```

2. **A browser window will open:**
   - Sign in with your Microsoft account
   - If you don't have an account, create one at [azure.microsoft.com](https://azure.microsoft.com/free/)
   - The free tier gives you $200 credit for 30 days

3. **Verify login:**
   ```bash
   az account show
   ```
   
   You should see your subscription information.

**What this does:** Connects your terminal to your Azure account so you can create and manage resources.

---

## 📦 Step 3: Create Azure Resource Group

A resource group is like a folder that holds all your Azure resources.

**Run this command:**

```bash
az group create \
  --name chat-app-resources \
  --location eastus
```

**What this does:**
- Creates a container named `chat-app-resources`
- Sets location to `eastus` (US East - you can change to `westus`, `westeurope`, etc.)

**Expected Output:**
```json
{
  "id": "/subscriptions/.../resourceGroups/chat-app-resources",
  "location": "eastus",
  "name": "chat-app-resources",
  "properties": {
    "provisioningState": "Succeeded"
  }
}
```

✅ **Success!** Your resource group is created.

---

## 🔧 Step 4: Prepare Backend for Deployment

### **4.1: Build the JAR File**

```bash
cd backend
./mvnw clean package -DskipTests
```

**What this does:**
- Cleans previous builds
- Compiles your code
- Creates JAR file: `target/chat-backend-0.0.1-SNAPSHOT.jar`
- Skips tests (faster build)

**Expected Output:**
```
[INFO] BUILD SUCCESS
[INFO] JAR file created: target/chat-backend-0.0.1-SNAPSHOT.jar
```

✅ **JAR file is ready!**

### **4.2: Verify JAR File Exists**

```bash
ls -lh target/chat-backend-*.jar
```

You should see a JAR file (around 50-80 MB).

---

## ☁️ Step 5: Deploy Backend to Azure App Service

### **5.1: Create App Service Plan**

An App Service Plan defines the infrastructure (CPU, memory, etc.) for your app.

```bash
az appservice plan create \
  --name chat-backend-plan \
  --resource-group chat-app-resources \
  --sku B1 \
  --is-linux
```

**What this does:**
- Creates a hosting plan named `chat-backend-plan`
- Uses `B1` tier (Basic - ~$13/month, but you have free credits)
- Linux-based (required for Java 17)

**Parameters explained:**
- `--sku B1`: Basic tier (1 CPU, 1.75GB RAM) - good for development
- `--is-linux`: Linux OS (required for Java apps)

**Alternative (Free tier - limited features):**
```bash
az appservice plan create \
  --name chat-backend-plan \
  --resource-group chat-app-resources \
  --sku FREE \
  --is-linux
```

### **5.2: Create Web App**

This creates the actual web application container.

```bash
# Generate unique name (Azure requires unique names globally)
WEBAPP_NAME="chat-backend-$(date +%s)"

az webapp create \
  --resource-group chat-app-resources \
  --plan chat-backend-plan \
  --name $WEBAPP_NAME \
  --runtime "JAVA:17-java17"
```

**What this does:**
- Creates web app with unique name (using timestamp)
- Connects it to your App Service Plan
- Configures Java 17 runtime

**Important:** Save the `$WEBAPP_NAME` - you'll need it! Or check it:
```bash
echo $WEBAPP_NAME
```

### **5.3: Configure Java Version**

```bash
az webapp config set \
  --resource-group chat-app-resources \
  --name $WEBAPP_NAME \
  --java-version 17 \
  --java-container "Java SE" \
  --java-container-version "17"
```

### **5.4: Deploy Your JAR File**

```bash
az webapp deploy \
  --resource-group chat-app-resources \
  --name $WEBAPP_NAME \
  --type jar \
  --src-path target/chat-backend-0.0.1-SNAPSHOT.jar
```

**What this does:**
- Uploads your JAR file to Azure
- Azure automatically:
  - Extracts the JAR
  - Starts your Spring Boot application
  - Runs on port 80/443

**Expected Output:**
```
Deployment successful!
App URL: https://chat-backend-1234567890.azurewebsites.net
```

✅ **Backend deployed!** Note the URL - you'll need it!

---

## ⚙️ Step 6: Configure Backend Environment Variables

Your backend needs API keys and configuration. Set them in Azure:

```bash
az webapp config appsettings set \
  --resource-group chat-app-resources \
  --name $WEBAPP_NAME \
  --settings \
    AI_API_KEY="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" \
    AI_API_URL="https://openrouter.ai/api/v1/chat/completions" \
    AI_MODEL="anthropic/claude-3-haiku" \
    SPRING_PROFILES_ACTIVE="production" \
    SPRING_DATASOURCE_URL="jdbc:h2:mem:chatdb;DB_CLOSE_ON_EXIT=FALSE"
```

**What this does:**
- Sets environment variables
- Available to your app as `System.getenv("AI_API_KEY")`
- Spring Boot automatically reads them

**For production, use PostgreSQL instead of H2:**

```bash
# Option: Add PostgreSQL (better for production)
# We'll use H2 for now (simpler, but data resets on restart)
```

### **Update CORS for Production**

Create production profile file: `backend/src/main/resources/application-production.properties`

```bash
cat > backend/src/main/resources/application-production.properties << 'EOF'
# Production Configuration
server.port=${PORT:8080}

# AI API Configuration
ai.api.key=${AI_API_KEY}
ai.api.url=${AI_API_URL:https://openrouter.ai/api/v1/chat/completions}
ai.model.name=${AI_MODEL:anthropic/claude-3-haiku}
ai.api.timeout=30000

# CORS - Update with your frontend URL
spring.web.cors.allowed-origins=${FRONTEND_URL:https://your-frontend.azurestaticapps.net}
spring.web.cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS
spring.web.cors.allowed-headers=*

# Database - Using H2 in-memory (for simplicity)
# In production, use PostgreSQL or Azure SQL
spring.datasource.url=jdbc:h2:mem:chatdb;DB_CLOSE_ON_EXIT=FALSE
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# JPA Configuration
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false

# Logging
logging.level.com.chat=INFO
logging.level.org.springframework.web=WARN
EOF
```

### **Rebuild and Redeploy with Production Profile**

```bash
cd backend
./mvnw clean package -DskipTests
az webapp deploy \
  --resource-group chat-app-resources \
  --name $WEBAPP_NAME \
  --type jar \
  --src-path target/chat-backend-0.0.1-SNAPSHOT.jar
```

---

## 🧪 Step 7: Test Backend Deployment

### **7.1: Check Backend Health**

```bash
# Get your backend URL
BACKEND_URL="https://$WEBAPP_NAME.azurewebsites.net"

# Test health endpoint
curl $BACKEND_URL/api/health
```

**Expected Response:**
```json
{"status":"UP","message":"Backend is running!"}
```

✅ **Backend is live!**

### **7.2: View Backend Logs**

If there are issues, check logs:

```bash
az webapp log tail \
  --resource-group chat-app-resources \
  --name $WEBAPP_NAME
```

Press `Ctrl+C` to stop viewing logs.

---

## 🎨 Step 8: Prepare Frontend for Deployment

### **8.1: Build Frontend**

```bash
cd frontend
npm install  # If you haven't already
npm run build
```

**What this does:**
- Installs dependencies
- Builds optimized production version
- Creates `build/` folder with static files

### **8.2: Update API URL for Production**

Create `.env.production` file:

```bash
cat > frontend/.env.production << EOF
REACT_APP_API_URL=https://$WEBAPP_NAME.azurewebsites.net/api
EOF
```

**Rebuild frontend:**

```bash
npm run build
```

---

## 🌐 Step 9: Deploy Frontend to Azure Static Web Apps

### **9.1: Install Static Web Apps CLI**

```bash
npm install -g @azure/static-web-apps-cli
```

### **9.2: Create Static Web App**

```bash
cd frontend

az staticwebapp create \
  --name chat-frontend-$(date +%s) \
  --resource-group chat-app-resources \
  --location "East US 2" \
  --sku Free
```

**Note the output** - you'll see a URL like: `https://chat-frontend-xxxxx.azurestaticapps.net`

### **9.3: Deploy Frontend Files**

```bash
# If you're still in frontend directory
swa deploy ./build \
  --deployment-token <your-deployment-token> \
  --env production
```

**Get deployment token:**
```bash
az staticwebapp secrets list \
  --name <your-static-web-app-name> \
  --resource-group chat-app-resources \
  --query "properties.apiKey" \
  --output tsv
```

**Or use Azure Portal method (easier):**

1. Go to Azure Portal: https://portal.azure.com
2. Find your Static Web App resource
3. Go to "Overview" → Click "Browse"
4. Upload files from `frontend/build/` folder

---

## 🔄 Step 10: Update CORS Configuration

Your backend needs to allow requests from your frontend URL.

### **10.1: Get Frontend URL**

From Step 9.2, you have your frontend URL (something like `https://chat-frontend-xxxxx.azurestaticapps.net`)

### **10.2: Update Backend CORS**

```bash
az webapp config appsettings set \
  --resource-group chat-app-resources \
  --name $WEBAPP_NAME \
  --settings \
    SPRING_WEB_CORS_ALLOWED_ORIGINS="https://your-frontend-url.azurestaticapps.net"
```

**Or update in code and redeploy:**

Edit `backend/src/main/resources/application-production.properties`:
```properties
spring.web.cors.allowed-origins=${FRONTEND_URL:https://your-frontend-url.azurestaticapps.net}
```

Rebuild and redeploy.

---

## ✅ Step 11: Test Your Deployed Application

### **11.1: Test Backend**

```bash
# Health check
curl https://$WEBAPP_NAME.azurewebsites.net/api/health

# Test chat (should work)
curl -X POST https://$WEBAPP_NAME.azurewebsites.net/api/chat \
  -H "Content-Type: application/json" \
  -d '{"message":"Hello from Azure!"}'
```

### **11.2: Test Frontend**

1. Open your frontend URL in browser
2. Try to register/login
3. Send a chat message
4. Everything should work!

---

## 🔍 Step 12: View Your Resources in Azure Portal

1. **Go to:** https://portal.azure.com
2. **Search for:** "Resource groups"
3. **Click:** `chat-app-resources`
4. **You'll see:**
   - App Service Plan
   - Web App (backend)
   - Static Web App (frontend)

**View your apps:**
- Backend: Click Web App → "Browse" button
- Frontend: Click Static Web App → "Browse" button

---

## 📊 Deployment Summary

After deployment, you'll have:

```
┌─────────────────────────────────────┐
│         AZURE CLOUD                 │
│                                     │
│  ┌──────────────────────────────┐  │
│  │  Resource Group:             │  │
│  │  chat-app-resources          │  │
│  │                              │  │
│  │  ┌──────────────────────┐    │  │
│  │  │  App Service Plan    │    │  │
│  │  │  (chat-backend-plan) │    │  │
│  │  └──────────┬───────────┘    │  │
│  │             │                │  │
│  │  ┌──────────▼───────────┐    │  │
│  │  │  Web App (Backend)   │    │  │
│  │  │  Java 17             │    │  │
│  │  │  Port 8080           │    │  │
│  │  │  URL: ...azurewebsites.net││  │
│  │  └──────────────────────┘    │  │
│  │                              │  │
│  │  ┌──────────────────────┐    │  │
│  │  │  Static Web App      │    │  │
│  │  │  (Frontend)          │    │  │
│  │  │  React Build         │    │  │
│  │  │  URL: ...azurestaticapps.net││
│  │  └──────────────────────┘    │  │
│  └──────────────────────────────┘  │
└─────────────────────────────────────┘
```

---

## 🐛 Troubleshooting

### **Problem: Backend won't start**

**Check logs:**
```bash
az webapp log tail --resource-group chat-app-resources --name $WEBAPP_NAME
```

**Common issues:**
- Java version mismatch
- Missing environment variables
- Port configuration

### **Problem: Frontend can't connect to backend**

**Check:**
1. Backend URL is correct in frontend
2. CORS is configured correctly
3. Backend is running (check health endpoint)

**Update CORS:**
```bash
az webapp config appsettings set \
  --resource-group chat-app-resources \
  --name $WEBAPP_NAME \
  --settings \
    SPRING_WEB_CORS_ALLOWED_ORIGINS="https://your-frontend-url.azurestaticapps.net"
```

### **Problem: Environment variables not working**

**Check current settings:**
```bash
az webapp config appsettings list \
  --resource-group chat-app-resources \
  --name $WEBAPP_NAME
```

**Update settings:**
```bash
az webapp config appsettings set \
  --resource-group chat-app-resources \
  --name $WEBAPP_NAME \
  --settings KEY="value"
```

### **Problem: Database not persisting**

**Why:** H2 in-memory database resets on restart

**Solution:** Use Azure Database for PostgreSQL (production-ready):
- We can set this up if needed
- For now, H2 is fine for learning

---

## 💰 Cost Management

### **Current Setup Costs:**

- **App Service Plan (B1):** ~$13/month
- **Static Web App (Free tier):** $0/month
- **Total:** ~$13/month (covered by free credits)

### **Reduce Costs:**

1. **Use Free App Service Plan:**
   ```bash
   # Delete current plan
   az appservice plan delete \
     --name chat-backend-plan \
     --resource-group chat-app-resources
   
   # Create free plan
   az appservice plan create \
     --name chat-backend-plan \
     --resource-group chat-app-resources \
     --sku FREE \
     --is-linux
   ```

2. **Stop resources when not using:**
   ```bash
   az webapp stop \
     --resource-group chat-app-resources \
     --name $WEBAPP_NAME
   ```

3. **Delete resources when done:**
   ```bash
   az group delete \
     --name chat-app-resources \
     --yes
   ```

---

## 🔄 Quick Redeploy Commands

**Redeploy Backend:**
```bash
cd backend
./mvnw clean package -DskipTests
az webapp deploy \
  --resource-group chat-app-resources \
  --name $WEBAPP_NAME \
  --type jar \
  --src-path target/chat-backend-0.0.1-SNAPSHOT.jar
```

**Redeploy Frontend:**
```bash
cd frontend
npm run build
# Then deploy via Azure Portal or CLI
```

---

## 📝 Important URLs to Save

After deployment, save these URLs:

```
Backend URL: https://your-backend-name.azurewebsites.net
Frontend URL: https://your-frontend-name.azurestaticapps.net
Azure Portal: https://portal.azure.com
Resource Group: chat-app-resources
```

---

## ✅ Deployment Checklist

- [ ] Azure CLI installed
- [ ] Logged in to Azure
- [ ] Resource group created
- [ ] Backend JAR built
- [ ] App Service Plan created
- [ ] Web App created
- [ ] Backend deployed
- [ ] Environment variables set
- [ ] Backend tested (health check works)
- [ ] Frontend built
- [ ] Static Web App created
- [ ] Frontend deployed
- [ ] CORS updated
- [ ] Full application tested
- [ ] URLs saved

---

## 🎯 Next Steps After Deployment

1. **Test all features** - Register, login, chat, conversations
2. **Monitor logs** - Check for any errors
3. **Share your app** - Send URL to friends/family
4. **Add to resume** - "Deployed full-stack app to Azure"
5. **Interview talking point** - "I deployed my project to Azure"

---

**Your app is now live on the cloud! 🎉**

Need help with any step? Just ask!

