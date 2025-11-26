import React from 'react';
import { List, Button, Typography, Empty } from 'antd';
import { MessageOutlined, DeleteOutlined, PlusOutlined } from '@ant-design/icons';
import './ConversationList.css';

const { Text } = Typography;

/**
 * ConversationList Component - Displays list of conversations
 * Responsive design for desktop and mobile
 */
const ConversationList = ({ conversations, activeConversationId, onSelectConversation, onDeleteConversation, onCreateConversation }) => {
  const formatDate = (dateString) => {
    if (!dateString) return '';
    const date = new Date(dateString);
    const now = new Date();
    const diff = now - date;
    const hours = Math.floor(diff / (1000 * 60 * 60));
    const days = Math.floor(hours / 24);

    if (hours < 1) return 'Just now';
    if (hours < 24) return `${hours}h ago`;
    if (days < 7) return `${days}d ago`;
    return date.toLocaleDateString();
  };

  return (
    <div className="conversation-list-container">
      <div className="conversation-list-header">
        <Text strong className="conversation-list-title">Conversations</Text>
        <Button
          type="primary"
          icon={<PlusOutlined />}
          size="small"
          onClick={onCreateConversation}
          className="new-conversation-button"
        >
          <span className="new-button-text">New</span>
        </Button>
      </div>

      <div className="conversation-list-content">
        {conversations.length === 0 ? (
          <Empty
            description="No conversations yet"
            className="conversation-empty"
            image={Empty.PRESENTED_IMAGE_SIMPLE}
          />
        ) : (
          <List
            dataSource={conversations}
            className="conversation-list"
            renderItem={(conversation) => {
              const isActive = activeConversationId === conversation.id?.toString();
              return (
                <List.Item
                  className={`conversation-item ${isActive ? 'active' : ''}`}
                  onClick={() => onSelectConversation(conversation.id)}
                  actions={[
                    <Button
                      type="text"
                      danger
                      icon={<DeleteOutlined />}
                      size="small"
                      className="delete-button"
                      onClick={(e) => {
                        e.stopPropagation();
                        onDeleteConversation(conversation.id);
                      }}
                    />,
                  ]}
                >
                  <List.Item.Meta
                    avatar={<MessageOutlined className="conversation-icon" />}
                    title={
                      <Text
                        ellipsis
                        className={`conversation-title ${isActive ? 'active' : ''}`}
                      >
                        {conversation.title || 'New Conversation'}
                      </Text>
                    }
                    description={
                      <Text type="secondary" className="conversation-date">
                        {formatDate(conversation.createdAt)}
                      </Text>
                    }
                  />
                </List.Item>
              );
            }}
          />
        )}
      </div>
    </div>
  );
};

export default ConversationList;
