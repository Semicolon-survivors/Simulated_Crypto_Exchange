package com.openex.service;

import com.openex.entity.Order;
import com.openex.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class OrderService {

    private final Optional<OrderRepository> orderRepository;

    public OrderService(Optional<OrderRepository> orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional
    public Order createOrder(Order order) {
        return orderRepository
                .orElseThrow(() -> new IllegalStateException(
                        "OrderRepository is not available because JPA/DataSource autoconfiguration is disabled. " +
                        "Configure a datasource to enable persistence."))
                .save(order);
    }
}
