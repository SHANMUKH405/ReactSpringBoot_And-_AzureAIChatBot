import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import App from './App';

/**
 * Entry point of React application
 * 
 * ReactDOM.render() tells React:
 * - What to render (App component)
 * - Where to render it (element with id="root")
 * 
 * Everything starts here!
 */
const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);

