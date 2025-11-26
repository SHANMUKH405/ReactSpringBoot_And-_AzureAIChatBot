import React from 'react';
import { Avatar, Typography, Space } from 'antd';
import { UserOutlined, RobotOutlined } from '@ant-design/icons';

const { Text } = Typography;

/**
 * MessageBubble Component - UPDATED with Timestamps!
 * 
 * Displays a single chat message (user or AI) with timestamp
 * 
 * Props:
 * - message: The message text
 * - isUser: Boolean - true if message is from user, false if from AI
 * - timestamp: Optional timestamp string (ISO format)
 * 
 * This is a reusable component - we use it for each message in the chat
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
    <div
      style={{
        display: 'flex',
        flexDirection: 'column',
        alignItems: isUser ? 'flex-end' : 'flex-start',
        marginBottom: '16px',
        padding: '0 16px',
      }}
    >
      <Space
        direction={isUser ? 'horizontal-reverse' : 'horizontal'}
        align="start"
        size="small"
        style={{ maxWidth: '70%' }}
      >
        {/* Avatar - shows user or robot icon */}
        <Avatar
          icon={isUser ? <UserOutlined /> : <RobotOutlined />}
          style={{
            backgroundColor: isUser ? '#1890ff' : '#52c41a',
          }}
        />
        
        {/* Message bubble */}
        <div
          style={{
            maxWidth: '100%',
            padding: '12px 16px',
            borderRadius: '12px',
            backgroundColor: isUser ? '#1890ff' : '#ffffff',
            boxShadow: '0 2px 8px rgba(0,0,0,0.1)',
            wordWrap: 'break-word',
          }}
        >
          <Text
            style={{
              color: isUser ? '#ffffff' : '#000000',
              fontSize: '14px',
              whiteSpace: 'pre-wrap', // Preserves line breaks
              display: 'block',
            }}
          >
            {message}
          </Text>
          {/* Timestamp */}
          {timestamp && (
            <Text
              type="secondary"
              style={{
                fontSize: '11px',
                color: isUser ? 'rgba(255,255,255,0.7)' : '#8c8c8c',
                display: 'block',
                marginTop: '4px',
              }}
            >
              {formatTimestamp(timestamp)}
            </Text>
          )}
        </div>
      </Space>
    </div>
  );
};

export default MessageBubble;

