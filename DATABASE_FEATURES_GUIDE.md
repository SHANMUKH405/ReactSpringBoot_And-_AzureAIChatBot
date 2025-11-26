# 🎯 Quick Start: Database Features Guide

## 🚀 What's New

All features have been added! Your chat app now has:
1. ✅ **Database storage** - Messages saved permanently
2. ✅ **User authentication** - Register and login
3. ✅ **Multiple conversations** - Create and manage conversations
4. ✅ **Message timestamps** - Every message has a timestamp

---

## 📋 API Endpoints

### **Authentication**

#### 1. Register User
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "john",
  "email": "john@example.com",
  "password": "password123"
}
```

**Response:**
```json
{
  "status": "success",
  "message": "User registered successfully",
  "userId": 1,
  "username": "john"
}
```

#### 2. Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "john",
  "password": "password123"
}
```

**Response:**
```json
{
  "status": "success",
  "message": "Login successful",
  "userId": 1,
  "username": "john",
  "email": "john@example.com"
}
```

---

### **Chat (Updated - Now with Database!)**

#### 3. Send Message (Same endpoint, now saves to database!)
```http
POST /api/chat
Content-Type: application/json

{
  "message": "Hello!",
  "conversationId": "123"  // Optional - creates new if not provided
}
```

**Response:**
```json
{
  "response": "Hello! How can I help you?",
  "conversationId": "123",
  "status": "success",
  "error": null
}
```

---

### **Conversations**

#### 4. Get All Conversations
```http
GET /api/conversations
```

**Response:**
```json
[
  {
    "id": 1,
    "title": "New Conversation",
    "createdAt": "2025-11-24T10:30:00",
    "updatedAt": "2025-11-24T10:35:00"
  }
]
```

#### 5. Create New Conversation
```http
POST /api/conversations
Content-Type: application/json

{
  "title": "Discussion about Spring Boot"
}
```

#### 6. Get Conversation Messages (with timestamps!)
```http
GET /api/history/{conversationId}
```

**Response:**
```json
[
  {
    "id": 1,
    "role": "user",
    "content": "Hello!",
    "timestamp": "2025-11-24T10:30:00",
    "createdAt": "2025-11-24T10:30:00"
  },
  {
    "id": 2,
    "role": "assistant",
    "content": "Hello! How can I help?",
    "timestamp": "2025-11-24T10:30:05",
    "createdAt": "2025-11-24T10:30:05"
  }
]
```

#### 7. Delete Conversation
```http
DELETE /api/conversations/{conversationId}
```

---

## 🗄️ View Database

### **H2 Console**

Spring Boot provides a web interface to view your database!

1. **Start your backend**
2. **Open**: `http://localhost:8080/h2-console`
3. **Login**:
   - JDBC URL: `jdbc:h2:mem:chatdb`
   - Username: `sa`
   - Password: (leave empty)
4. **View Tables**:
   - `USERS` - All registered users
   - `CONVERSATIONS` - All conversations
   - `CHAT_MESSAGES` - All messages with timestamps!

---

## 🎓 Key Spring Boot Concepts Used

### **1. JPA Entities (`@Entity`)**
```java
@Entity
public class ChatMessage {
    @Id
    @GeneratedValue
    private Long id;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
```
**What it does:** Spring Boot automatically creates the database table!

### **2. Repositories (`JpaRepository`)**
```java
@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByConversationOrderByCreatedAtAsc(Conversation conv);
}
```
**What it does:** Spring Boot automatically implements this interface and generates SQL!

### **3. Automatic Timestamps (`@PrePersist`)**
```java
@PrePersist
protected void onCreate() {
    createdAt = LocalDateTime.now();
}
```
**What it does:** Spring Boot automatically sets timestamp before saving!

### **4. Password Encryption (`BCryptPasswordEncoder`)**
```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```
**What it does:** Spring Boot encrypts passwords automatically!

---

## 🔄 Restart Backend

After adding all these features, restart your backend:

```bash
cd backend
./mvnw spring-boot:run
```

**What happens:**
1. Spring Boot starts
2. **Automatically creates database tables** (first time)
3. **Sets up H2 database**
4. **Starts on port 8080**

**Check logs** - you'll see:
```
Hibernate: create table users ...
Hibernate: create table conversations ...
Hibernate: create table chat_messages ...
```

Spring Boot created all tables automatically! 🎉

---

## ✅ Testing

### **1. Test Registration**
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","email":"test@example.com","password":"test123"}'
```

### **2. Test Login**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"test123"}'
```

### **3. Test Chat (saves to database!)**
```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"message":"Hello!"}'
```

### **4. View in Database**
1. Open `http://localhost:8080/h2-console`
2. Run: `SELECT * FROM chat_messages;`
3. See your message with timestamp! ⏰

---

## 📚 Files Changed/Created

### **New Entities**
- `User.java` - User table
- `Conversation.java` - Conversation table  
- `ChatMessage.java` - Message table (with timestamps!)

### **New Repositories**
- `UserRepository.java` - User database operations
- `ConversationRepository.java` - Conversation operations
- `ChatMessageRepository.java` - Message operations

### **New Services**
- `UserService.java` - User authentication

### **Updated Services**
- `ChatService.java` - Now uses database!

### **New Controllers**
- `AuthController.java` - Registration and login

### **Updated Controllers**
- `ChatController.java` - Database support added

### **New Configuration**
- `SecurityConfig.java` - Password encryption
- `application.properties` - Database config

---

## 🎯 Next Steps

1. ✅ Backend is ready
2. ⏭️ Update frontend to use new endpoints
3. ⏭️ Add login/register UI
4. ⏭️ Display timestamps in chat
5. ⏭️ Add conversation list UI

---

**All features implemented using Spring Boot's powerful features!** 🚀

