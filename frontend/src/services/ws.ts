import SockJS from 'sockjs-client/dist/sockjs.min.js';
import { Client, IMessage, StompSubscription } from '@stomp/stompjs';

// Prefer explicit VITE_WS_BASE; do not auto-connect in dev unless provided
const ENV = (import.meta as any).env || {};
const RAW_WS_BASE = typeof ENV.VITE_WS_BASE === 'string' ? ENV.VITE_WS_BASE : undefined;
// If not explicitly provided, don't set a base (disables WS by default in dev)
const WS_BASE = RAW_WS_BASE && RAW_WS_BASE.trim().length > 0 ? RAW_WS_BASE : '';
const WS_ENABLED = !!(RAW_WS_BASE && RAW_WS_BASE.trim().length > 0);

export type OnMessage = (msg: IMessage) => void;

let client: Client | null = null;

export function connect(onConnect?: () => void, onDisconnect?: () => void) {
  if (!WS_ENABLED) return; // disabled unless VITE_WS_BASE explicitly set
  if (client && client.connected) return;
  client = new Client({
    webSocketFactory: () => new SockJS(`${WS_BASE}/ws`), // '/ws' when WS_BASE is ''
    reconnectDelay: 2000,
    debug: () => {}
  });
  if (onConnect) client.onConnect = onConnect;
  if (onDisconnect) client.onDisconnect = onDisconnect;
  client.activate();
}

export function disconnect() {
  if (client) client.deactivate();
}

export function subscribe(topic: string, onMessage: OnMessage): StompSubscription | null {
  if (!WS_ENABLED) return null;
  if (!client || !client.connected) return null;
  return client.subscribe(topic, onMessage);
}
