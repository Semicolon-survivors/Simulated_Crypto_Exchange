import React from 'react';
import { Box, Container, CssBaseline, Typography } from '@mui/material';
import Dashboard from './components/Dashboard';

class ErrorBoundary extends React.Component<{ children: React.ReactNode }, { hasError: boolean; error?: unknown }> {
  constructor(props: { children: React.ReactNode }) {
    super(props);
    this.state = { hasError: false };
  }
  static getDerivedStateFromError(error: unknown) {
    return { hasError: true, error };
  }
  componentDidCatch(error: unknown, errorInfo: unknown) {
    // Surface to console for debugging
    // eslint-disable-next-line no-console
    console.error('App error:', error, errorInfo);
  }
  render() {
    if (this.state.hasError) {
      return (
        <Container sx={{ py: 4 }}>
          <Typography variant="h6" color="error" sx={{ mb: 1 }}>
            Something went wrong rendering the app.
          </Typography>
          <Typography variant="body2" color="text.secondary">
            Check the browser console for details.
          </Typography>
        </Container>
      );
    }
    return this.props.children as React.ReactElement;
  }
}

export default function AppRoot() {
  return (
    <>
      <CssBaseline />
      <Box sx={{ minHeight: '100vh', display: 'flex', flexDirection: 'column' }}>
        <ErrorBoundary>
          <Container sx={{ py: 3 }}>
            <Dashboard />
          </Container>
        </ErrorBoundary>
      </Box>
    </>
  );
}
