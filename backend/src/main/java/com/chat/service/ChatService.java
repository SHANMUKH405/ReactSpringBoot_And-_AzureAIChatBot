package com.chat.service;

import com.chat.model.*;
import com.chat.repository.ChatMessageRepository;
import com.chat.repository.ConversationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Chat Service - Manages chat conversations using DATABASE
 * 
 * This service handles:
 * 1. Storing conversation history in DATABASE (persistent!)
 * 2. Managing multiple conversations per user
 * 3. Coordinating between controller and AI service
 * 4. Message timestamps automatically handled by database
 * 
 * @Service tells Spring this is a service component
 */
@Slf4j
@Service
public class ChatService {
    
    // Repository for database operations
    private final ConversationRepository conversationRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final AIService aiService;
    
    /**
     * Constructor - Spring automatically injects dependencies
     */
    @Autowired
    public ChatService(ConversationRepository conversationRepository,
                      ChatMessageRepository chatMessageRepository,
                      AIService aiService) {
        this.conversationRepository = conversationRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.aiService = aiService;
    }
    
    /**
     * Processes a chat message and returns AI response
     * 
     * @param userMessage The user's message
     * @param conversationId Optional conversation ID
     * @param user The user sending the message
     * @return Response object with AI's reply and conversation ID
     */
    @Transactional
    public ChatResponse processMessage(String userMessage, Long conversationId, User user) {
        try {
            Conversation conversation;
            
            // Get or create conversation
            if (conversationId != null) {
                conversation = conversationRepository.findByIdAndUser(conversationId, user)
                        .orElseThrow(() -> new IllegalArgumentException("Conversation not found"));
            } else {
                // Create new conversation
                conversation = new Conversation();
                conversation.setUser(user);
                conversation.setTitle("New Conversation"); // Can be updated later
                conversation = conversationRepository.save(conversation);
                log.info("Created new conversation {} for user {}", conversation.getId(), user.getUsername());
            }
            
            // Get conversation history for AI context
            List<ChatMessage> existingMessages = chatMessageRepository
                    .findByConversationOrderByCreatedAtAsc(conversation);
            
            // Convert to Message format for AI service
            List<Message> history = existingMessages.stream()
                    .map(ChatMessage::toMessage)
                    .collect(Collectors.toList());
            
            // Create and save user message
            ChatMessage userMsg = new ChatMessage();
            userMsg.setRole("user");
            userMsg.setContent(userMessage);
            userMsg.setConversation(conversation);
            // Timestamp automatically set by @PrePersist
            chatMessageRepository.save(userMsg);
            
            // Get AI response
            String aiResponse = aiService.getAIResponse(userMessage, history);
            
            // Create and save AI message
            ChatMessage aiMsg = new ChatMessage();
            aiMsg.setRole("assistant");
            aiMsg.setContent(aiResponse);
            aiMsg.setConversation(conversation);
            // Timestamp automatically set by @PrePersist
            chatMessageRepository.save(aiMsg);
            
            // Update conversation title if it's still "New Conversation" and has enough messages
            if ("New Conversation".equals(conversation.getTitle()) && 
                chatMessageRepository.countByConversation(conversation) >= 4) {
                // Use first user message as title (first 50 chars)
                ChatMessage firstUserMsg = existingMessages.stream()
                        .filter(m -> "user".equals(m.getRole()))
                        .findFirst()
                        .orElse(userMsg);
                String title = firstUserMsg.getContent();
                if (title.length() > 50) {
                    title = title.substring(0, 50) + "...";
                }
                conversation.setTitle(title);
                conversationRepository.save(conversation);
            }
            
            log.info("Processed message for conversation {}: {}", conversation.getId(), userMessage);
            
            // Return response
            return new ChatResponse(
                    aiResponse,
                    conversation.getId().toString(),
                    "success",
                    null
            );
            
        } catch (Exception e) {
            log.error("Error processing message: {}", e.getMessage(), e);
            return new ChatResponse(
                    null,
                    conversationId != null ? conversationId.toString() : "",
                    "error",
                    "An error occurred: " + e.getMessage()
            );
        }
    }
    
    /**
     * Gets conversation history for a given conversation ID
     * 
     * @param conversationId The conversation ID
     * @param user The user (for security - can only access own conversations)
     * @return List of messages in the conversation with timestamps
     */
    public List<ChatMessage> getConversationHistory(Long conversationId, User user) {
        Conversation conversation = conversationRepository.findByIdAndUser(conversationId, user)
                .orElseThrow(() -> new IllegalArgumentException("Conversation not found"));
        
        return chatMessageRepository.findByConversationOrderByCreatedAtAsc(conversation);
    }
    
    /**
     * Gets all conversations for a user
     * 
     * @param user The user
     * @return List of conversations
     */
    public List<Conversation> getUserConversations(User user) {
        return conversationRepository.findByUserOrderByCreatedAtDesc(user);
    }
    
    /**
     * Creates a new conversation for a user
     * 
     * @param user The user
     * @param title Optional title
     * @return Created conversation
     */
    @Transactional
    public Conversation createConversation(User user, String title) {
        Conversation conversation = new Conversation();
        conversation.setUser(user);
        conversation.setTitle(title != null ? title : "New Conversation");
        return conversationRepository.save(conversation);
    }
    
    /**
     * Deletes a conversation
     * 
     * @param conversationId The conversation ID to delete
     * @param user The user (for security)
     */
    @Transactional
    public void deleteConversation(Long conversationId, User user) {
        Conversation conversation = conversationRepository.findByIdAndUser(conversationId, user)
                .orElseThrow(() -> new IllegalArgumentException("Conversation not found"));
        
        // Delete all messages (cascade will handle this automatically)
        chatMessageRepository.deleteByConversation(conversation);
        
        // Delete conversation
        conversationRepository.delete(conversation);
        log.info("Deleted conversation {} for user {}", conversationId, user.getUsername());
    }
    
    /**
     * Updates conversation title
     * 
     * @param conversationId The conversation ID
     * @param user The user (for security)
     * @param newTitle New title
     * @return Updated conversation
     */
    @Transactional
    public Conversation updateConversationTitle(Long conversationId, User user, String newTitle) {
        Conversation conversation = conversationRepository.findByIdAndUser(conversationId, user)
                .orElseThrow(() -> new IllegalArgumentException("Conversation not found"));
        
        conversation.setTitle(newTitle);
        return conversationRepository.save(conversation);
    }
}
