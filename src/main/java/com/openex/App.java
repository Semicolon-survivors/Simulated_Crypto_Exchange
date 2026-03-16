package com.openex;

import com.openex.common.Currency;
import com.openex.trading.InMemoryTradingService;
import com.openex.trading.Order;
import com.openex.trading.OrderRequest;
import com.openex.trading.OrderSide;
import com.openex.trading.OrderType;
import com.openex.trading.TradingService;
import com.openex.wallet.InMemoryWalletService;
import com.openex.wallet.WalletService;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class App {
    public static void main(String[] args) {
        WalletService walletService = new InMemoryWalletService();
        TradingService tradingService = new InMemoryTradingService(walletService);

        UUID userId = UUID.randomUUID();
        walletService.createWallet(userId);

        walletService.deposit(userId, Currency.USD, new BigDecimal("10000"));
        walletService.deposit(userId, Currency.BTC, new BigDecimal("1.0"));

        OrderRequest buyLimit = new OrderRequest()
                .setUserId(userId)
                .setType(OrderType.LIMIT)
                .setSide(OrderSide.BUY)
                .setQuantity(new BigDecimal("0.10"))     // 0.10 BTC
                .setLimitPrice(new BigDecimal("50000")); // $50,000

        Order order = tradingService.placeOrder(buyLimit);

        System.out.println("Placed order id=" + order.getId() + " status=" + order.getStatus());
        System.out.println("USD available=" + walletService.getAvailable(userId, Currency.USD)
                + " reserved=" + walletService.getReserved(userId, Currency.USD));
        System.out.println("BTC available=" + walletService.getAvailable(userId, Currency.BTC)
                + " reserved=" + walletService.getReserved(userId, Currency.BTC));

        List<Order> open = tradingService.listOpenOrders(userId);
        System.out.println("Open orders: " + open.size());
        for (Order o : open) {
            System.out.println(" - " + o.getId() + " " + o.getSide() + " " + o.getType());
        }
    }
}
