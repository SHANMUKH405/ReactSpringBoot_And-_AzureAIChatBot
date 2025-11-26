package com.chat.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * User Entity - Represents a user in the system
 * 
 * This is a JPA Entity that maps to a database table
 * 
 * @Entity - Marks this as a database table
 * @Table - Specifies table name
 * 
 * Spring Boot automatically creates this table in the database!
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    /**
     * Primary Key - Auto-generated ID
     * 
     * @Id - Marks as primary key
     * @GeneratedValue - Auto-generates unique IDs
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Username - must be unique
     */
    @Column(unique = true, nullable = false)
    private String username;
    
    /**
     * Email - must be unique
     */
    @Column(unique = true, nullable = false)
    private String email;
    
    /**
     * Password (will be encrypted)
     */
    @Column(nullable = false)
    private String password;
    
    /**
     * When user was created
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * When user was last updated
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    /**
     * Automatically set creation timestamp before saving
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * Automatically update timestamp before saving
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

