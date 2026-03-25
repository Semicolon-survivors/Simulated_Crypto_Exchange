import { useState } from 'react';
import {
  Button,
  Card,
  CardActions,
  CardContent,
  InputAdornment,
  Stack,
  TextField,
  ToggleButton,
  ToggleButtonGroup,
  Tooltip,
  Typography
} from '@mui/material';
import { placeOrderDemoEcho } from '../services/api';

export default function PlaceOrderForm({
  userId,
  onPlaced
}: {
  userId: string;
  onPlaced?: () => void;
}) {
  const [side, setSide] = useState<'BUY' | 'SELL'>('BUY');
  const [type, setType] = useState<'LIMIT' | 'MARKET'>('LIMIT');
  const [quantity, setQuantity] = useState<number>(0.1);
  const [price, setPrice] = useState<number>(50000);
  const [submitting, setSubmitting] = useState(false);

  const canSubmit = !!userId && quantity > 0 && (type === 'MARKET' || price > 0);

  const onSubmit = async (e?: React.FormEvent) => {
    e?.preventDefault();
    if (!canSubmit) return;
    setSubmitting(true);
    try {
      await placeOrderDemoEcho(userId, side, quantity, type === 'LIMIT' ? price : undefined);
      onPlaced && onPlaced();
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <Card>
      <CardContent>
        <Typography variant="h6">Place Order</Typography>

        <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2} sx={{ mt: 2 }}>
          <Stack spacing={1} sx={{ width: '100%' }}>
            <Typography variant="overline" color="text.secondary">
              Side
            </Typography>
            <ToggleButtonGroup
              color={side === 'BUY' ? 'success' : 'error'}
              exclusive
              value={side}
              onChange={(_, val) => val && setSide(val)}
              size="small"
            >
              <ToggleButton value="BUY">Buy</ToggleButton>
              <ToggleButton value="SELL">Sell</ToggleButton>
            </ToggleButtonGroup>
          </Stack>

          <Stack spacing={1} sx={{ width: '100%' }}>
            <Typography variant="overline" color="text.secondary">
              Type
            </Typography>
            <ToggleButtonGroup
              color="primary"
              exclusive
              value={type}
              onChange={(_, val) => val && setType(val)}
              size="small"
            >
              <ToggleButton value="LIMIT">Limit</ToggleButton>
              <ToggleButton value="MARKET">Market</ToggleButton>
            </ToggleButtonGroup>
          </Stack>
        </Stack>

        <form onSubmit={onSubmit}>
          <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2} sx={{ mt: 2 }}>
            <TextField
              label="Quantity (BTC)"
              type="number"
              inputProps={{ step: 0.001, min: 0 }}
              value={quantity}
              onChange={(e) => setQuantity(Number(e.target.value) || 0)}
              fullWidth
              helperText="Amount of BTC to trade"
            />
            <TextField
              label="Limit Price"
              type="number"
              inputProps={{ step: 1, min: 0 }}
              value={price}
              onChange={(e) => setPrice(Number(e.target.value) || 0)}
              fullWidth
              disabled={type === 'MARKET'}
              helperText={type === 'MARKET' ? 'Not required for Market orders' : 'Price per BTC (USD)'}
              InputProps={{
                startAdornment: <InputAdornment position="start">$</InputAdornment>
              }}
            />
          </Stack>
        </form>
      </CardContent>

      <CardActions sx={{ justifyContent: 'space-between', px: 2, pb: 2 }}>
        <Tooltip title={userId ? '' : 'Enter a User ID on the dashboard to enable orders'}>
          <span>
            <Button
              variant="contained"
              onClick={onSubmit}
              disabled={!canSubmit || submitting}
              color={side === 'BUY' ? 'success' : 'error'}
            >
              {submitting ? 'Submitting…' : `${side === 'BUY' ? 'Buy' : 'Sell'} ${type}`}
            </Button>
          </span>
        </Tooltip>
      </CardActions>
    </Card>
  );
}
