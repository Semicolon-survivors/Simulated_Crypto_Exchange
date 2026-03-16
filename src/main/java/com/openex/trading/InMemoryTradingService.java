package com.openex.trading;

import com.openex.common.AmountUtil;
import com.openex.common.Currency;
import com.openex.common.exceptions.OrderNotFoundException;
import com.openex.common.exceptions.ValidationException;
import com.openex.wallet.WalletService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class InMemoryTradingService implements TradingService {

    private final WalletService walletService;
    private final ConcurrentMap<UUID, Order> orders = new ConcurrentHashMap<>();
    private final ConcurrentMap<UUID, List<UUID>> ordersByUser = new ConcurrentHashMap<>();

    public InMemoryTradingService(WalletService walletService) {
        this.walletService = Objects.requireNonNull(walletService, "walletService");
    }

    @Override
    public Order placeOrder(OrderRequest request) {
        validateRequest(request);

        UUID id = UUID.randomUUID();
        UUID userId = request.getUserId();
        OrderSide side = request.getSide();
        OrderType type = request.getType();

        // Determine reservation
        Currency reserveCurrency;
        BigDecimal reserveAmount;

        switch (type) {
            case LIMIT -> {
                if (request.getQuantity() == null || request.getLimitPrice() == null) {
                    throw new ValidationException("LIMIT order requires quantity (BTC) and limitPrice (USD)");
                }
                BigDecimal qtyBtc = request.getQuantity().setScale(Currency.BTC.getScale(), Currency.BTC.getDefaultRounding());
                AmountUtil.requirePositive(qtyBtc, "quantity");
                BigDecimal priceUsd = request.getLimitPrice().setScale(Currency.USD.getScale(), Currency.USD.getDefaultRounding());
                AmountUtil.requirePositive(priceUsd, "limitPrice");

                if (side == OrderSide.BUY) {
                    reserveCurrency = Currency.USD;
                    BigDecimal notional = priceUsd.multiply(qtyBtc);
                    // Reserve UP to ensure sufficient funds after rounding
                    reserveAmount = notional.setScale(Currency.USD.getScale(), RoundingMode.UP);
                } else {
                    reserveCurrency = Currency.BTC;
                    reserveAmount = qtyBtc;
                }

                // Perform reservation
                walletService.reserve(userId, reserveCurrency, reserveAmount);

                Order order = new Order(
                        id, userId, type, side, qtyBtc, priceUsd, reserveCurrency, reserveAmount
                );
                storeOrder(order);
                return order;
            }
            case MARKET -> {
                if (side == OrderSide.BUY) {
                    if (request.getQuoteAmount() == null) {
                        throw new ValidationException("BUY MARKET requires quoteAmount (USD)");
                    }
                    reserveCurrency = Currency.USD;
                    reserveAmount = request.getQuoteAmount().setScale(Currency.USD.getScale(), Currency.USD.getDefaultRounding());
                    AmountUtil.requirePositive(reserveAmount, "quoteAmount");
                    walletService.reserve(userId, reserveCurrency, reserveAmount);

                    Order order = new Order(
                            id, userId, type, side, null, null, reserveCurrency, reserveAmount
                    );
                    storeOrder(order);
                    return order;
                } else {
                    if (request.getQuantity() == null) {
                        throw new ValidationException("SELL MARKET requires quantity (BTC)");
                    }
                    reserveCurrency = Currency.BTC;
                    reserveAmount = request.getQuantity().setScale(Currency.BTC.getScale(), Currency.BTC.getDefaultRounding());
                    AmountUtil.requirePositive(reserveAmount, "quantity");
                    walletService.reserve(userId, reserveCurrency, reserveAmount);

                    Order order = new Order(
                            id, userId, type, side, reserveAmount, null, reserveCurrency, reserveAmount
                    );
                    storeOrder(order);
                    return order;
                }
            }
            default -> throw new IllegalStateException("Unexpected order type: " + type);
        }
    }

    @Override
    public boolean cancelOrder(UUID userId, UUID orderId) {
        Objects.requireNonNull(userId, "userId");
        Objects.requireNonNull(orderId, "orderId");
        Order order = orders.get(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new OrderNotFoundException(orderId);
        }
        synchronized (order) {
            if (order.getStatus() == OrderStatus.CANCELED || order.getStatus() == OrderStatus.FILLED) {
                return false;
            }
            // Release full remaining reserved amount (no partial fills implemented yet)
            walletService.releaseReserve(userId, order.getReservedCurrency(), order.getReservedAmount());
            order.setStatus(OrderStatus.CANCELED);
            return true;
        }
    }

    @Override
    public List<Order> listOpenOrders(UUID userId) {
        Objects.requireNonNull(userId, "userId");
        List<UUID> ids = ordersByUser.getOrDefault(userId, List.of());
        List<Order> result = new ArrayList<>(ids.size());
        for (UUID id : ids) {
            Order o = orders.get(id);
            if (o == null) continue;
            OrderStatus s = o.getStatus();
            if (s == OrderStatus.NEW || s == OrderStatus.OPEN || s == OrderStatus.PARTIALLY_FILLED) {
                result.add(o);
            }
        }
        return result;
    }

    private void storeOrder(Order order) {
        orders.put(order.getId(), order);
        ordersByUser.computeIfAbsent(order.getUserId(), k -> new ArrayList<>()).add(order.getId());
    }

    private void validateRequest(OrderRequest request) {
        if (request == null) throw new ValidationException("request is null");
        if (request.getUserId() == null) throw new ValidationException("userId is required");
        if (request.getType() == null) throw new ValidationException("type is required");
        if (request.getSide() == null) throw new ValidationException("side is required");
        // Ensure wallet exists for user
        if (!walletService.walletExists(request.getUserId())) {
            walletService.createWallet(request.getUserId());
        }
    }
}
