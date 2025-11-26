package com.chat.repository;

import com.chat.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * User Repository - Interface for database operations on Users
 * 
 * JpaRepository provides:
 * - save() - Save user
 * - findById() - Find by ID
 * - findAll() - Find all users
 * - delete() - Delete user
 * - And many more automatically!
 * 
 * Spring Boot automatically implements this interface!
 * You don't write implementation code - Spring does it!
 * 
 * @Repository - Marks this as a data access component
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Find user by username
     * 
     * Spring automatically creates this query method!
     * No SQL needed - Spring generates it!
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Find user by email
     * 
     * Spring automatically creates: SELECT * FROM users WHERE email = ?
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Check if username exists
     * 
     * Spring automatically creates: SELECT COUNT(*) > 0 FROM users WHERE username = ?
     */
    boolean existsByUsername(String username);
    
    /**
     * Check if email exists
     */
    boolean existsByEmail(String email);
}

