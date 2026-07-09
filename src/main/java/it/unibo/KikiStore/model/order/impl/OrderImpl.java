package it.unibo.KikiStore.model.order.impl;
import it.unibo.KikiStore.model.order.api.Order;

public class OrderImpl implements Order {
    private final String customer;
    private final String need;

    public OrderImpl(String customer, String need) {
        this.customer = customer;
        this.need = need;
    }

    @Override   
    public String getCustomer() {
        return customer;
    }

    @Override
    public String getNeed() {
        return need;
    }
}
