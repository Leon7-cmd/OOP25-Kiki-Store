package it.unibo.KikiStore.model.order.impl;

import java.util.ArrayList;
import java.util.List;

import it.unibo.KikiStore.model.order.api.Order;
import it.unibo.KikiStore.model.order.api.OrderBook;

public class OrderBookImpl implements OrderBook {

    private final List<Order> orders;

    public OrderBookImpl() {
        orders = new ArrayList<>();
    }

    @Override
    public List<Order> getOrders() {
        return orders;
    }

    @Override
    public void addOrder(Order order) {
        orders.add(order);
    }

    @Override
    public void removeOrder(Order order) {
        orders.remove(order);
    }
}