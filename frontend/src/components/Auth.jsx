import React, { useState } from 'react';
import { Form, Input, Button, Card, Tabs, message } from 'antd';
import { UserOutlined, LockOutlined, MailOutlined } from '@ant-design/icons';
import { registerUser, loginUser } from '../services/api';

/**
 * Auth Component - Handles user registration and login
 */
const Auth = ({ onLoginSuccess }) => {
  const [loading, setLoading] = useState(false);

  const onRegister = async (values) => {
    setLoading(true);
    try {
      const response = await registerUser(values.username, values.email, values.password);
      message.success('Registration successful! Please login.');
      // Switch to login tab after successful registration
    } catch (error) {
      // Handle validation errors with more detail
      let errorMessage = 'Registration failed. Please try again.';
      
      try {
        const errorData = error.response?.data;
        
        if (errorData) {
          // Handle errors object (validation errors)
          if (errorData.errors && typeof errorData.errors === 'object') {
            const errorMessages = Object.values(errorData.errors)
              .filter(msg => typeof msg === 'string')
              .map(msg => msg.trim())
              .filter(msg => msg.length > 0);
            
            if (errorMessages.length > 0) {
              errorMessage = errorMessages.join('. ');
            } else if (errorData.message) {
              errorMessage = typeof errorData.message === 'string' 
                ? errorData.message 
                : JSON.stringify(errorData.message);
            }
          } else if (errorData.message) {
            // Handle simple message
            errorMessage = typeof errorData.message === 'string' 
              ? errorData.message 
              : JSON.stringify(errorData.message);
          } else if (typeof errorData === 'string') {
            errorMessage = errorData;
          }
        } else if (error.message) {
          errorMessage = error.message;
        }
      } catch (e) {
        console.error('Error parsing error message:', e);
        errorMessage = 'Registration failed. Please check your input and try again.';
      }
      
      message.error(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  const onLogin = async (values) => {
    setLoading(true);
    try {
      const response = await loginUser(values.username, values.password);
      message.success('Login successful!');
      // Store user info in localStorage
      localStorage.setItem('user', JSON.stringify({
        id: response.userId,
        username: response.username,
        email: response.email,
      }));
      onLoginSuccess(response);
    } catch (error) {
      let errorMessage = 'Login failed. Please check your credentials.';
      
      try {
        const errorData = error.response?.data;
        if (errorData?.message) {
          errorMessage = typeof errorData.message === 'string' 
            ? errorData.message 
            : JSON.stringify(errorData.message);
        } else if (error.message) {
          errorMessage = error.message;
        }
      } catch (e) {
        console.error('Error parsing login error:', e);
      }
      
      message.error(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{
      display: 'flex',
      justifyContent: 'center',
      alignItems: 'center',
      minHeight: '100vh',
      background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
      padding: '20px'
    }}>
      <Card
        style={{
          width: '100%',
          maxWidth: 400,
          boxShadow: '0 10px 40px rgba(0,0,0,0.2)'
        }}
      >
        <div style={{ textAlign: 'center', marginBottom: 30 }}>
          <h1 style={{ margin: 0, color: '#1890ff' }}>AI Chat Assistant</h1>
          <p style={{ color: '#666', marginTop: 8 }}>Please login or register</p>
        </div>

        <Tabs
          defaultActiveKey="login"
          items={[
            {
              key: 'login',
              label: 'Login',
              children: (
                <Form
                  name="login"
                  onFinish={onLogin}
                  autoComplete="off"
                  layout="vertical"
                >
                  <Form.Item
                    label="Username"
                    name="username"
                    rules={[{ required: true, message: 'Please input your username!' }]}
                  >
                    <Input
                      prefix={<UserOutlined />}
                      placeholder="Enter your username"
                      size="large"
                    />
                  </Form.Item>

                  <Form.Item
                    label="Password"
                    name="password"
                    rules={[{ required: true, message: 'Please input your password!' }]}
                  >
                    <Input.Password
                      prefix={<LockOutlined />}
                      placeholder="Enter your password"
                      size="large"
                    />
                  </Form.Item>

                  <Form.Item>
                    <Button
                      type="primary"
                      htmlType="submit"
                      loading={loading}
                      block
                      size="large"
                    >
                      Login
                    </Button>
                  </Form.Item>
                </Form>
              ),
            },
            {
              key: 'register',
              label: 'Register',
              children: (
                <Form
                  name="register"
                  onFinish={onRegister}
                  autoComplete="off"
                  layout="vertical"
                >
                  <Form.Item
                    label="Username"
                    name="username"
                    rules={[
                      { required: true, message: 'Please input your username!' },
                      { min: 3, message: 'Username must be at least 3 characters!' },
                      { max: 50, message: 'Username must be less than 50 characters!' }
                    ]}
                    extra="Choose a unique username (3-50 characters)"
                  >
                    <Input
                      prefix={<UserOutlined />}
                      placeholder="Choose a username (not your email)"
                      size="large"
                    />
                  </Form.Item>

                  <Form.Item
                    label="Email"
                    name="email"
                    rules={[
                      { required: true, message: 'Please input your email!' },
                      { type: 'email', message: 'Please enter a valid email!' }
                    ]}
                  >
                    <Input
                      prefix={<MailOutlined />}
                      placeholder="Enter your email"
                      size="large"
                    />
                  </Form.Item>

                  <Form.Item
                    label="Password"
                    name="password"
                    rules={[
                      { required: true, message: 'Please input your password!' },
                      { min: 6, message: 'Password must be at least 6 characters!' }
                    ]}
                  >
                    <Input.Password
                      prefix={<LockOutlined />}
                      placeholder="Choose a password"
                      size="large"
                    />
                  </Form.Item>

                  <Form.Item>
                    <Button
                      type="primary"
                      htmlType="submit"
                      loading={loading}
                      block
                      size="large"
                    >
                      Register
                    </Button>
                  </Form.Item>
                </Form>
              ),
            },
          ]}
        />
      </Card>
    </div>
  );
};

export default Auth;

