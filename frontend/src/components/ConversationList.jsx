import React from 'react';
import { List, Button, Typography, Empty } from 'antd';
import { MessageOutlined, DeleteOutlined, PlusOutlined } from '@ant-design/icons';

const { Text } = Typography;

/**
 * ConversationList Component - Displays list of conversations
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
    <div style={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
      <div style={{ padding: '16px', borderBottom: '1px solid #f0f0f0', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <Text strong style={{ fontSize: 16 }}>Conversations</Text>
        <Button
          type="primary"
          icon={<PlusOutlined />}
          size="small"
          onClick={onCreateConversation}
        >
          New
        </Button>
      </div>

      <div style={{ flex: 1, overflowY: 'auto' }}>
        {conversations.length === 0 ? (
          <Empty
            description="No conversations yet"
            style={{ marginTop: 50 }}
            image={Empty.PRESENTED_IMAGE_SIMPLE}
          />
        ) : (
          <List
            dataSource={conversations}
            renderItem={(conversation) => (
              <List.Item
                style={{
                  cursor: 'pointer',
                  backgroundColor: activeConversationId === conversation.id?.toString() ? '#e6f7ff' : 'transparent',
                  padding: '12px 16px',
                  borderLeft: activeConversationId === conversation.id?.toString() ? '3px solid #1890ff' : '3px solid transparent',
                }}
                onClick={() => onSelectConversation(conversation.id)}
                actions={[
                  <Button
                    type="text"
                    danger
                    icon={<DeleteOutlined />}
                    size="small"
                    onClick={(e) => {
                      e.stopPropagation();
                      onDeleteConversation(conversation.id);
                    }}
                  />,
                ]}
              >
                <List.Item.Meta
                  avatar={<MessageOutlined style={{ fontSize: 20, color: '#1890ff' }} />}
                  title={
                    <Text
                      ellipsis
                      style={{
                        maxWidth: 150,
                        fontWeight: activeConversationId === conversation.id?.toString() ? 'bold' : 'normal',
                      }}
                    >
                      {conversation.title || 'New Conversation'}
                    </Text>
                  }
                  description={
                    <Text type="secondary" style={{ fontSize: 12 }}>
                      {formatDate(conversation.createdAt)}
                    </Text>
                  }
                />
              </List.Item>
            )}
          />
        )}
      </div>
    </div>
  );
};

export default ConversationList;

