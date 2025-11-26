# ⚡ Azure Deployment - Quick Reference

## 🚀 Fast Track Deployment (30 minutes)

### **Prerequisites Check**
```bash
# Check if Azure CLI is installed
az --version

# If not installed:
brew install azure-cli  # macOS
```

---

## 📝 Step-by-Step Commands

### **1. Login to Azure**
```bash
az login
# Browser opens - sign in with Microsoft account
```

### **2. Create Resource Group**
```bash
az group create \
  --name chat-app-resources \
  --location eastus
```

### **3. Build Backend**
```bash
cd backend
./mvnw clean package -DskipTests
cd ..
```

### **4. Create App Service Plan**
```bash
az appservice plan create \
  --name chat-backend-plan \
  --resource-group chat-app-resources \
  --sku B1 \
  --is-linux
```

### **5. Create Web App**
```bash
WEBAPP_NAME="chat-backend-$(date +%s)"
az webapp create \
  --resource-group chat-app-resources \
  --plan chat-backend-plan \
  --name $WEBAPP_NAME \
  --runtime "JAVA:17-java17"
```

**Save the $WEBAPP_NAME!**

### **6. Deploy Backend**
```bash
az webapp deploy \
  --resource-group chat-app-resources \
  --name $WEBAPP_NAME \
  --type jar \
  --src-path backend/target/chat-backend-0.0.1-SNAPSHOT.jar
```

### **7. Set Environment Variables**
```bash
az webapp config appsettings set \
  --resource-group chat-app-resources \
  --name $WEBAPP_NAME \
  --settings \
    AI_API_KEY="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" \
    AI_API_URL="https://openrouter.ai/api/v1/chat/completions" \
    AI_MODEL="anthropic/claude-3-haiku" \
    SPRING_PROFILES_ACTIVE="production"
```

### **8. Test Backend**
```bash
curl https://$WEBAPP_NAME.azurewebsites.net/api/health
```

Should return: `{"status":"UP","message":"Backend is running!"}`

### **9. Build Frontend**
```bash
cd frontend
echo "REACT_APP_API_URL=https://$WEBAPP_NAME.azurewebsites.net/api" > .env.production
npm run build
cd ..
```

### **10. Create Static Web App**
```bash
STATIC_APP_NAME="chat-frontend-$(date +%s)"
az staticwebapp create \
  --name $STATIC_APP_NAME \
  --resource-group chat-app-resources \
  --location "East US 2" \
  --sku Free
```

### **11. Update CORS**
```bash
FRONTEND_URL="https://$STATIC_APP_NAME.azurestaticapps.net"
az webapp config appsettings set \
  --resource-group chat-app-resources \
  --name $WEBAPP_NAME \
  --settings FRONTEND_URL="$FRONTEND_URL"
```

### **12. Deploy Frontend**
- Go to Azure Portal: https://portal.azure.com
- Find your Static Web App
- Upload files from `frontend/build/` folder

---

## ✅ Verify Deployment

### **Backend Health Check**
```bash
curl https://$WEBAPP_NAME.azurewebsites.net/api/health
```

### **View Backend Logs**
```bash
az webapp log tail \
  --resource-group chat-app-resources \
  --name $WEBAPP_NAME
```

### **Test Chat Endpoint**
```bash
curl -X POST https://$WEBAPP_NAME.azurewebsites.net/api/chat \
  -H "Content-Type: application/json" \
  -d '{"message":"Hello from Azure!"}'
```

---

## 🔄 Quick Redeploy

**Backend:**
```bash
cd backend
./mvnw clean package -DskipTests
az webapp deploy \
  --resource-group chat-app-resources \
  --name $WEBAPP_NAME \
  --type jar \
  --src-path target/chat-backend-0.0.1-SNAPSHOT.jar
```

**Frontend:**
```bash
cd frontend
npm run build
# Then upload build/ folder via Azure Portal
```

---

## 🗑️ Cleanup (Delete Everything)

```bash
az group delete \
  --name chat-app-resources \
  --yes
```

This deletes all resources and stops all charges.

---

## 📊 URLs to Save

After deployment:
- **Backend:** `https://$WEBAPP_NAME.azurewebsites.net`
- **Frontend:** `https://$STATIC_APP_NAME.azurestaticapps.net`
- **Portal:** https://portal.azure.com

---

## 🎯 Or Use the Automated Script

```bash
./deploy-to-azure.sh
```

This script does everything automatically!

---

**For detailed explanations, see:** `AZURE_DEPLOYMENT_STEP_BY_STEP.md`

