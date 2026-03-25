import { useEffect, useMemo, useRef, useState } from 'react';
import { Card, CardContent, CardHeader, IconButton, Tooltip } from '@mui/material';
import PauseIcon from '@mui/icons-material/Pause';
import PlayArrowIcon from '@mui/icons-material/PlayArrow';
import RestartAltIcon from '@mui/icons-material/RestartAlt';

import {
  Chart as ChartJS,
  LineElement,
  PointElement,
  LinearScale,
  TimeScale,
  CategoryScale,
  Tooltip as ChartTooltip,
  Legend,
  Filler
} from 'chart.js';
import 'chartjs-adapter-date-fns';
import { Line } from 'react-chartjs-2';
import { connect, disconnect, subscribe } from '../services/ws';

ChartJS.register(LineElement, PointElement, LinearScale, TimeScale, CategoryScale, ChartTooltip, Legend, Filler);

type Point = { x: number; y: number };

export default function LivePriceChart() {
  const [series, setSeries] = useState<Point[]>([]);
  const [paused, setPaused] = useState(false);
  const maxPoints = 180; // last 3 minutes @ 1s

  const subRef = useRef<any>(null);
  const simRef = useRef<number | null>(null);
  const liveSeenRef = useRef(false);
  const lastPriceRef = useRef<number>(50000);

  const addPoint = (ts: number, price: number) => {
    if (paused) return;
    lastPriceRef.current = price;
    setSeries((prev) => {
      const next = [...prev, { x: ts, y: price }];
      return next.length > maxPoints ? next.slice(next.length - maxPoints) : next;
    });
  };

  const startSimulation = () => {
    if (simRef.current) return;
    simRef.current = window.setInterval(() => {
      const prev = lastPriceRef.current;
      // random walk with small drift
      const delta = (Math.random() - 0.5) * 50; // +/- $25
      const next = Math.max(1000, prev + delta);
      addPoint(Date.now(), parseFloat(next.toFixed(2)));
    }, 1000);
  };

  const stopSimulation = () => {
    if (simRef.current) {
      window.clearInterval(simRef.current);
      simRef.current = null;
    }
  };

  useEffect(() => {
    // Begin with simulated data to show instant chart
    startSimulation();

    // Connect WS and subscribe to price topic
    connect(
      () => {
        // onConnect
        subRef.current = subscribe('/topic/price', (msg) => {
          try {
            const obj = JSON.parse(msg.body);
            let price: number | null = null;
            let ts = Date.now();

            if (typeof obj?.price === 'number') price = obj.price;
            else if (typeof obj?.p === 'number') price = obj.p;
            else if (obj?.text) {
              // fallback: try to parse price embedded in text
              const m = String(obj.text).match(/(\d{4,7}(?:\.\d+)?)/);
              if (m) price = parseFloat(m[1]);
            }

            if (typeof obj?.ts === 'number') ts = obj.ts;
            else if (typeof obj?.ts === 'string') {
              const parsed = Date.parse(obj.ts);
              if (!Number.isNaN(parsed)) ts = parsed;
            } else if (typeof obj?.sentAt === 'string') {
              const parsed = Date.parse(obj.sentAt);
              if (!Number.isNaN(parsed)) ts = parsed;
            }

            if (typeof price === 'number' && isFinite(price)) {
              if (!liveSeenRef.current) {
                liveSeenRef.current = true;
                stopSimulation(); // switch to live
              }
              addPoint(ts, price);
            }
          } catch {
            // ignore malformed messages
          }
        });
      },
      () => {
        // onDisconnect
        subRef.current?.unsubscribe?.();
      }
    );

    return () => {
      subRef.current?.unsubscribe?.();
      stopSimulation();
      disconnect();
    };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  // Pause/resume affects only adding new points; simulation keeps its interval alive but addPoint is a no-op when paused.
  const togglePause = () => setPaused((p) => !p);
  const clearData = () => setSeries([]);

  const options = useMemo(
    () => ({
      responsive: true,
      animation: false as const,
      maintainAspectRatio: false,
      interaction: { mode: 'nearest' as const, intersect: false },
      scales: {
        x: {
          type: 'time' as const,
          time: { tooltipFormat: 'PPpp' },
          ticks: { maxRotation: 0 },
          grid: { display: false }
        },
        y: {
          title: { display: true, text: 'Price (USD)' },
          ticks: { callback: (v: any) => `$${Number(v).toLocaleString()}` },
          grid: { color: 'rgba(0,0,0,0.05)' }
        }
      },
      plugins: {
        legend: { display: false },
        tooltip: {
          callbacks: {
            label: (ctx: any) => ` $${ctx.parsed.y.toLocaleString()}`
          }
        }
      },
      elements: {
        point: { radius: 0, hitRadius: 6 }
      }
    }),
    []
  );

  const data = useMemo(
    () => ({
      datasets: [
        {
          label: 'BTC/USD',
          data: series,
          borderColor: '#00bcd4',
          backgroundColor: 'rgba(0, 188, 212, 0.18)',
          fill: true,
          tension: 0.25
        }
      ]
    }),
    [series]
  );

  return (
    <Card>
      <CardHeader
        title="Live Price (BTC/USD)"
        action={
          <>
            <Tooltip title={paused ? 'Resume' : 'Pause'}>
              <IconButton onClick={togglePause} size="small" color="primary">
                {paused ? <PlayArrowIcon /> : <PauseIcon />}
              </IconButton>
            </Tooltip>
            <Tooltip title="Clear">
              <IconButton onClick={clearData} size="small" color="primary">
                <RestartAltIcon />
              </IconButton>
            </Tooltip>
          </>
        }
      />
      <CardContent sx={{ height: 320, pt: 0 }}>
        <Line options={options as any} data={data} />
      </CardContent>
    </Card>
  );
}
