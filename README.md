# 💱 OpenEx - Simulated Crypto Exchange

🚀 A **Java-based simulated cryptocurrency exchange** designed to demonstrate core backend concepts like wallet management, order handling, and trading logic using in-memory services.

---

## 📌 Project Overview

**OpenEx** is a backend-focused project that simulates the core functionality of a crypto exchange.  
It is built with clean architecture principles and is ideal for learning:

- 💼 Wallet management
- 📊 Order lifecycle handling
- 🔄 Trading logic
- ⚙️ Service-based design

> ⚠️ Current implementation uses **in-memory storage** (no database yet)

---

## 🧩 Features (Week 1)

### 💰 Wallet Service
- Create user wallets
- Deposit funds (BTC / USD)
- Withdraw funds
- Reserve funds for trading
- Release reserved funds
- Track:
  - Available balance
  - Reserved balance

---

### 📈 Trading Service
- Place orders:
  - Market Orders
  - Limit Orders
- Cancel orders
- View open orders
- Validate funds before placing trades
- Integrates with Wallet Service for fund reservation

---

## 🛠️ Tech Stack

- ☕ Java 17
- 📦 Maven
- 🧠 In-memory data structures
- 🏗️ Modular architecture

---

## 📂 Project Structure

```
src/main/java/com/openex
│
├── common/              # Utilities & shared components
│   ├── exceptions/     # Custom exceptions
│
├── wallet/             # Wallet module
│   ├── WalletService
│   ├── InMemoryWalletService
│   ├── Wallet
│   └── Balance
│
├── trading/            # Trading module
│   ├── TradingService
│   ├── InMemoryTradingService
│   ├── Order
│   ├── OrderRequest
│   └── Enums (OrderType, OrderStatus, OrderSide)
│
└── App.java            # Entry point
```

---

## ⚙️ How to Run

### 🔧 Prerequisites
- Java 17 installed
- Maven installed

### ▶️ Run the project

```bash
# Clone the repository
git clone https://github.com/your-username/openex.git

# Navigate into project
cd openex

# Build project
mvn clean install

# Run application
mvn exec:java -Dexec.mainClass="com.openex.App"
```

---

## 🧪 Example Flow

1. Create a wallet
2. Deposit USD/BTC
3. Place a buy/sell order
4. Funds get reserved
5. Cancel or process order

---

## 🚧 Upcoming Features

- 🔄 Order matching engine
- 🗄️ Database integration (PostgreSQL)
- 🌐 REST API (Spring Boot)
- 🔌 WebSocket support
- 📊 Market data
- 💻 Frontend UI (React)

---

## 🎯 Learning Goals

This project demonstrates:
- Object-Oriented Programming (OOP)
- Service abstraction
- Exception handling
- Clean code structure
- Backend system design

---

## 🌸 Future Vision

Transform this into a **full microservices-based crypto exchange** with:
- Docker 🐳
- Kubernetes ☸️
- Terraform 🌍
- Cloud deployment ☁️

---

## ⭐ Support

If you like this project:
- ⭐ Star the repo
- 💡 Contribute ideas

---

✨ *Building real-world backend systems, one service at a time.*
