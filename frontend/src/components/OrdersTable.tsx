import {
  Card,
  CardContent,
  IconButton,
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableRow,
  Tooltip,
  Typography
} from '@mui/material';
import RefreshIcon from '@mui/icons-material/Refresh';

type Order = {
  id: string;
  side: 'BUY' | 'SELL';
  type: 'LIMIT' | 'MARKET';
  quantity: number;
  limitPrice?: number;
  status: string;
  createdAt: string;
};

export default function OrdersTable({
  orders,
  onRefresh
}: {
  orders: Order[];
  onRefresh?: () => void | Promise<void>;
}) {
  const data = orders?.length
    ? orders
    : [
        { id: 'demo-1', side: 'BUY', type: 'LIMIT', quantity: 0.1, limitPrice: 50000, status: 'NEW', createdAt: new Date().toISOString() }
      ];

  return (
    <Card>
      <CardContent>
        <Typography variant="h6" sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
          Recent Orders
          {onRefresh && (
            <Tooltip title="Refresh">
              <IconButton size="small" onClick={() => onRefresh()}>
                <RefreshIcon fontSize="small" />
              </IconButton>
            </Tooltip>
          )}
        </Typography>
        <Table size="small">
          <TableHead>
            <TableRow>
              <TableCell>ID</TableCell>
              <TableCell>Side</TableCell>
              <TableCell>Type</TableCell>
              <TableCell align="right">Quantity</TableCell>
              <TableCell align="right">Limit Price</TableCell>
              <TableCell>Status</TableCell>
              <TableCell>Created</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {data.map((o) => (
              <TableRow key={o.id}>
                <TableCell sx={{ maxWidth: 120, overflow: 'hidden', textOverflow: 'ellipsis' }}>{o.id}</TableCell>
                <TableCell>{o.side}</TableCell>
                <TableCell>{o.type}</TableCell>
                <TableCell align="right">{o.quantity}</TableCell>
                <TableCell align="right">{o.limitPrice ? `$${o.limitPrice.toLocaleString()}` : '-'}</TableCell>
                <TableCell>{o.status}</TableCell>
                <TableCell>{new Date(o.createdAt).toLocaleString()}</TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </CardContent>
    </Card>
  );
}
