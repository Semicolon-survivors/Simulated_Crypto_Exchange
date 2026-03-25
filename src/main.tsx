// Minimal bootstrap to satisfy IDE resolution and provide a simple UI placeholder.
// This file intentionally avoids external imports to prevent module resolution warnings.
const mount = () => {
  const root = document.getElementById('app-root');
  if (root) {
    root.innerHTML = `
      <style>
        .app-placeholder {
          font-family: system-ui, -apple-system, Segoe UI, Roboto, Helvetica, Arial, "Apple Color Emoji", "Segoe UI Emoji";
          max-width: 720px;
          margin: 10vh auto;
          padding: 24px;
          line-height: 1.5;
          color: #1f2937;
          background: #ffffff;
          border: 1px solid #e5e7eb;
          border-radius: 12px;
          box-shadow: 0 10px 20px rgba(0,0,0,0.05);
          text-align: center;
        }
        .app-placeholder h1 {
          font-size: 1.75rem;
          margin: 0 0 8px 0;
        }
        .app-placeholder p {
          margin: 0;
          color: #4b5563;
        }
      </style>
      <div class="app-placeholder">
        <h1>OpenEx UI</h1>
        <p>Your frontend build pipeline is not configured yet. This is a placeholder.</p>
      </div>
    `;
  }
};

// Run immediately
mount();

// Export an empty object to ensure this is treated as a module.
export {};
