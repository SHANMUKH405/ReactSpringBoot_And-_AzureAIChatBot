package com.chat.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Message Model - represents a chat message
 * 
 * This is a Plain Old Java Object (POJO) that represents data
 * 
 * @Data from Lombok automatically generates:
 * - Getters and setters
 * - toString(), equals(), hashCode()
 * 
 * JSON Structure:
 * {
 *   "role": "user",
 *   "content": "Hello, how are you?"
 * }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    
    /**
     * Role of the message sender
     * "user" = message from user
     * "assistant" = message from AI
     * "system" = system message (for AI context)
     */
    @JsonProperty("role")
    private String role;
    
    /**
     * The actual message content
     */
    @JsonProperty("content")
    private String content;
}

