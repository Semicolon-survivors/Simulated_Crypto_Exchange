package com.openex.trading;

import java.util.List;
import java.util.UUID;

public interface TradingService {
    Order placeOrder(OrderRequest request);

    boolean cancelOrder(UUID userId, UUID orderId);

    List<Order> listOpenOrders(UUID userId);
}
