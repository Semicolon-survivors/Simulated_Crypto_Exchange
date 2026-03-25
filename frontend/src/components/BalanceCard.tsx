import { Card, CardContent, Stack, Typography } from '@mui/material';

export default function BalanceCard({ currency, amount }: { currency: string; amount: number }) {
  const formatted =
    currency === 'USD'
      ? amount.toLocaleString(undefined, { style: 'currency', currency: 'USD' })
      : `${amount} ${currency}`;

  return (
    <Card
      sx={{
        borderRadius: 3,
        border: (t) => `1px solid ${t.palette.divider}`,
        transition: 'transform 120ms ease, box-shadow 120ms ease',
        '&:hover': { transform: 'translateY(-2px)', boxShadow: 4 }
      }}
    >
      <CardContent>
        <Stack spacing={0.5}>
          <Typography variant="overline" color="text.secondary">
            {currency} Balance
          </Typography>
          <Typography variant="h4" fontWeight={700}>
            {formatted}
          </Typography>
        </Stack>
      </CardContent>
    </Card>
  );
}
