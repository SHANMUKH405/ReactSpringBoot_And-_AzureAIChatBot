package com.chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot Application Class
 * 
 * @SpringBootApplication annotation does three things:
 * 1. @Configuration - Marks this as a configuration class
 * 2. @EnableAutoConfiguration - Enables Spring Boot auto-configuration
 * 3. @ComponentScan - Scans for components in this package and sub-packages
 * 
 * When you run this class, Spring Boot starts an embedded server
 * and your application is ready to receive HTTP requests!
 */
@SpringBootApplication
public class ChatApplication {

    public static void main(String[] args) {
        // This starts the Spring Boot application
        SpringApplication.run(ChatApplication.class, args);
        System.out.println("🚀 Chat Backend is running on http://localhost:8080");
    }
}

