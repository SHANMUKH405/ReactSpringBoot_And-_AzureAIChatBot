import React, { useState, useEffect, useRef } from 'react';
import {
  Card,
  Input,
  Button,
  Space,
  Typography,
  Spin,
  message as antMessage,
  Empty,
  Avatar,
} from 'antd';
import { SendOutlined, ClearOutlined, ReloadOutlined, RobotOutlined } from '@ant-design/icons';
import MessageBubble from './MessageBubble';
import { sendChatMessage, checkHealth, getConversationHistory } from '../services/api';
import './ChatBox.css';

const { Title } = Typography;
const { TextArea } = Input;

/**
 * ChatBox Component - Main chat interface with Conversation Support
 * 
 * Features:
 * - Supports multiple conversations
 * - Message timestamps
 * - Conversation history loading
 * - Responsive design for mobile and desktop
 */
const ChatBox = ({ conversationId: propConversationId, onConversationChange, onConversationsUpdate }) => {
  // State variables - data that can change
  const [messages, setMessages] = useState([]); // Array of {role: 'user'|'assistant', content: 'text', timestamp: '...'}
  const [inputValue, setInputValue] = useState(''); // Current input text
  const [loading, setLoading] = useState(false); // Is AI processing?
  const [conversationId, setConversationId] = useState(propConversationId); // Current conversation ID
  const [backendOnline, setBackendOnline] = useState(false); // Is backend running?
  const [loadingHistory, setLoadingHistory] = useState(false); // Loading conversation history
  
  // Ref to scroll chat to bottom
  const messagesEndRef = useRef(null);

  // Update conversationId when prop changes
  useEffect(() => {
    setConversationId(propConversationId);
    if (propConversationId) {
      loadConversationHistory(propConversationId);
    } else {
      setMessages([]);
    }
  }, [propConversationId]);

  /**
   * useEffect Hook - Runs when component first loads
   */
  useEffect(() => {
    checkBackendHealth();
  }, []);

  /**
   * useEffect Hook - Runs when messages change
   * Auto-scrolls chat to bottom when new message arrives
   */
  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  /**
   * Load conversation history from database
   */
  const loadConversationHistory = async (convId) => {
    if (!convId) return;
    
    setLoadingHistory(true);
    try {
      const history = await getConversationHistory(convId);
      // Convert to message format with timestamps
      const formattedMessages = history.map(msg => ({
        role: msg.role,
        content: msg.content,
        timestamp: msg.createdAt || msg.timestamp,
      }));
      setMessages(formattedMessages);
    } catch (error) {
      console.error('Error loading conversation history:', error);
      antMessage.error('Failed to load conversation history');
    } finally {
      setLoadingHistory(false);
    }
  };

  /**
   * Checks if backend is running
   */
  const checkBackendHealth = async () => {
    try {
      await checkHealth();
      setBackendOnline(true);
    } catch (error) {
      setBackendOnline(false);
      antMessage.error('Backend is not running. Please start the Spring Boot server.');
    }
  };

  /**
   * Scrolls chat area to bottom
   */
  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  /**
   * Handles sending a message
   */
  const handleSend = async () => {
    // Validate input
    if (!inputValue.trim()) {
      antMessage.warning('Please enter a message');
      return;
    }

    if (!backendOnline) {
      antMessage.error('Backend is not available. Please start the server.');
      return;
    }

    // Get user message
    const userMessage = inputValue.trim();
    
    // Clear input
    setInputValue('');
    
    // Add user message to chat immediately (with current timestamp)
    const newUserMessage = {
      role: 'user',
      content: userMessage,
      timestamp: new Date().toISOString(),
    };
    setMessages((prev) => [...prev, newUserMessage]);
    
    // Show loading state
    setLoading(true);

    try {
      // Send message to backend
      const response = await sendChatMessage(userMessage, conversationId);
      
      // Update conversation ID if it's a new conversation
      const newConvId = response.conversationId;
      if (newConvId && newConvId !== conversationId) {
        setConversationId(newConvId);
        if (onConversationChange) {
          onConversationChange(newConvId);
        }
        // Reload conversations list
        if (onConversationsUpdate) {
          onConversationsUpdate();
        }
      }
      
      // Check if request was successful
      if (response.status === 'success' && response.response) {
        // Add AI response to chat (timestamp will be from backend)
        const aiMessage = {
          role: 'assistant',
          content: response.response,
          timestamp: new Date().toISOString(), // Will be updated when we reload history
        };
        setMessages((prev) => [...prev, aiMessage]);
        
        // Reload conversation to get actual timestamps from database
        if (newConvId) {
          setTimeout(() => loadConversationHistory(newConvId), 500);
        }
      } else {
        // Handle error response
        antMessage.error(response.error || 'Failed to get response from AI');
        const errorMessage = {
          role: 'assistant',
          content: 'Sorry, I encountered an error. Please try again.',
          timestamp: new Date().toISOString(),
        };
        setMessages((prev) => [...prev, errorMessage]);
      }
    } catch (error) {
      // Handle network/other errors
      console.error('Error sending message:', error);
      antMessage.error(error.message || 'Failed to send message');
      
      const errorMessage = {
        role: 'assistant',
        content: 'Sorry, I couldn\'t process your message. Please check your connection and try again.',
        timestamp: new Date().toISOString(),
      };
      setMessages((prev) => [...prev, errorMessage]);
    } finally {
      // Always hide loading state
      setLoading(false);
    }
  };

  /**
   * Handles Enter key press
   */
  const handleKeyPress = (e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleSend();
    }
  };

  /**
   * Clears chat conversation
   */
  const handleClear = () => {
    setMessages([]);
    setConversationId(null);
    if (onConversationChange) {
      onConversationChange(null);
    }
    antMessage.success('Chat cleared');
  };

  return (
    <Card
      className="chat-box-card"
      style={{
        width: '100%',
        height: '100%',
        display: 'flex',
        flexDirection: 'column',
        boxShadow: 'none',
        borderRadius: 0,
        border: 'none',
      }}
      bodyStyle={{
        display: 'flex',
        flexDirection: 'column',
        height: '100%',
        padding: 0,
      }}
    >
      {/* Header */}
      <div className="chat-header">
        <Space style={{ width: '100%', justifyContent: 'space-between', flexWrap: 'wrap' }}>
          <Title level={4} className="chat-title">
            🤖 AI Chat Assistant
          </Title>
          <Space className="chat-header-actions" size="small">
            <Button
              icon={<ReloadOutlined />}
              onClick={checkBackendHealth}
              size="small"
              className="header-button"
            >
              <span className="status-text">{backendOnline ? 'Online' : 'Offline'}</span>
            </Button>
            {conversationId && (
              <Button
                icon={<ClearOutlined />}
                onClick={handleClear}
                size="small"
                danger
                className="header-button"
              >
                <span className="clear-text">Clear</span>
              </Button>
            )}
          </Space>
        </Space>
      </div>

      {/* Messages Area */}
      <div className="chat-messages">
        {loadingHistory ? (
          <div className="loading-container">
            <Spin size="large" />
            <p className="loading-text">Loading conversation...</p>
          </div>
        ) : messages.length === 0 ? (
          <Empty
            description={
              <span className="empty-description">
                {conversationId ? 'No messages in this conversation yet' : 'Start a conversation with the AI assistant!'}
              </span>
            }
            className="empty-state"
          />
        ) : (
          <div className="messages-container">
            {messages.map((msg, index) => (
              <MessageBubble
                key={index}
                message={msg.content}
                isUser={msg.role === 'user'}
                timestamp={msg.timestamp}
              />
            ))}
          </div>
        )}
        
        {/* Loading indicator when AI is responding */}
        {loading && (
          <div className="ai-thinking">
            <Space>
              <Avatar icon={<RobotOutlined />} style={{ backgroundColor: '#52c41a' }} />
              <Spin size="small" />
              <span className="thinking-text">AI is thinking...</span>
            </Space>
          </div>
        )}
        
        {/* Invisible element to scroll to */}
        <div ref={messagesEndRef} />
      </div>

      {/* Input Area */}
      <div className="chat-input-area">
        <Space.Compact style={{ width: '100%' }} className="input-container">
          <TextArea
            value={inputValue}
            onChange={(e) => setInputValue(e.target.value)}
            onKeyPress={handleKeyPress}
            placeholder="Type your message here... (Press Enter to send, Shift+Enter for new line)"
            autoSize={{ minRows: 1, maxRows: 4 }}
            disabled={loading || !backendOnline || !conversationId}
            className="message-input"
          />
          <Button
            type="primary"
            icon={<SendOutlined />}
            onClick={handleSend}
            loading={loading}
            disabled={!inputValue.trim() || !backendOnline || !conversationId}
            className="send-button"
          >
            <span className="send-text">Send</span>
          </Button>
        </Space.Compact>
        
        {/* Status indicator */}
        <div className="status-indicator">
          {backendOnline ? (
            <span className="status-online">● Connected</span>
          ) : (
            <span className="status-offline">● Backend offline</span>
          )}
        </div>
      </div>
    </Card>
  );
};

export default ChatBox;
