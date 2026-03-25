import React from 'react';
import ReactDOM from 'react-dom/client';
import AppRoot from './AppRoot';

const container = document.getElementById('root');

if (container) {
  // Idempotent mount to avoid duplicate createRoot during HMR
  const root =
    (container as any)._reactRoot ??
    ((container as any)._reactRoot = ReactDOM.createRoot(container));

  root.render(
    <React.StrictMode>
      <AppRoot />
    </React.StrictMode>
  );
} else {
  const fallback = document.createElement('div');
  fallback.textContent = 'Root element not found';
  document.body.appendChild(fallback);
}
