package com.chat.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Conversation Entity - Represents a chat conversation
 * 
 * A conversation belongs to a user and contains multiple messages
 * 
 * @Entity - Maps to database table
 * 
 * Spring Boot creates this table automatically!
 */
@Entity
@Table(name = "conversations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Conversation {
    
    /**
     * Primary Key - Auto-generated ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Title of the conversation (e.g., "Chat about Spring Boot")
     */
    private String title;
    
    /**
     * User who owns this conversation
     * 
     * @ManyToOne - Many conversations belong to one user
     * @JoinColumn - Foreign key column name
     */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"password", "createdAt", "updatedAt"}) // Don't serialize password
    private User user;
    
    /**
     * Messages in this conversation
     * 
     * @OneToMany - One conversation has many messages
     * @CascadeType.ALL - Delete messages when conversation is deleted
     * 
     * @JsonIgnore - Ignore messages when serializing Conversation to prevent circular reference
     * Messages are loaded separately via /api/history/{conversationId}
     */
    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<ChatMessage> messages = new ArrayList<>();
    
    /**
     * When conversation was created
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * When conversation was last updated
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    /**
     * Automatically set creation timestamp
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (title == null || title.isEmpty()) {
            title = "New Conversation";
        }
    }
    
    /**
     * Automatically update timestamp
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

