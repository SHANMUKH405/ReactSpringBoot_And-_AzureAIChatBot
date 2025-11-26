package com.chat.repository;

import com.chat.model.Conversation;
import com.chat.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Conversation Repository - Database operations for Conversations
 * 
 * Spring Boot automatically implements all basic CRUD operations!
 * 
 * @Repository - Marks this as a data access component
 */
@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    
    /**
     * Find all conversations for a user
     * 
     * Spring automatically creates:
     * SELECT * FROM conversations WHERE user_id = ? ORDER BY created_at DESC
     */
    List<Conversation> findByUserOrderByCreatedAtDesc(User user);
    
    /**
     * Find conversation by ID and user (for security - users can only access their own conversations)
     */
    Optional<Conversation> findByIdAndUser(Long id, User user);
    
    /**
     * Find conversation by user and title
     */
    List<Conversation> findByUserAndTitleContaining(User user, String title);
    
    /**
     * Count conversations for a user
     */
    long countByUser(User user);
}

