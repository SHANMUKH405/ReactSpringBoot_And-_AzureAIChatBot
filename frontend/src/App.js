import React, { useState, useEffect } from 'react';
import { ConfigProvider, Layout, Button } from 'antd';
import { MenuOutlined, LogoutOutlined } from '@ant-design/icons';
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
  const [sidebarCollapsed, setSidebarCollapsed] = useState(false);
  const [isMobile, setIsMobile] = useState(window.innerWidth <= 768);

  // Handle window resize
  useEffect(() => {
    const handleResize = () => {
      const mobile = window.innerWidth <= 768;
      setIsMobile(mobile);
      if (!mobile && sidebarCollapsed) {
        setSidebarCollapsed(false);
      }
    };
    window.addEventListener('resize', handleResize);
    return () => window.removeEventListener('resize', handleResize);
  }, [sidebarCollapsed]);

  // Check if user is logged in on mount and load conversations
  useEffect(() => {
    const savedUser = localStorage.getItem('user');
    if (savedUser) {
      const userData = JSON.parse(savedUser);
      setUser(userData);
      // Load conversations when user is restored from localStorage
      loadConversations();
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
      // Auto-select first conversation if available and none selected
      if (convs.length > 0 && !activeConversationId) {
        setActiveConversationId(convs[0].id?.toString());
      }
    } catch (error) {
      console.error('Error loading conversations:', error);
    }
  };

  // Select conversation
  const handleSelectConversation = (conversationId) => {
    setActiveConversationId(conversationId?.toString());
    // Close sidebar on mobile after selection
    if (isMobile) {
      setSidebarCollapsed(true);
    }
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
      // Close sidebar on mobile after creation
      if (isMobile) {
        setSidebarCollapsed(true);
      }
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
      <Layout style={{ minHeight: '100vh', overflow: 'hidden' }}>
        {/* Sidebar with Conversations */}
        <Sider
          width={280}
          collapsible
          collapsed={sidebarCollapsed}
          onCollapse={setSidebarCollapsed}
          breakpoint="lg"
          collapsedWidth={isMobile ? 0 : 0}
          trigger={null}
          style={{
            background: '#fff',
            borderRight: '1px solid #f0f0f0',
            overflow: 'hidden',
            position: isMobile ? 'fixed' : undefined,
            height: '100vh',
            zIndex: isMobile ? 100 : undefined,
            boxShadow: isMobile && !sidebarCollapsed ? '2px 0 8px rgba(0,0,0,0.15)' : 'none',
          }}
          className={isMobile ? "chat-sidebar-mobile" : "chat-sidebar"}
        >
          <ConversationList
            conversations={conversations}
            activeConversationId={activeConversationId}
            onSelectConversation={handleSelectConversation}
            onDeleteConversation={handleDeleteConversation}
            onCreateConversation={handleCreateConversation}
          />
          {/* Logout button at bottom */}
          <div style={{ 
            padding: '16px', 
            borderTop: '1px solid #f0f0f0',
            background: '#fff',
            position: 'absolute',
            bottom: 0,
            left: 0,
            right: 0,
          }}>
            <Button
              type="primary"
              danger
              icon={<LogoutOutlined />}
              onClick={handleLogout}
              block
              style={{ height: '40px', fontSize: '14px' }}
            >
              {!sidebarCollapsed && `Logout (${user.username})`}
            </Button>
          </div>
        </Sider>

        {/* Mobile overlay when sidebar is open */}
        {isMobile && !sidebarCollapsed && (
          <div
            style={{
              position: 'fixed',
              top: 0,
              left: 0,
              right: 0,
              bottom: 0,
              background: 'rgba(0, 0, 0, 0.45)',
              zIndex: 99,
            }}
            onClick={() => setSidebarCollapsed(true)}
          />
        )}

        {/* Main Content Area */}
        <Layout style={{ 
          marginLeft: isMobile ? 0 : undefined,
          height: '100vh',
        }}>
          <Content style={{ height: '100vh', overflow: 'hidden', position: 'relative' }}>
            {/* Mobile menu button */}
            {isMobile && (
              <Button
                type="primary"
                icon={<MenuOutlined />}
                onClick={() => setSidebarCollapsed(!sidebarCollapsed)}
                shape="circle"
                size="large"
                style={{
                  position: 'fixed',
                  top: 16,
                  left: 16,
                  zIndex: 101,
                  boxShadow: '0 2px 8px rgba(0,0,0,0.15)',
                }}
              />
            )}
            <div className="App" style={{ height: '100vh', display: 'flex', flexDirection: 'column' }}>
              <main className="App-main" style={{ flex: 1, overflow: 'hidden' }}>
                <ChatBox
                  conversationId={activeConversationId}
                  onConversationChange={setActiveConversationId}
                  onConversationsUpdate={loadConversations}
                />
              </main>
            </div>
          </Content>
        </Layout>
      </Layout>
    </ConfigProvider>
  );
}

export default App;
