package it.unibo.KikiStore.model.order.impl;

import it.unibo.KikiStore.model.inventory.api.Potion;
import it.unibo.KikiStore.model.order.api.Customer;
import it.unibo.KikiStore.model.order.api.CustomerRequest;
import it.unibo.KikiStore.model.order.api.Order;
import it.unibo.KikiStore.model.order.api.OrderStatus;

/**
 * Standard implementation of {@link Order}.
 * Represents a customer's request and its current lifecycle status.
 */
public final class OrderImpl implements Order {

    private final Customer customer;
    private final CustomerRequest request;
    private OrderStatus status;
    private Potion potion;

    /**
     * @param customer the customer placing the order
     * @param request the specific request made by the customer
     */
    public OrderImpl(final Customer customer, final CustomerRequest request) {
        this.customer = customer;
        this.request = request;
        this.status = OrderStatus.PROPOSED;
    }

    @Override
    public Customer getCustomer() {
        return customer;
    }

    @Override
    public CustomerRequest getRequest() {
        return request;
    }

    @Override
    public OrderStatus getStatus() {
        return status;
    }

    @Override
    public void setStatus(final OrderStatus status) {
        this.status = status;
    }

    @Override
    public Potion getPotion() {
        return potion;
    }

    @Override
    public void setPotion(final Potion potion) {
        this.potion = potion;
    }
}