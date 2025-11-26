import React from 'react';
import { Avatar, Typography, Space } from 'antd';
import { UserOutlined, RobotOutlined } from '@ant-design/icons';
import './MessageBubble.css';

const { Text } = Typography;

/**
 * MessageBubble Component - UPDATED with Timestamps and Responsive Design!
 * 
 * Displays a single chat message (user or AI) with timestamp
 * Fully responsive for mobile and desktop
 */
const MessageBubble = ({ message, isUser, timestamp }) => {
  // Format timestamp for display
  const formatTimestamp = (ts) => {
    if (!ts) return '';
    try {
      const date = new Date(ts);
      const now = new Date();
      const diff = now - date;
      const minutes = Math.floor(diff / (1000 * 60));
      const hours = Math.floor(diff / (1000 * 60 * 60));

      if (minutes < 1) return 'Just now';
      if (minutes < 60) return `${minutes}m ago`;
      if (hours < 24) return `${hours}h ago`;
      return date.toLocaleDateString() + ' ' + date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    } catch (e) {
      return '';
    }
  };

  return (
    <div className={`message-wrapper ${isUser ? 'user-message' : 'ai-message'}`}>
      <Space
        direction={isUser ? 'horizontal-reverse' : 'horizontal'}
        align="start"
        size="small"
        className="message-space"
      >
        {/* Avatar - shows user or robot icon */}
        <Avatar
          icon={isUser ? <UserOutlined /> : <RobotOutlined />}
          className={`message-avatar ${isUser ? 'user-avatar' : 'ai-avatar'}`}
        />
        
        {/* Message bubble */}
        <div className={`message-bubble ${isUser ? 'user-bubble' : 'ai-bubble'}`}>
          <Text className={`message-text ${isUser ? 'user-text' : 'ai-text'}`}>
            {message}
          </Text>
          {/* Timestamp */}
          {timestamp && (
            <Text className={`message-timestamp ${isUser ? 'user-timestamp' : 'ai-timestamp'}`}>
              {formatTimestamp(timestamp)}
            </Text>
          )}
        </div>
      </Space>
    </div>
  );
};

export default MessageBubble;
