package com.chat.controller;

import com.chat.model.*;
import com.chat.service.ChatService;
import com.chat.service.ExternalAPIService;
import com.chat.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Chat Controller - REST API Endpoints (UPDATED with Database Support)
 * 
 * This class handles HTTP requests from the frontend
 * 
 * New Features:
 * - Database persistence (messages saved permanently)
 * - Multiple conversations per user
 * - Message timestamps
 * - User authentication support
 * 
 * @RestController annotation means:
 * - This class handles REST API requests
 * - Methods return data (not HTML pages)
 * - Spring automatically converts return values to JSON
 * 
 * @RequestMapping("/api") means all endpoints start with /api
 */
@Slf4j
@RestController
@RequestMapping("/api")
public class ChatController {
    
    // Dependency Injection - Spring automatically provides these services
    private final ChatService chatService;
    private final ExternalAPIService externalAPIService;
    private final UserService userService;
    
    /**
     * Constructor - Spring automatically injects services
     */
    @Autowired
    public ChatController(ChatService chatService, ExternalAPIService externalAPIService, UserService userService) {
        this.chatService = chatService;
        this.externalAPIService = externalAPIService;
        this.userService = userService;
    }
    
    /**
     * Health Check Endpoint
     * GET /api/health
     * 
     * Used to check if backend is running
     * Returns: {"status": "UP"}
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("message", "Backend is running!");
        log.info("Health check requested");
        return ResponseEntity.ok(response);
    }
    
    /**
     * Chat Endpoint (Updated with Database Support)
     * POST /api/chat
     * 
     * Receives user message, processes it, returns AI response
     * Messages are now saved to DATABASE with timestamps!
     * 
     * Request Body:
     * {
     *   "message": "Hello, how are you?",
     *   "conversationId": "optional-id",  // Now uses Long ID instead of String
     *   "userId": "optional-user-id"      // Optional for now
     * }
     * 
     * Response:
     * {
     *   "response": "I'm doing well!",
     *   "conversationId": "123",
     *   "status": "success"
     * }
     */
    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        try {
            log.info("Received chat request: {}", request.getMessage());
            
            // Validate request
            if (request.getMessage() == null || request.getMessage().trim().isEmpty()) {
                ChatResponse errorResponse = new ChatResponse(
                        null,
                        "",
                        "error",
                        "Message cannot be empty"
                );
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            // For now, create/get a default "guest" user for backward compatibility
            // In production, you'd get user from authentication token
            User user = getOrCreateGuestUser();
            
            // Parse conversation ID (can be String or Long)
            Long conversationId = null;
            if (request.getConversationId() != null && !request.getConversationId().isEmpty()) {
                try {
                    conversationId = Long.parseLong(request.getConversationId());
                } catch (NumberFormatException e) {
                    log.warn("Invalid conversation ID format: {}", request.getConversationId());
                }
            }
            
            // Process message through service (now saves to database!)
            ChatResponse response = chatService.processMessage(
                    request.getMessage(),
                    conversationId,
                    user
            );
            
            // Return response
            if ("success".equals(response.getStatus())) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
            
        } catch (Exception e) {
            log.error("Error in chat endpoint: {}", e.getMessage(), e);
            ChatResponse errorResponse = new ChatResponse(
                    null,
                    "",
                    "error",
                    "Internal server error: " + e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * Get Conversation History (Updated - Now with Timestamps!)
     * GET /api/history/{conversationId}
     * 
     * Returns all messages in a conversation with timestamps
     */
    @GetMapping("/history/{conversationId}")
    public ResponseEntity<List<ChatMessage>> getHistory(@PathVariable Long conversationId) {
        try {
            log.info("Getting history for conversation: {}", conversationId);
            User user = getOrCreateGuestUser();
            List<ChatMessage> history = chatService.getConversationHistory(conversationId, user);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            log.error("Error getting history: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get All Conversations for User
     * GET /api/conversations
     * 
     * Returns list of all conversations with titles and timestamps
     */
    @GetMapping("/conversations")
    public ResponseEntity<List<Conversation>> getConversations() {
        try {
            User user = getOrCreateGuestUser();
            List<Conversation> conversations = chatService.getUserConversations(user);
            return ResponseEntity.ok(conversations);
        } catch (Exception e) {
            log.error("Error getting conversations: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Create New Conversation
     * POST /api/conversations
     * 
     * Creates a new conversation for the user
     */
    @PostMapping("/conversations")
    public ResponseEntity<Conversation> createConversation(@RequestBody Map<String, String> request) {
        try {
            User user = getOrCreateGuestUser();
            String title = request.get("title");
            Conversation conversation = chatService.createConversation(user, title);
            return ResponseEntity.status(HttpStatus.CREATED).body(conversation);
        } catch (Exception e) {
            log.error("Error creating conversation: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Delete Conversation
     * DELETE /api/conversations/{conversationId}
     * 
     * Deletes a conversation and all its messages
     */
    @DeleteMapping("/conversations/{conversationId}")
    public ResponseEntity<Map<String, String>> deleteConversation(@PathVariable Long conversationId) {
        try {
            log.info("Deleting conversation: {}", conversationId);
            User user = getOrCreateGuestUser();
            chatService.deleteConversation(conversationId, user);
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Conversation deleted");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error deleting conversation: {}", e.getMessage());
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Weather API Example
     * GET /api/weather?city=London
     * 
     * Example of external API integration
     */
    @GetMapping("/weather")
    public ResponseEntity<Map<String, Object>> getWeather(@RequestParam String city) {
        try {
            log.info("Getting weather for: {}", city);
            Map<String, Object> weather = externalAPIService.getWeather(city);
            return ResponseEntity.ok(weather);
        } catch (Exception e) {
            log.error("Error getting weather: {}", e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Could not fetch weather");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * Helper method - Gets or creates a guest user
     * 
     * For learning purposes, we create a default user
     * In production, you'd get user from authentication token
     */
    private User getOrCreateGuestUser() {
        // Try to find guest user
        return userService.findByUsername("guest")
                .orElseGet(() -> {
                    // Create guest user if doesn't exist
                    try {
                        return userService.registerUser("guest", "guest@example.com", "guest123");
                    } catch (Exception e) {
                        // If exists, just return it
                        return userService.findByUsername("guest").orElse(null);
                    }
                });
    }
}
