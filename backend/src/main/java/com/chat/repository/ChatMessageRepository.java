package com.chat.repository;

import com.chat.model.ChatMessage;
import com.chat.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ChatMessage Repository - Database operations for Messages
 * 
 * Spring Boot automatically provides all database operations!
 * 
 * @Repository - Marks this as a data access component
 */
@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    
    /**
     * Find all messages in a conversation, ordered by timestamp
     * 
     * Spring automatically creates:
     * SELECT * FROM chat_messages WHERE conversation_id = ? ORDER BY created_at ASC
     */
    List<ChatMessage> findByConversationOrderByCreatedAtAsc(Conversation conversation);
    
    /**
     * Count messages in a conversation
     */
    long countByConversation(Conversation conversation);
    
    /**
     * Delete all messages in a conversation
     */
    void deleteByConversation(Conversation conversation);
}

