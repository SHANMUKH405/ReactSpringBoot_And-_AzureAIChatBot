package com.chat.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Chat Request Model - what frontend sends to backend
 * 
 * When frontend makes a POST request to /api/chat,
 * it sends this structure in the request body
 * 
 * Example JSON:
 * {
 *   "message": "Hello, how are you?",
 *   "conversationId": "abc123"
 * }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequest {
    
    /**
     * The user's message
     */
    @JsonProperty("message")
    private String message;
    
    /**
     * Optional: Conversation ID to maintain chat history
     * If not provided, a new conversation is started
     */
    @JsonProperty("conversationId")
    private String conversationId;
}

