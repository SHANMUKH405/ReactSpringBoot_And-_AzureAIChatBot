package com.chat.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Chat Response Model - what backend sends back to frontend
 * 
 * After processing the chat request, backend sends this structure
 * 
 * Example JSON:
 * {
 *   "response": "I'm doing well, thank you!",
 *   "conversationId": "abc123",
 *   "status": "success"
 * }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {
    
    /**
     * AI's response message
     */
    @JsonProperty("response")
    private String response;
    
    /**
     * Conversation ID (same as request or newly generated)
     */
    @JsonProperty("conversationId")
    private String conversationId;
    
    /**
     * Status of the request
     * "success" = everything worked
     * "error" = something went wrong
     */
    @JsonProperty("status")
    private String status;
    
    /**
     * Error message (only if status is "error")
     */
    @JsonProperty("error")
    private String error;
}

