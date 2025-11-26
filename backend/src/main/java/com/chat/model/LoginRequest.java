package com.chat.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Login Request Model
 * 
 * Used when a user wants to login
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    
    @NotBlank(message = "Username is required")
    @JsonProperty("username")
    private String username;
    
    @NotBlank(message = "Password is required")
    @JsonProperty("password")
    private String password;
}

