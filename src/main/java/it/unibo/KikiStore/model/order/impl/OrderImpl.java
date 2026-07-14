package it.unibo.KikiStore.model.order.impl;
import it.unibo.KikiStore.model.order.api.Customer;
import it.unibo.KikiStore.model.order.api.CustomerRequest;
import it.unibo.KikiStore.model.order.api.Order;
public class OrderImpl implements Order {
    private final Customer customer;
    private final CustomerRequest request;

    public OrderImpl(final Customer customer, final CustomerRequest request) {
        this.customer = customer;
        this.request = request;
    }

    @Override   
    public Customer getCustomer() {
        return customer;
    }

    @Override
    public CustomerRequest getRequest() {
        return request;
    }
}
