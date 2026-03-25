import { useEffect, useMemo, useState } from 'react';
import {
  Alert,
  Box,
  Card,
  CardContent,
  CardHeader,
  Chip,
  Grid,
  IconButton,
  InputAdornment,
  Skeleton,
  Stack,
  TextField,
  Tooltip,
  Typography,
  Button
} from '@mui/material';
import CloudDoneIcon from '@mui/icons-material/CloudDone';
import CloudOffIcon from '@mui/icons-material/CloudOff';
import AccessTimeIcon from '@mui/icons-material/AccessTime';
import PersonSearchIcon from '@mui/icons-material/PersonSearch';
import RefreshIcon from '@mui/icons-material/Refresh';
import BalanceCard from './BalanceCard';
import OrdersTable from './OrdersTable';
import PlaceOrderForm from './PlaceOrderForm';
import RealtimeFeed from './RealtimeFeed';
import LivePriceChart from './LivePriceChart';
import { getPing, getTime, getBalances, getOrders } from '../services/api';

export default function Dashboard() {
  const [userId, setUserId] = useState<string>('');
  const [serverOk, setServerOk] = useState<boolean>(false);
  const [serverTime, setServerTime] = useState<string>('');
  const [balances, setBalances] = useState<{ currency: string; amount: number }[]>([]);
  const [orders, setOrders] = useState<any[]>([]);
  const [error, setError] = useState<string>('');
  const [loadingUser, setLoadingUser] = useState(false);

  useEffect(() => {
    (async () => {
      try {
        const ping = await getPing();
        setServerOk(ping.status === 'ok');
      } catch {
        setServerOk(false);
      }
      try {
        const t = await getTime();
        setServerTime(t.now);
      } catch {
        setServerTime('');
      }
    })();
  }, []);

  const fetchUserData = async (uid: string) => {
    if (!uid) {
      setBalances([]);
      setOrders([]);
      setError('');
      return;
    }
    setLoadingUser(true);
    try {
      const [bals, ords] = await Promise.all([getBalances(uid), getOrders(uid)]);
      setBalances(bals);
      setOrders(ords);
      setError('');
    } catch {
      setError('Failed to load user data');
    } finally {
      setLoadingUser(false);
    }
  };

  useEffect(() => {
    // auto-fetch on change
    fetchUserData(userId);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [userId]);

  const refreshUserData = async () => {
    if (userId) {
      await fetchUserData(userId);
    }
  };

  const statusChip = useMemo(
    () => (
      <Chip
        icon={serverOk ? <CloudDoneIcon /> : <CloudOffIcon />}
        label={serverOk ? 'Server: Online' : 'Server: Offline'}
        color={serverOk ? 'success' : 'error'}
        size="small"
        variant="filled"
      />
    ),
    [serverOk]
  );

  const timeChip = useMemo(
    () =>
      serverTime ? (
        <Chip
          icon={<AccessTimeIcon />}
          label={`Time: ${new Date(serverTime).toLocaleString()}`}
          size="small"
          variant="outlined"
        />
      ) : null,
    [serverTime]
  );

  return (
    <Stack spacing={3} sx={{ pb: 3 }}>
      <Card
        sx={{
          p: 3,
          borderRadius: 3,
          background: (t) =>
            `linear-gradient(135deg, ${t.palette.primary.main} 0%, ${t.palette.secondary.main} 100%)`,
          color: 'common.white',
          boxShadow: 6
        }}
      >
        <Stack direction={{ xs: 'column', md: 'row' }} spacing={2} alignItems="center">
          <Box sx={{ flexGrow: 1 }}>
            <Typography variant="h4" fontWeight={700}>
              User Dashboard
            </Typography>
            <Typography variant="body2" sx={{ opacity: 0.9 }}>
              Track live prices, manage orders, and chat in real-time.
            </Typography>
          </Box>
          <Stack direction="row" spacing={1} alignItems="center">
            {statusChip}
            {timeChip}
            <Tooltip title="Refresh server time">
              <IconButton
                aria-label="refresh time"
                onClick={async () => {
                  try {
                    const t = await getTime();
                    setServerTime(t.now);
                  } catch {
                    // ignore
                  }
                }}
                sx={{ color: 'inherit' }}
              >
                <RefreshIcon />
              </IconButton>
            </Tooltip>
          </Stack>
        </Stack>
      </Card>

      <Card>
        <CardContent>
          <Stack direction={{ xs: 'column', md: 'row' }} spacing={2} alignItems="center">
            <TextField
              label="User ID"
              placeholder="Enter UUID or identifier"
              value={userId}
              onChange={(e) => setUserId(e.target.value)}
              fullWidth
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <PersonSearchIcon color="action" />
                  </InputAdornment>
                ),
                endAdornment: (
                  <InputAdornment position="end">
                    <Stack direction="row" spacing={0.5}>
                      {userId && (
                        <Tooltip title="Clear">
                          <IconButton aria-label="clear user" onClick={() => setUserId('')} size="small">
                            ✕
                          </IconButton>
                        </Tooltip>
                      )}
                      <Tooltip title="Refresh user data">
                        <span>
                          <IconButton
                            aria-label="refresh user data"
                            onClick={refreshUserData}
                            size="small"
                            disabled={!userId || loadingUser}
                          >
                            <RefreshIcon fontSize="small" />
                          </IconButton>
                        </span>
                      </Tooltip>
                    </Stack>
                  </InputAdornment>
                )
              }}
              helperText="Enter a user ID to load balances and orders. Demo values appear when empty."
            />
            <Button
              variant="contained"
              onClick={refreshUserData}
              disabled={!userId || loadingUser}
            >
              {loadingUser ? 'Loading…' : 'Load'}
            </Button>
          </Stack>
        </CardContent>
      </Card>

      {error && <Alert severity="error">{error}</Alert>}

      <Grid container spacing={3}>
        <Grid item xs={12}>
          <LivePriceChart />
        </Grid>

        <Grid item xs={12} md={6} lg={4}>
          <Stack spacing={2}>
            {loadingUser ? (
              <>
                <Skeleton variant="rounded" height={96} />
                <Skeleton variant="rounded" height={96} />
                <Skeleton variant="rounded" height={96} />
              </>
            ) : balances.length ? (
              balances.map((b) => (
                <BalanceCard key={b.currency} currency={b.currency} amount={b.amount} />
              ))
            ) : (
              <>
                <BalanceCard currency="USD" amount={10000} />
                <BalanceCard currency="BTC" amount={1.0} />
              </>
            )}
          </Stack>
        </Grid>

        <Grid item xs={12} md={6} lg={8}>
          <Card>
            <CardHeader
              title="Orders"
              action={
                <Tooltip title="Refresh orders">
                  <span>
                    <IconButton
                      aria-label="refresh orders"
                      onClick={async () => userId && setOrders(await getOrders(userId))}
                      disabled={!userId}
                    >
                      <RefreshIcon />
                    </IconButton>
                  </span>
                </Tooltip>
              }
            />
            <CardContent sx={{ pt: 0 }}>
              <OrdersTable
                orders={orders}
                onRefresh={async () => userId && setOrders(await getOrders(userId))}
              />
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} md={6}>
          <PlaceOrderForm userId={userId} onPlaced={refreshUserData} />
        </Grid>

        <Grid item xs={12} md={6}>
          <RealtimeFeed />
        </Grid>
      </Grid>

      <Box />
    </Stack>
  );
}
