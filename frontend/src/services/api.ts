import axios from 'axios';

const API_BASE = import.meta.env.VITE_API_BASE || '/api';

export const api = axios.create({
  baseURL: API_BASE,
  timeout: 10000
});

export type ChatRequest = { from?: string; text: string };
export type ChatMessage = { from: string; text: string; sentAt: string };

export async function getPing(): Promise<{ status: string }> {
  const { data } = await api.get('/ping');
  return data;
}

export async function getTime(): Promise<{ now: string }> {
  const { data } = await api.get('/time');
  return data;
}

export async function postChat(payload: ChatRequest): Promise<ChatMessage> {
  const { data } = await api.post('/chat', payload);
  return data;
}

// Optional backends - will gracefully fall back if not available
export type Balance = { currency: string; amount: number };

export async function getBalances(userId: string): Promise<Balance[]> {
  try {
    const { data } = await api.get(`/wallets/${userId}/balances`);
    return data;
  } catch {
    return [
      { currency: 'USD', amount: 10000 },
      { currency: 'BTC', amount: 1.0 }
    ];
  }
}

export type Order = {
  id: string;
  side: 'BUY' | 'SELL';
  type: 'LIMIT' | 'MARKET';
  quantity: number;
  limitPrice?: number;
  status: 'NEW' | 'FILLED' | 'PARTIALLY_FILLED' | 'CANCELLED';
  createdAt: string;
};

export async function getOrders(userId: string): Promise<Order[]> {
  try {
    const { data } = await api.get(`/orders?userId=${encodeURIComponent(userId)}`);
    return data;
  } catch {
    const now = new Date().toISOString();
    return [
      { id: 'demo-1', side: 'BUY', type: 'LIMIT', quantity: 0.1, limitPrice: 50000, status: 'NEW', createdAt: now }
    ];
  }
}

export async function placeOrderDemoEcho(userId: string, side: 'BUY' | 'SELL', quantity: number, price?: number) {
  const text = `Order by ${userId || 'anonymous'}: ${side} ${quantity}${price ? ` @ ${price}` : ''}`;
  return postChat({ from: 'dashboard', text });
}
