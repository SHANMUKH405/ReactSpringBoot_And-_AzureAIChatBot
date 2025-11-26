package com.chat.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Global Exception Handler
 * 
 * Handles validation errors and other exceptions globally across all controllers
 * 
 * @RestControllerAdvice - Makes this apply to all controllers
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * Handle validation errors from @Valid annotation
     * 
     * This catches MethodArgumentNotValidException which Spring throws
     * when @Valid validation fails
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> response = new HashMap<>();
        Map<String, String> errors = new HashMap<>();
        
        // Extract validation errors from all fields
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        // Build user-friendly error message
        StringBuilder errorMessage = new StringBuilder();
        boolean first = true;
        for (String msg : errors.values()) {
            if (!first) {
                errorMessage.append(" ");
            }
            errorMessage.append(msg);
            first = false;
        }
        
        // If no specific errors, use generic message
        if (errorMessage.length() == 0) {
            errorMessage.append("Validation failed. Please check your input.");
        }
        
        response.put("status", "error");
        response.put("message", errorMessage.toString());
        response.put("errors", errors);
        
        log.warn("Validation error: {}", errors);
        return ResponseEntity.badRequest().body(response);
    }
}

