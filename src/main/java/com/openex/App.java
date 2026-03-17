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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@SpringBootApplication
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Bean
    WalletService walletService() {
        return new InMemoryWalletService();
    }

    @Bean
    TradingService tradingService(WalletService walletService) {
        return new InMemoryTradingService(walletService);
    }

    @Bean
    CommandLineRunner demoRunner(
            TradingService tradingService,
            WalletService walletService,
            @Value("${app.demo.initial-deposits.usd:10000}") BigDecimal initialUsd,
            @Value("${app.demo.initial-deposits.btc:1.0}") BigDecimal initialBtc
    ) {
        return args -> {
            UUID userId = UUID.randomUUID();
            walletService.createWallet(userId);

            walletService.deposit(userId, Currency.USD, initialUsd);
            walletService.deposit(userId, Currency.BTC, initialBtc);

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
        };
    }
}
