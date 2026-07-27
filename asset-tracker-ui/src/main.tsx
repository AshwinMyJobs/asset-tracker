import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import App from './App.tsx';
import './index.css';
import keycloak from './keycloak';

// Initialize keycloak before mounting the React App
keycloak
  .init({
    onLoad: 'login-required', // Forces redirect to Keycloak if not logged in
    checkLoginIframe: false,
  })
  .then((authenticated) => {
    if (authenticated) {
      createRoot(document.getElementById('root')!).render(
        <StrictMode>
          <App />
        </StrictMode>
      );
    } else {
      window.location.reload();
    }
  })
  .catch((error) => {
    console.error('Keycloak initialization failed', error);
  });



  