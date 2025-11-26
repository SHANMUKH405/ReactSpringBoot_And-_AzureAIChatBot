import axios from 'axios';

/**
 * API Service - Handles all communication with backend
 * 
 * This file contains functions that make HTTP requests to the backend
 * 
 * axios is a popular library for making HTTP requests in JavaScript
 * It's like fetch() but easier to use
 */

// Base URL of backend API
// Change this when deploying to production
const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

// Create axios instance with default configuration
const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 30000, // 30 seconds timeout
});

/**
 * Health Check - Tests if backend is running
 * GET /api/health
 */
export const checkHealth = async () => {
  try {
    const response = await apiClient.get('/health');
    return response.data;
  } catch (error) {
    console.error('Health check failed:', error);
    throw error;
  }
};

/**
 * Send Chat Message - Sends user message to backend, gets AI response
 * POST /api/chat
 * 
 * @param {string} message - User's message
 * @param {string} conversationId - Optional conversation ID
 * @returns {Promise} Response with AI's reply
 */
export const sendChatMessage = async (message, conversationId = null) => {
  try {
    const response = await apiClient.post('/chat', {
      message: message,
      conversationId: conversationId,
    });
    return response.data;
  } catch (error) {
    console.error('Error sending chat message:', error);
    
    // Handle different error types
    if (error.response) {
      // Server responded with error status
      throw new Error(error.response.data?.error || 'Server error occurred');
    } else if (error.request) {
      // Request made but no response received
      throw new Error('Unable to connect to server. Is the backend running?');
    } else {
      // Something else happened
      throw new Error('An error occurred: ' + error.message);
    }
  }
};

/**
 * Get Conversation History - Retrieves all messages in a conversation with timestamps
 * GET /api/history/{conversationId}
 * 
 * @param {string|number} conversationId - Conversation ID
 * @returns {Promise} List of messages with timestamps
 */
export const getConversationHistory = async (conversationId) => {
  try {
    const response = await apiClient.get(`/history/${conversationId}`);
    return response.data;
  } catch (error) {
    console.error('Error getting conversation history:', error);
    throw error;
  }
};

/**
 * Delete Conversation - Deletes a conversation
 * DELETE /api/history/{conversationId}
 * 
 * @param {string} conversationId - Conversation ID to delete
 */
export const deleteConversation = async (conversationId) => {
  try {
    const response = await apiClient.delete(`/history/${conversationId}`);
    return response.data;
  } catch (error) {
    console.error('Error deleting conversation:', error);
    throw error;
  }
};

/**
 * Get Weather - Example external API call
 * GET /api/weather?city={city}
 * 
 * @param {string} city - City name
 * @returns {Promise} Weather information
 */
export const getWeather = async (city) => {
  try {
    const response = await apiClient.get(`/weather?city=${encodeURIComponent(city)}`);
    return response.data;
  } catch (error) {
    console.error('Error getting weather:', error);
    throw error;
  }
};

/**
 * Register User - Creates a new user account
 * POST /api/auth/register
 */
export const registerUser = async (username, email, password) => {
  try {
    const response = await apiClient.post('/auth/register', {
      username,
      email,
      password,
    });
    return response.data;
  } catch (error) {
    console.error('Registration error:', error);
    throw error;
  }
};

/**
 * Login User - Authenticates user
 * POST /api/auth/login
 */
export const loginUser = async (username, password) => {
  try {
    const response = await apiClient.post('/auth/login', {
      username,
      password,
    });
    return response.data;
  } catch (error) {
    console.error('Login error:', error);
    throw error;
  }
};

/**
 * Get All Conversations - Gets all conversations for current user
 * GET /api/conversations
 */
export const getConversations = async () => {
  try {
    const response = await apiClient.get('/conversations');
    return response.data;
  } catch (error) {
    console.error('Error getting conversations:', error);
    throw error;
  }
};

/**
 * Create New Conversation - Creates a new conversation
 * POST /api/conversations
 */
export const createConversation = async (title = null) => {
  try {
    const response = await apiClient.post('/conversations', { title });
    return response.data;
  } catch (error) {
    console.error('Error creating conversation:', error);
    throw error;
  }
};

/**
 * Delete Conversation - Deletes a conversation
 * DELETE /api/conversations/{conversationId}
 */
export const deleteConversationById = async (conversationId) => {
  try {
    const response = await apiClient.delete(`/conversations/${conversationId}`);
    return response.data;
  } catch (error) {
    console.error('Error deleting conversation:', error);
    throw error;
  }
};

// Export the API base URL for use in other files
export { API_BASE_URL };

