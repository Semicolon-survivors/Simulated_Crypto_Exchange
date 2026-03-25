import { AppBar, Box, Container, Toolbar, Typography, Link } from '@mui/material';
import Dashboard from './components/Dashboard';
import React from 'react';
import Dashboard from './components/Dashboard';

export default function App() {
  return <Dashboard />;
}
export default function App() {
  return (
    <Box sx={{ minHeight: '100vh', display: 'flex', flexDirection: 'column' }}>
      <AppBar position="static" color="primary" enableColorOnDark>
        <Toolbar>
          <Typography variant="h6" sx={{ flexGrow: 1 }}>
            Trading Dashboard
          </Typography>
          <Link
            href="https://localhost"
            target="_blank"
            rel="noreferrer"
            color="inherit"
            underline="hover"
            sx={{ fontSize: 14 }}
          >
            Help
          </Link>
        </Toolbar>
      </AppBar>

      <Container sx={{ flex: 1, py: 4 }}>
        <Dashboard />
      </Container>

      <Box component="footer" sx={{ py: 2, textAlign: 'center', color: 'text.secondary' }}>
        <Typography variant="body2">
          © {new Date().getFullYear()} Dashboard
        </Typography>
      </Box>
    </Box>
  );
}
