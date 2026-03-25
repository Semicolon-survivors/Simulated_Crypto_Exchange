import { createTheme } from '@mui/material';

const theme = createTheme({
  palette: {
    mode: 'light',
    primary: { main: '#3f51b5' },
    secondary: { main: '#00bcd4' },
    success: { main: '#2e7d32' },
    error: { main: '#d32f2f' },
    background: { default: '#f7f9fc' }
  },
  shape: { borderRadius: 10 },
  typography: {
    fontFamily: '"Inter", "Roboto", "Helvetica", "Arial", sans-serif',
    h4: { fontWeight: 700 },
    h6: { fontWeight: 600 }
  },
  components: {
    MuiCard: { styleOverrides: { root: { boxShadow: '0 6px 18px rgba(0,0,0,0.06)' } } }
  }
});

export default theme;
