package com.openex.controller;

import com.openex.common.Currency;
import com.openex.dto.TradeDto;
import com.openex.repository.TradeRepository;
import com.openex.trading.Order;
import com.openex.trading.OrderRequest;
import com.openex.trading.TradingService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * REST controller for trading operations (orders and trades).
 */
@RestController
@RequestMapping("/api/trades")
public class TradeController {

    private final ObjectProvider<TradingService> tradingServiceProvider;
    private final ObjectProvider<TradeRepository> tradeRepositoryProvider;

    public TradeController(
            ObjectProvider<TradingService> tradingServiceProvider,
            ObjectProvider<TradeRepository> tradeRepositoryProvider
    ) {
        this.tradingServiceProvider = tradingServiceProvider;
        this.tradeRepositoryProvider = tradeRepositoryProvider;
    }

    /**
     * Place an order.
     */
    @PostMapping("/orders")
    public ResponseEntity<Order> placeOrder(@RequestBody OrderRequest request) {
        TradingService service = tradingServiceProvider.getIfAvailable();
        if (service == null) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
        if (request == null) {
            return ResponseEntity.badRequest().build();
        }
        Order order = service.placeOrder(request);
        return ResponseEntity.ok(order);
    }

    /**
     * Cancel an order.
     */
    @DeleteMapping("/orders/{orderId}")
    public ResponseEntity<Void> cancelOrder(
            @PathVariable UUID orderId,
            @RequestParam("userId") UUID userId
    ) {
        TradingService service = tradingServiceProvider.getIfAvailable();
        if (service == null) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
        boolean cancelled = service.cancelOrder(userId, orderId);
        return cancelled ? ResponseEntity.noContent().build() : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    /**
     * List open orders for a user.
     */
    @GetMapping("/orders/open")
    public ResponseEntity<List<Order>> listOpenOrders(@RequestParam("userId") UUID userId) {
        TradingService service = tradingServiceProvider.getIfAvailable();
        if (service == null) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
        List<Order> orders = service.listOpenOrders(userId);
        return ResponseEntity.ok(orders);
    }

    /**
     * Recent trades for a symbol.
     */
    @GetMapping("/recent")
    public ResponseEntity<List<TradeDto>> recentTrades(
            @RequestParam("base") Currency baseCurrency,
            @RequestParam("quote") Currency quoteCurrency,
            @RequestParam(name = "limit", defaultValue = "50") int limit
    ) {
        TradeRepository repo = tradeRepositoryProvider.getIfAvailable();
        if (repo == null) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
        List<TradeDto> dtos = repo.findRecentBySymbol(baseCurrency, quoteCurrency, limit).stream()
                .map(r -> new TradeDto(r.tradeId(), r.orderId(), r.baseCurrency(), r.quoteCurrency(), r.side(), r.price(), r.quantity(), r.timestamp()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * Trades by order id.
     */
    @GetMapping("/by-order/{orderId}")
    public ResponseEntity<List<TradeDto>> tradesByOrder(@PathVariable UUID orderId) {
        TradeRepository repo = tradeRepositoryProvider.getIfAvailable();
        if (repo == null) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
        List<TradeDto> dtos = repo.findByOrderId(orderId).stream()
                .map(r -> new TradeDto(r.tradeId(), r.orderId(), r.baseCurrency(), r.quoteCurrency(), r.side(), r.price(), r.quantity(), r.timestamp()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
}
