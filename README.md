# 🤖 AI-Powered Chat Assistant
## Full-Stack Learning Project: React + Spring Boot + Azure

A complete full-stack application to learn modern web development with AI integration.

---

## 🎯 Project Overview

This project teaches you:
- ✅ **Frontend**: React with Ant Design (beautiful UI)
- ✅ **Backend**: Java Spring Boot (REST APIs)
- ✅ **API Integration**: External APIs (Weather, News, etc.)
- ✅ **AI Integration**: OpenAI/open-source AI models
- ✅ **Deployment**: Microsoft Azure cloud hosting

---

## 📋 Prerequisites

### Required Software
1. **Java 17+** - [Download](https://www.oracle.com/java/technologies/downloads/)
   ```bash
   java -version  # Should show Java 17 or higher
   ```

2. **Node.js 18+** - [Download](https://nodejs.org/)
   ```bash
   node -v  # Should show v18 or higher
   npm -v   # Should show npm version
   ```

3. **Maven** (comes with Spring Boot project, or install separately)
   ```bash
   mvn -v  # Check if installed
   ```

4. **Git** - [Download](https://git-scm.com/)
   ```bash
   git --version
   ```

5. **IDE** (Choose one):
   - **IntelliJ IDEA** (recommended for Java) - [Download](https://www.jetbrains.com/idea/)
   - **VS Code** - [Download](https://code.visualstudio.com/)

---

## 🚀 Quick Start Guide

### Step 1: Clone and Navigate
```bash
cd /Users/shannu405/Internship_Learning_Project
```

### Step 2: Start Backend (Spring Boot)
```bash
cd backend
./mvnw spring-boot:run
# Or on Windows: mvnw.cmd spring-boot:run
```

Backend runs on: `http://localhost:8080`

### Step 3: Start Frontend (React)
Open a **new terminal**:
```bash
cd frontend
npm install    # Install dependencies (first time only)
npm start      # Start development server
```

Frontend runs on: `http://localhost:3000`

### Step 4: Open Browser
Navigate to: `http://localhost:3000`

---

## 📁 Project Structure

```
Internship_Learning_Project/
│
├── backend/                    # Spring Boot Application
│   ├── src/main/java/com/chat/
│   │   ├── controller/         # API endpoints (receives HTTP requests)
│   │   │   └── ChatController.java
│   │   ├── service/            # Business logic
│   │   │   ├── ChatService.java
│   │   │   └── AIService.java  # AI integration
│   │   ├── model/              # Data structures
│   │   │   └── Message.java
│   │   ├── config/             # Configuration
│   │   │   └── CorsConfig.java # CORS settings
│   │   └── ChatApplication.java # Main class
│   ├── pom.xml                # Maven dependencies
│   └── application.properties  # App configuration
│
├── frontend/                   # React Application
│   ├── src/
│   │   ├── components/         # React components
│   │   │   ├── ChatBox.jsx     # Main chat UI
│   │   │   └── MessageBubble.jsx
│   │   ├── services/           # API communication
│   │   │   └── api.js          # HTTP requests to backend
│   │   ├── App.js              # Main component
│   │   └── index.js            # Entry point
│   ├── package.json            # npm dependencies
│   └── public/                 # Static files
│
├── ROADMAP.md                  # Learning roadmap
└── README.md                   # This file
```

---

## 🔧 Configuration

### Backend Configuration

Edit `backend/src/main/resources/application.properties`:

```properties
# Server Port
server.port=8080

# AI API Key (Get from OpenAI or Hugging Face)
ai.api.key=your-api-key-here
ai.api.url=https://api.openai.com/v1/chat/completions

# CORS (allows frontend to connect)
spring.web.cors.allowed-origins=http://localhost:3000
```

**Important**: Create a `.env` file or use environment variables for API keys in production!

### Frontend Configuration

Edit `frontend/src/services/api.js` to change backend URL:
```javascript
const API_BASE_URL = 'http://localhost:8080/api';
```

---

## 🎓 Learning Path

### **Lesson 1: Understanding the Architecture**
1. Frontend sends HTTP request → Backend receives it
2. Backend processes request → Calls AI API
3. Backend sends response → Frontend displays it

### **Lesson 2: Backend (Spring Boot)**
- `@RestController` = Handles HTTP requests
- `@Service` = Contains business logic
- `@GetMapping/@PostMapping` = Defines API endpoints
- Dependency Injection = Spring automatically creates objects

### **Lesson 3: Frontend (React)**
- `useState` = Manages component state (data that changes)
- `useEffect` = Runs code when component loads
- `fetch/axios` = Makes HTTP requests to backend
- Components = Reusable UI pieces

### **Lesson 4: API Integration**
- HTTP Methods: GET (fetch), POST (send data)
- JSON = Data format (JavaScript Object Notation)
- Async/Await = Handling asynchronous operations
- Error Handling = Try-catch blocks

### **Lesson 5: AI Integration**
- API Key = Authentication token
- Request Body = Data sent to AI API
- Response = AI-generated text
- Streaming = Real-time response updates

---

## 🔑 API Endpoints

### Backend Endpoints

#### 1. Health Check
```http
GET http://localhost:8080/api/health
Response: { "status": "UP" }
```

#### 2. Send Chat Message
```http
POST http://localhost:8080/api/chat
Content-Type: application/json

{
  "message": "Hello, how are you?",
  "conversationId": "optional-id"
}

Response:
{
  "response": "I'm doing well, thank you!",
  "conversationId": "abc123"
}
```

#### 3. Get Chat History
```http
GET http://localhost:8080/api/history/{conversationId}
Response: [ { "role": "user", "content": "Hello" }, ... ]
```

#### 4. External API Example - Weather
```http
GET http://localhost:8080/api/weather?city=London
Response: { "city": "London", "temperature": "15°C", ... }
```

---

## 🤖 AI Integration Setup

### Option 1: OpenAI (Recommended for learning)
1. Sign up at [OpenAI](https://platform.openai.com/)
2. Get API key from dashboard
3. Add to `application.properties`:
   ```properties
   ai.api.key=sk-your-key-here
   ai.api.url=https://api.openai.com/v1/chat/completions
   ```

### Option 2: OpenRouter (Open-source models)
1. Sign up at [OpenRouter](https://openrouter.ai/)
2. Get API key
3. Update URL to OpenRouter endpoint

### Option 3: Hugging Face
1. Sign up at [Hugging Face](https://huggingface.co/)
2. Get API key
3. Use Hugging Face Inference API

**Cost Note**: OpenAI charges per request. Start with free tier or use open-source alternatives.

---

## ☁️ Azure Deployment

### Deploy Backend (Spring Boot)

1. **Create Azure App Service**:
   ```bash
   az login
   az group create --name myResourceGroup --location eastus
   az appservice plan create --name myAppServicePlan --resource-group myResourceGroup --sku FREE
   az webapp create --resource-group myResourceGroup --plan myAppServicePlan --name my-chat-backend --runtime "JAVA:17-java17"
   ```

2. **Deploy Code**:
   ```bash
   cd backend
   mvn clean package
   az webapp deploy --resource-group myResourceGroup --name my-chat-backend --src-path target/chat-backend-0.0.1-SNAPSHOT.jar
   ```

3. **Set Environment Variables**:
   ```bash
   az webapp config appsettings set --resource-group myResourceGroup --name my-chat-backend --settings AI_API_KEY=your-key-here
   ```

### Deploy Frontend (React)

1. **Create Azure Static Web App**:
   ```bash
   npm install -g @azure/static-web-apps-cli
   cd frontend
   npm run build
   swa deploy ./build
   ```

2. **Or use Azure Portal**:
   - Create Static Web App resource
   - Connect GitHub repository
   - Auto-deploy on push

### Update CORS
After deployment, update backend CORS to allow your Azure frontend URL:
```properties
spring.web.cors.allowed-origins=https://your-frontend-url.azurestaticapps.net
```

---

## 🐛 Troubleshooting

### Backend won't start
- Check Java version: `java -version` (need 17+)
- Check port 8080 is free: `lsof -i :8080`
- Check logs in console

### Frontend won't connect to backend
- Check backend is running on port 8080
- Check CORS configuration in backend
- Check browser console for errors
- Verify API URL in `frontend/src/services/api.js`

### AI API not working
- Verify API key is correct
- Check API key has credits/quota
- Check internet connection
- Review backend logs for error messages

### Module not found errors
- Run `npm install` in frontend directory
- Delete `node_modules` and reinstall if needed
- Check `package.json` dependencies

---

## 📚 Key Concepts Explained

### **What is REST API?**
REST = Representational State Transfer
- Standard way to communicate between frontend and backend
- Uses HTTP methods (GET, POST, PUT, DELETE)
- Returns data in JSON format

### **What is CORS?**
Cross-Origin Resource Sharing
- Browser security feature
- Allows frontend (localhost:3000) to call backend (localhost:8080)
- Must be configured on backend

### **What is Dependency Injection?**
- Spring automatically creates and wires objects
- No need for `new` keyword everywhere
- Makes code testable and maintainable

### **What is useState?**
- React Hook to manage changing data
- When state changes, component re-renders
- Example: `const [count, setCount] = useState(0)`

---

## 🎯 Interview Preparation Tips

1. **Understand the Flow**:
   - User types message → Frontend → Backend → AI API → Backend → Frontend → Display

2. **Know Your Code**:
   - Can explain what each file does
   - Understand the request/response cycle
   - Know how components communicate

3. **Practice Explaining**:
   - "This is how React works..."
   - "Spring Boot handles..."
   - "The API integration works by..."

4. **Common Questions**:
   - What is the difference between frontend and backend?
   - How does React work?
   - What is a REST API?
   - How do you handle errors?
   - What is CORS and why do we need it?

---

## 📖 Additional Resources

- [React Official Docs](https://react.dev/)
- [Ant Design Docs](https://ant.design/)
- [Spring Boot Guide](https://spring.io/guides)
- [Azure Documentation](https://docs.microsoft.com/azure/)
- [REST API Tutorial](https://restfulapi.net/)

---

## ✅ Next Steps

1. ✅ Complete setup
2. ✅ Run both frontend and backend
3. ✅ Test the chat functionality
4. ✅ Add your API key
5. ✅ Experiment with the code
6. ✅ Deploy to Azure
7. ✅ Prepare for interview!

**Good luck! 🚀**

---

## 💬 Support

If you get stuck:
1. Check the error message carefully
2. Review the logs (backend console, browser console)
3. Verify all prerequisites are installed
4. Check the troubleshooting section

Remember: **Every error is a learning opportunity!** 💪

