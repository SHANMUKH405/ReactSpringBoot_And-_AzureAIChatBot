# 🤖 AI API Setup Guide

## Current Status
✅ **Frontend ↔ Backend**: Connected and working!  
✅ **User messages**: Being received by backend  
⚠️ **AI responses**: Need API key configured

---

## 🎯 Quick Setup (3 Steps)

### Step 1: Get an API Key

**Option A: OpenAI (Recommended for learning)**
1. Go to: https://platform.openai.com/signup
2. Sign up / Log in
3. Go to: https://platform.openai.com/api-keys
4. Click "Create new secret key"
5. Copy the key (starts with `sk-...`)
6. ⚠️ **Save it immediately** - you won't see it again!

**Option B: Free Alternatives**
- **OpenRouter** (supports multiple models): https://openrouter.ai/keys
- **Hugging Face** (open-source models): https://huggingface.co/settings/tokens
- **Together AI**: https://api.together.xyz/settings/api-keys

---

### Step 2: Add API Key to Backend

**Edit the configuration file:**

```bash
# Open the file
cd backend/src/main/resources
nano application.properties
# Or use your IDE/text editor
```

**Find this line:**
```properties
ai.api.key=${AI_API_KEY:your-api-key-here}
```

**Replace it with your actual key:**
```properties
ai.api.key=sk-your-actual-openai-key-here
```

**Save the file.**

---

### Step 3: Restart Backend

**In the backend terminal:**
1. Press `Ctrl+C` to stop
2. Restart:
   ```bash
   cd backend
   export JAVA_HOME="/opt/homebrew/opt/openjdk@17"
   export PATH="/opt/homebrew/opt/openjdk@17/bin:$PATH"
   ./mvnw spring-boot:run
   ```

---

## ✅ Test It!

1. Wait for backend to start: `🚀 Chat Backend is running on http://localhost:8080`
2. Refresh browser (F5)
3. Type a message: "Hello, how are you?"
4. You should get a real AI response! 🎉

---

## 🔒 Security Best Practice

**For Production**: Use environment variables instead of hardcoding keys!

**Option 1: Environment Variable (Recommended)**
```bash
export AI_API_KEY="sk-your-key-here"
```

Then keep in `application.properties`:
```properties
ai.api.key=${AI_API_KEY:your-api-key-here}
```

**Option 2: .env File** (Create `backend/.env`):
```
AI_API_KEY=sk-your-key-here
```

**Never commit API keys to Git!** ✅ Already in `.gitignore`

---

## 💰 Cost Information

**OpenAI Pricing** (as of 2024):
- **gpt-3.5-turbo**: ~$0.0015 per 1K tokens (very cheap!)
- **gpt-4**: More expensive
- **Free tier**: Usually $5 credit to start

**Tips to Save Money:**
- Use `gpt-3.5-turbo` (cheapest)
- Set `max_tokens` limit (already set to 500)
- Monitor usage on OpenAI dashboard

---

## 🆓 Free Alternatives

### OpenRouter (Recommended for free tier)
1. Sign up: https://openrouter.ai/
2. Get API key
3. Update `application.properties`:
   ```properties
   ai.api.url=https://openrouter.ai/api/v1/chat/completions
   ai.api.key=your-openrouter-key
   ai.model.name=openai/gpt-3.5-turbo
   ```

### Hugging Face
1. Sign up: https://huggingface.co/
2. Get token: https://huggingface.co/settings/tokens
3. Update configuration for Hugging Face API

---

## 🧪 Test Without API Key (Development Mode)

If you just want to test the UI without AI:

**Temporary test response** - Edit `AIService.java`:

Find the `getAIResponse` method and add at the beginning:
```java
// Temporary test - remove after getting API key
if (apiKey.equals("your-api-key-here")) {
    return "Hello! This is a test response. Your message was: " + userMessage + 
           "\n\nTo get real AI responses, please add your OpenAI API key to application.properties";
}
```

**Remember**: This is just for testing! Remove it after getting an API key.

---

## 📝 Configuration Reference

**File**: `backend/src/main/resources/application.properties`

```properties
# AI API Configuration
ai.api.key=sk-your-key-here                    # Your OpenAI API key
ai.api.url=https://api.openai.com/v1/chat/completions  # OpenAI endpoint
ai.model.name=gpt-3.5-turbo                    # Model to use
ai.api.timeout=30000                           # Timeout in milliseconds
```

---

## ✅ Checklist

- [ ] Got API key from OpenAI (or alternative)
- [ ] Added key to `application.properties`
- [ ] Restarted backend
- [ ] Tested with a message
- [ ] Got real AI response!

---

## 🐛 Troubleshooting

**"Still getting error message"**
- Check API key is correct (no extra spaces)
- Verify backend restarted after adding key
- Check backend logs for error details

**"Invalid API key"**
- Make sure key starts with `sk-` (OpenAI)
- Check for typos
- Verify key hasn't been revoked

**"Rate limit exceeded"**
- You've used up free tier credits
- Wait a bit or upgrade account
- Try a different API provider

---

**Once you add the API key and restart, you'll have a fully functional AI chat!** 🚀

