# ⚡ Quick Start Guide

Get your AI Chat Assistant running in 5 minutes!

---

## 🎯 Prerequisites Check

Run these commands to verify you have everything installed:

```bash
# Check Java (need 17+)
java -version

# Check Node.js (need 18+)
node -v
npm -v

# Check Maven (optional - project includes wrapper)
mvn -v || echo "Maven wrapper will be used"
```

**Don't have these?** See [README.md](README.md) for installation links.

---

## 🚀 Quick Setup (5 Steps)

### Step 1: Navigate to Project
```bash
cd /Users/shannu405/Internship_Learning_Project
```

### Step 2: Start Backend (Terminal 1)

```bash
cd backend
./mvnw spring-boot:run
# On Windows: mvnw.cmd spring-boot:run
```

Wait for: `🚀 Chat Backend is running on http://localhost:8080`

**Note**: First run will download dependencies (may take 2-3 minutes)

### Step 3: Start Frontend (Terminal 2 - NEW TERMINAL)

Open a **new terminal window** (keep backend running):

```bash
cd /Users/shannu405/Internship_Learning_Project/frontend
npm install    # First time only (downloads dependencies)
npm start      # Starts development server
```

Wait for browser to open at `http://localhost:3000`

### Step 4: Configure AI API (Optional - for AI responses)

Edit `backend/src/main/resources/application.properties`:

```properties
ai.api.key=your-openai-api-key-here
```

**Get API Key:**
- OpenAI: [platform.openai.com](https://platform.openai.com/api-keys)
- Or use a free alternative (see README.md)

**Note**: Without API key, you'll get error messages, but you can still test the UI!

### Step 5: Test It!

1. Open `http://localhost:3000` in browser
2. Type a message: "Hello, how are you?"
3. Click Send
4. See AI response!

---

## 🐛 Troubleshooting

### Backend won't start?

**Port 8080 already in use?**
```bash
# Find what's using port 8080
lsof -i :8080

# Kill it or change port in application.properties
# server.port=8081
```

**Java not found?**
```bash
# Install Java 17+ from:
# https://www.oracle.com/java/technologies/downloads/
```

### Frontend won't start?

**Port 3000 already in use?**
- React will ask to use a different port - say yes!

**Module not found errors?**
```bash
cd frontend
rm -rf node_modules package-lock.json
npm install
```

**Can't connect to backend?**
- Make sure backend is running on port 8080
- Check backend console for errors
- Verify `http://localhost:8080/api/health` works in browser

---

## 📝 What Each Part Does

### Backend (`backend/`)
- Receives messages from frontend
- Calls AI API
- Returns responses
- Runs on port 8080

### Frontend (`frontend/`)
- Beautiful chat UI
- Sends messages to backend
- Displays responses
- Runs on port 3000

### How They Talk
```
Frontend (3000) → HTTP Request → Backend (8080) → AI API → Backend → Frontend
```

---

## 🎓 Next Steps

1. ✅ **Get it running** (you just did!)
2. ✅ **Read the code** - Check out comments explaining each part
3. ✅ **Modify something** - Change colors, add features
4. ✅ **Read ROADMAP.md** - Understand the concepts
5. ✅ **Prepare for interview** - Practice explaining the stack

---

## 💡 Quick Test Without AI API

Want to test without setting up AI API? 

Edit `backend/src/main/java/com/chat/service/AIService.java`:

Find the `getAIResponse` method and add at the beginning:

```java
// Temporary test response (remove after testing)
return "Hello! This is a test response. Your message was: " + userMessage;
```

This bypasses the AI API for quick testing!

---

## 🎯 Common Commands

```bash
# Backend
cd backend
./mvnw spring-boot:run          # Start backend
./mvnw clean package            # Build JAR file

# Frontend
cd frontend
npm start                       # Start dev server
npm run build                   # Build for production
npm install                     # Install dependencies
```

---

## ✅ Success Checklist

- [ ] Backend running on http://localhost:8080
- [ ] Frontend running on http://localhost:3000
- [ ] Browser shows chat interface
- [ ] Can send messages
- [ ] Backend responds (even if error, that's ok!)

**You're all set! 🎉**

Now read the [ROADMAP.md](ROADMAP.md) to understand what you just built!

