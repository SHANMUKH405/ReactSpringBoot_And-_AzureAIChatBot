# ☁️ Azure Deployment Guide

This guide will help you deploy your full-stack application to Microsoft Azure.

---

## 📋 Prerequisites

1. **Azure Account** - Sign up at [azure.microsoft.com](https://azure.microsoft.com/)
   - Free tier available with $200 credit
   
2. **Azure CLI** - Install from [docs.microsoft.com](https://docs.microsoft.com/cli/azure/install-azure-cli)
   ```bash
   az --version  # Verify installation
   ```

3. **Git** (for CI/CD if using)

---

## 🚀 Deployment Steps

### Step 1: Login to Azure

```bash
az login
# Opens browser for authentication
```

### Step 2: Create Resource Group

A resource group is a container for Azure resources.

```bash
az group create \
  --name myChatAppResourceGroup \
  --location eastus
```

### Step 3: Deploy Backend (Spring Boot)

#### Option A: Using Azure App Service (Recommended)

1. **Create App Service Plan** (defines the infrastructure):
```bash
az appservice plan create \
  --name myChatBackendPlan \
  --resource-group myChatAppResourceGroup \
  --sku B1 \
  --is-linux
```

2. **Create Web App** (your backend application):
```bash
az webapp create \
  --resource-group myChatAppResourceGroup \
  --plan myChatBackendPlan \
  --name my-chat-backend-$(date +%s) \
  --runtime "JAVA:17-java17"
```

3. **Build and Deploy**:
```bash
cd backend
mvn clean package -DskipTests

# Deploy JAR file
az webapp deploy \
  --resource-group myChatAppResourceGroup \
  --name <your-webapp-name> \
  --type jar \
  --src-path target/chat-backend-0.0.1-SNAPSHOT.jar
```

4. **Set Environment Variables**:
```bash
az webapp config appsettings set \
  --resource-group myChatAppResourceGroup \
  --name <your-webapp-name> \
  --settings \
    AI_API_KEY="your-openai-key" \
    AI_API_URL="https://api.openai.com/v1/chat/completions" \
    AI_MODEL="gpt-3.5-turbo" \
    SPRING_PROFILES_ACTIVE="production"
```

5. **Update CORS** (in `application.properties` or via Azure Portal):
   - Go to Azure Portal → Your Web App → Configuration → Application settings
   - Add: `spring.web.cors.allowed-origins` = `https://your-frontend-url.azurestaticapps.net`

#### Option B: Using Azure Container Instances (Advanced)

If you prefer Docker containers, see Dockerfile in the project.

---

### Step 4: Deploy Frontend (React)

#### Option A: Azure Static Web Apps (Recommended)

1. **Install Static Web Apps CLI**:
```bash
npm install -g @azure/static-web-apps-cli
```

2. **Create Static Web App**:
```bash
az staticwebapp create \
  --name my-chat-frontend \
  --resource-group myChatAppResourceGroup \
  --location eastus2 \
  --sku Free
```

3. **Build Frontend**:
```bash
cd frontend
npm install
npm run build
```

4. **Update API URL** in `frontend/src/services/api.js`:
```javascript
const API_BASE_URL = 'https://your-backend-url.azurewebsites.net/api';
```

Or use environment variable:
```bash
# Create .env.production
echo "REACT_APP_API_URL=https://your-backend-url.azurewebsites.net/api" > .env.production
```

5. **Deploy**:
```bash
# Get deployment token from Azure Portal
# Azure Portal → Static Web App → Deployment token

swa deploy ./build \
  --deployment-token <your-deployment-token> \
  --env production
```

#### Option B: Azure Storage Static Website (Simple)

1. **Create Storage Account**:
```bash
az storage account create \
  --name mychatappstorage$(date +%s) \
  --resource-group myChatAppResourceGroup \
  --location eastus \
  --sku Standard_LRS
```

2. **Enable Static Website**:
```bash
az storage blob service-properties update \
  --account-name <storage-account-name> \
  --static-website \
  --404-document index.html \
  --index-document index.html
```

3. **Upload Files**:
```bash
az storage blob upload-batch \
  --account-name <storage-account-name> \
  --source ./frontend/build \
  --destination '$web' \
  --overwrite
```

4. **Get Website URL**:
```bash
az storage account show \
  --name <storage-account-name> \
  --resource-group myChatAppResourceGroup \
  --query "primaryEndpoints.web" \
  --output tsv
```

---

## 🔧 Configuration

### Backend Configuration (Azure App Service)

1. **Set Java Version**:
```bash
az webapp config set \
  --resource-group myChatAppResourceGroup \
  --name <your-webapp-name> \
  --java-version 17 \
  --java-container Tomcat \
  --java-container-version 10
```

2. **Enable Always On** (prevents cold starts):
```bash
az webapp config set \
  --resource-group myChatAppResourceGroup \
  --name <your-webapp-name> \
  --always-on true
```

3. **Set Startup Command**:
```bash
az webapp config set \
  --resource-group myChatAppResourceGroup \
  --name <your-webapp-name> \
  --startup-file "java -jar /home/site/wwwroot/chat-backend-0.0.1-SNAPSHOT.jar"
```

### Frontend Configuration

Create `frontend/.env.production`:
```env
REACT_APP_API_URL=https://your-backend-url.azurewebsites.net/api
```

---

## 🔐 Security Best Practices

1. **Use Environment Variables** - Never commit API keys
2. **Enable HTTPS** - Azure provides SSL certificates
3. **Configure CORS Properly** - Only allow your frontend domain
4. **Use Azure Key Vault** - For sensitive credentials:
```bash
az keyvault create \
  --name myChatKeyVault \
  --resource-group myChatAppResourceGroup \
  --location eastus

# Store secret
az keyvault secret set \
  --vault-name myChatKeyVault \
  --name ai-api-key \
  --value "your-api-key"
```

---

## 📊 Monitoring & Logs

### View Application Logs

```bash
az webapp log tail \
  --resource-group myChatAppResourceGroup \
  --name <your-webapp-name>
```

### Enable Application Insights (Monitoring)

```bash
az monitor app-insights component create \
  --app myChatAppInsights \
  --location eastus \
  --resource-group myChatAppResourceGroup

# Link to your web app
az monitor app-insights component connect-webapp \
  --app myChatAppInsights \
  --resource-group myChatAppResourceGroup \
  --web-app <your-webapp-name>
```

---

## 💰 Cost Optimization

1. **Use Free Tier** - Azure Static Web Apps has a free tier
2. **Choose Right SKU** - Start with B1 (Basic) for backend
3. **Auto-scale** - Configure auto-scaling rules
4. **Monitor Costs** - Use Azure Cost Management

---

## 🐛 Troubleshooting

### Backend Not Starting

1. Check logs:
```bash
az webapp log tail --resource-group myChatAppResourceGroup --name <your-webapp-name>
```

2. Verify Java version:
```bash
az webapp config show --resource-group myChatAppResourceGroup --name <your-webapp-name> --query "javaVersion"
```

3. Check environment variables are set correctly

### Frontend Can't Connect to Backend

1. Verify CORS configuration
2. Check backend URL is correct
3. Verify backend is running (check health endpoint)
4. Check browser console for errors

### API Keys Not Working

1. Verify keys are set in Azure Portal
2. Check keys are correct (no extra spaces)
3. Verify API keys have credits/permissions

---

## 🎯 Quick Deployment Script

Save this as `deploy.sh`:

```bash
#!/bin/bash

# Configuration
RESOURCE_GROUP="myChatAppResourceGroup"
BACKEND_NAME="my-chat-backend"
FRONTEND_NAME="my-chat-frontend"
LOCATION="eastus"

# Login (uncomment if needed)
# az login

# Create resource group
az group create --name $RESOURCE_GROUP --location $LOCATION

# Deploy backend
cd backend
mvn clean package -DskipTests
# ... (add backend deployment commands)

# Deploy frontend
cd ../frontend
npm run build
# ... (add frontend deployment commands)

echo "✅ Deployment complete!"
```

---

## 📚 Additional Resources

- [Azure App Service Docs](https://docs.microsoft.com/azure/app-service/)
- [Azure Static Web Apps Docs](https://docs.microsoft.com/azure/static-web-apps/)
- [Azure CLI Reference](https://docs.microsoft.com/cli/azure/)

---

## ✅ Deployment Checklist

- [ ] Azure account created
- [ ] Azure CLI installed
- [ ] Resource group created
- [ ] Backend deployed and running
- [ ] Frontend deployed and running
- [ ] Environment variables configured
- [ ] CORS configured
- [ ] HTTPS enabled
- [ ] Monitoring enabled
- [ ] Tested end-to-end

**Good luck with your deployment! 🚀**

