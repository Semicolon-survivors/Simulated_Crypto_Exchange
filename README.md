# OpenEx - Simulated Crypto Exchange

Week 1 scope: User Wallet & Trading Service modules (in-memory).

## Modules (Week 1)

- Wallet Service
  - Manages per-user balances for BTC and USD with available and reserved buckets.
  - Supports: create wallet, deposit, withdraw, reserve funds (for orders), release reserve, consume on fill.
- Trading Service
  - Validates and accepts limit/market orders for BTC/USD.
  - Reserves required funds via the Wallet Service and tracks in-memory order state.
  - Supports: place order, cancel order, list open orders.
  - Matching/market data/REST/WebSocket/React UI to be added in subsequent weeks.

## Build

- Java: 17
- Tooling: Maven
