package com.chat.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * ChatMessage Entity - Represents a single message in a conversation
 * 
 * This is stored in the database with timestamps
 * 
 * @Entity - Maps to database table
 */
@Entity
@Table(name = "chat_messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    
    /**
     * Primary Key - Auto-generated ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Role of the message sender
     * "user" = message from user
     * "assistant" = message from AI
     * "system" = system message
     */
    @Column(nullable = false)
    private String role;
    
    /**
     * The actual message content
     */
    @Column(nullable = false, length = 5000) // Max 5000 characters
    private String content;
    
    /**
     * Conversation this message belongs to
     * 
     * @ManyToOne - Many messages belong to one conversation
     * @JoinColumn - Foreign key column name
     * 
     * @JsonIgnore - Ignore conversation when serializing ChatMessage to prevent circular reference
     * We already have conversationId in the URL path when loading messages
     */
    @ManyToOne
    @JoinColumn(name = "conversation_id", nullable = false)
    @JsonIgnore
    private Conversation conversation;
    
    /**
     * When message was created
     * 
     * This is the TIMESTAMP feature you requested!
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * Automatically set timestamp when message is created
     * 
     * @PrePersist - Runs before saving to database
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    /**
     * Convert to simple Message format for API responses
     * 
     * This helps maintain backward compatibility with existing API
     */
    public Message toMessage() {
        return new Message(role, content);
    }
    
    /**
     * Get formatted timestamp for display
     */
    @JsonProperty("timestamp")
    public String getFormattedTimestamp() {
        if (createdAt == null) {
            return "";
        }
        return createdAt.toString(); // You can format this as needed
    }
}

