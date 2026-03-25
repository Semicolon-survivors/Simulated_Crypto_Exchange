import { defineConfig } from 'vite';

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    // Dev-only lightweight API mocks to keep the UI usable when the backend is offline.
    {
      name: 'dev-mock-api',
      apply: 'serve',
      configureServer(server) {
        // GET /api/ping -> { status: "ok" }
        server.middlewares.use('/api/ping', (req, res, next) => {
          if (req.method !== 'GET') return next();
          res.setHeader('Content-Type', 'application/json');
          res.end(JSON.stringify({ status: 'ok' }));
        });

        // GET /api/time -> { now: ISO_DATE }
        server.middlewares.use('/api/time', (req, res, next) => {
          if (req.method !== 'GET') return next();
          res.setHeader('Content-Type', 'application/json');
          res.end(JSON.stringify({ now: new Date().toISOString() }));
        });

        // GET /api/wallets/:userId/balances -> demo balances
        server.middlewares.use('/api/wallets', (req: any, res: any, next: any) => {
          if (req.method !== 'GET') return next();
          // Expecting path like /:userId/balances
          const match = req.url && typeof req.url === 'string' ? req.url.match(/^\/([^/]+)\/balances\/?$/) : null;
          if (!match) return next();
          const userId = match[1];
          const demo = [
            { currency: 'USD', amount: 10000 },
            { currency: 'BTC', amount: 1.0 }
          ];
          res.setHeader('Content-Type', 'application/json');
          res.end(JSON.stringify(demo));
        });

        // GET /api/orders?userId=... -> demo/empty orders
        server.middlewares.use('/api/orders', (req: any, res: any, next: any) => {
          if (req.method !== 'GET') return next();
          // Simple mock: return empty list or a small demo order list
          const url = new URL(req.originalUrl || req.url, 'http://localhost');
          const userId = url.searchParams.get('userId');
          const orders = []; // Put demo orders here if desired
          res.setHeader('Content-Type', 'application/json');
          res.end(JSON.stringify(orders));
        });

        // POST /api/chat -> echoes a chat message structure
        server.middlewares.use('/api/chat', (req: any, res: any, next: any) => {
          if (req.method !== 'POST') return next();
          let body = '';
          req.on('data', (chunk: any) => (body += chunk));
          req.on('end', () => {
            try {
              const parsed = body ? JSON.parse(body) : {};
              const from = parsed?.from ?? 'anonymous';
              const text = parsed?.text ?? '';
              const message = { from, text, sentAt: new Date().toISOString() };
              res.statusCode = 202;
              res.setHeader('Content-Type', 'application/json');
              res.end(JSON.stringify(message));
            } catch {
              res.statusCode = 400;
              res.end('Invalid JSON');
            }
          });
        });
      }
    }
  ],
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
        // Soften noisy proxy errors during dev if backend is down
        configure: (proxy) => {
          proxy.on('error', () => {
            // Swallow errors to avoid console spam; frontend handles offline state.
          });
        }
      },
      '/ws': {
        target: 'http://localhost:8080',
        ws: true,
        changeOrigin: true,
        secure: false,
        configure: (proxy) => {
          proxy.on('error', () => {
            // Swallow WebSocket proxy errors when backend is offline.
          });
        }
      }
    }
  }
});
