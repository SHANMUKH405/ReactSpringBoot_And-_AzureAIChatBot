import React, { useState, useEffect } from 'react';
import { ConfigProvider, Layout } from 'antd';
import Auth from './components/Auth';
import ChatBox from './components/ChatBox';
import ConversationList from './components/ConversationList';
import './App.css';

const { Sider, Content } = Layout;

/**
 * App Component - Root component with Authentication and Conversation Management
 */
function App() {
  const [user, setUser] = useState(null);
  const [conversations, setConversations] = useState([]);
  const [activeConversationId, setActiveConversationId] = useState(null);

  // Check if user is logged in on mount
  useEffect(() => {
    const savedUser = localStorage.getItem('user');
    if (savedUser) {
      setUser(JSON.parse(savedUser));
    }
  }, []);

  // Handle successful login
  const handleLoginSuccess = (userData) => {
    setUser({
      id: userData.userId,
      username: userData.username,
      email: userData.email,
    });
    // Load conversations after login
    loadConversations();
  };

  // Handle logout
  const handleLogout = () => {
    localStorage.removeItem('user');
    setUser(null);
    setConversations([]);
    setActiveConversationId(null);
  };

  // Load conversations
  const loadConversations = async () => {
    try {
      const { getConversations } = await import('./services/api');
      const convs = await getConversations();
      setConversations(convs);
    } catch (error) {
      console.error('Error loading conversations:', error);
    }
  };

  // Select conversation
  const handleSelectConversation = (conversationId) => {
    setActiveConversationId(conversationId?.toString());
  };

  // Delete conversation
  const handleDeleteConversation = async (conversationId) => {
    try {
      const { deleteConversationById } = await import('./services/api');
      await deleteConversationById(conversationId);
      // Reload conversations
      await loadConversations();
      // Clear active conversation if it was deleted
      if (activeConversationId === conversationId?.toString()) {
        setActiveConversationId(null);
      }
    } catch (error) {
      console.error('Error deleting conversation:', error);
    }
  };

  // Create new conversation
  const handleCreateConversation = async () => {
    try {
      const { createConversation } = await import('./services/api');
      const newConv = await createConversation('New Conversation');
      await loadConversations();
      setActiveConversationId(newConv.id?.toString());
    } catch (error) {
      console.error('Error creating conversation:', error);
    }
  };

  // If user is not logged in, show auth screen
  if (!user) {
    return (
      <ConfigProvider
        theme={{
          token: {
            colorPrimary: '#1890ff',
            borderRadius: 8,
          },
        }}
      >
        <Auth onLoginSuccess={handleLoginSuccess} />
      </ConfigProvider>
    );
  }

  // Main app layout with conversations sidebar
  return (
    <ConfigProvider
      theme={{
        token: {
          colorPrimary: '#1890ff',
          borderRadius: 8,
        },
      }}
    >
      <Layout style={{ minHeight: '100vh' }}>
        {/* Sidebar with Conversations */}
        <Sider
          width={280}
          style={{
            background: '#fff',
            borderRight: '1px solid #f0f0f0',
            overflow: 'hidden',
          }}
        >
          <ConversationList
            conversations={conversations}
            activeConversationId={activeConversationId}
            onSelectConversation={handleSelectConversation}
            onDeleteConversation={handleDeleteConversation}
            onCreateConversation={handleCreateConversation}
          />
          {/* Logout button at bottom */}
          <div style={{ padding: '16px', borderTop: '1px solid #f0f0f0' }}>
            <button
              onClick={handleLogout}
              style={{
                width: '100%',
                padding: '8px',
                background: '#ff4d4f',
                color: 'white',
                border: 'none',
                borderRadius: '4px',
                cursor: 'pointer',
              }}
            >
              Logout ({user.username})
            </button>
          </div>
        </Sider>

        {/* Main Content Area */}
        <Layout>
          <Content>
            <div className="App">
              <main className="App-main" style={{ height: '100vh' }}>
                <ChatBox
                  conversationId={activeConversationId}
                  onConversationChange={setActiveConversationId}
                  onConversationsUpdate={loadConversations}
                />
              </main>
              <footer className="App-footer">
                <p>
                  Built with React, Ant Design, Spring Boot, and AI Integration
                </p>
              </footer>
            </div>
          </Content>
        </Layout>
      </Layout>
    </ConfigProvider>
  );
}

export default App;
