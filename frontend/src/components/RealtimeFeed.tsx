import { useEffect, useRef, useState } from 'react';
import {
  Avatar,
  Button,
  Card,
  CardContent,
  Divider,
  List,
  ListItem,
  ListItemAvatar,
  ListItemText,
  Stack,
  TextField,
  Typography,
  Box
} from '@mui/material';
import SendIcon from '@mui/icons-material/Send';
import ForumOutlinedIcon from '@mui/icons-material/ForumOutlined';
import { connect, disconnect, subscribe } from '../services/ws';
import { postChat } from '../services/api';

type FeedItem = { from: string; text: string; sentAt: string };

export default function RealtimeFeed() {
  const [items, setItems] = useState<FeedItem[]>([]);
  const [input, setInput] = useState('');
  const subRef = useRef<any>(null);

  useEffect(() => {
    connect(
      () => {
        subRef.current = subscribe('/topic/chat', (msg) => {
          try {
            const body = JSON.parse(msg.body) as FeedItem;
            setItems((prev) => [body, ...prev].slice(0, 100));
          } catch {
            // ignore
          }
        });
      },
      () => {
        subRef.current?.unsubscribe?.();
      }
    );
    return () => {
      subRef.current?.unsubscribe?.();
      disconnect();
    };
  }, []);

  const send = async () => {
    const text = input.trim();
    if (!text) return;
    await postChat({ from: 'dashboard', text });
    setInput('');
  };

  const onKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      void send();
    }
  };

  return (
    <Card>
      <CardContent>
        <Stack direction="row" alignItems="center" justifyContent="space-between" sx={{ mb: 1 }}>
          <Typography variant="h6">Realtime Feed</Typography>
        </Stack>
        <Stack direction="row" spacing={1} sx={{ mb: 2 }}>
          <TextField
            size="small"
            placeholder="Say something…"
            value={input}
            onChange={(e) => setInput(e.target.value)}
            onKeyDown={onKeyDown}
            fullWidth
          />
          <Button variant="contained" endIcon={<SendIcon />} onClick={send} disabled={!input.trim()}>
            Send
          </Button>
        </Stack>
        <Divider />
        <Box sx={{ maxHeight: 320, overflowY: 'auto' }}>
          <List dense>
            {items.length === 0 && (
              <Stack alignItems="center" justifyContent="center" sx={{ py: 4, color: 'text.secondary' }}>
                <ForumOutlinedIcon />
                <Typography variant="body2" sx={{ mt: 1 }}>
                  No messages yet — start the conversation!
                </Typography>
              </Stack>
            )}
            {items.map((m, idx) => (
              <ListItem key={idx} alignItems="flex-start">
                <ListItemAvatar>
                  <Avatar>{(m.from || 'A')[0]?.toUpperCase()}</Avatar>
                </ListItemAvatar>
                <ListItemText
                  primaryTypographyProps={{ fontWeight: 600 }}
                  primary={`${m.from || 'anonymous'} • ${new Date(m.sentAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}`}
                  secondary={m.text}
                />
              </ListItem>
            ))}
          </List>
        </Box>
      </CardContent>
    </Card>
  );
}
