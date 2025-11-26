package com.chat.service;

import com.chat.model.User;
import com.chat.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * User Service - Handles user authentication and management
 * 
 * This service manages:
 * - User registration
 * - User login
 * - Password encryption
 * - User validation
 * 
 * @Service - Marks this as a service component
 */
@Slf4j
@Service
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    /**
     * Constructor - Spring automatically injects dependencies
     */
    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    /**
     * Register a new user
     * 
     * @param username Username
     * @param email Email address
     * @param password Plain text password (will be encrypted)
     * @return Created user
     */
    public User registerUser(String username, String email, String password) {
        // Check if username already exists
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists");
        }
        
        // Check if email already exists
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists");
        }
        
        // Create new user
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password)); // Encrypt password
        
        // Save to database
        User savedUser = userRepository.save(user);
        log.info("User registered: {}", username);
        
        return savedUser;
    }
    
    /**
     * Authenticate user (login)
     * 
     * @param username Username
     * @param password Plain text password
     * @return User if credentials are valid, null otherwise
     */
    public User authenticateUser(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // Check if password matches (compares encrypted passwords)
            if (passwordEncoder.matches(password, user.getPassword())) {
                log.info("User authenticated: {}", username);
                return user;
            }
        }
        
        log.warn("Authentication failed for user: {}", username);
        return null;
    }
    
    /**
     * Find user by username
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    /**
     * Find user by ID
     */
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
}

