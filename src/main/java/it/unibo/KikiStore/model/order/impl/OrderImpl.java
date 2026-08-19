package it.unibo.KikiStore.model.order.impl;
import it.unibo.KikiStore.model.inventory.api.Potion;
import it.unibo.KikiStore.model.order.api.Customer;
import it.unibo.KikiStore.model.order.api.CustomerRequest;
import it.unibo.KikiStore.model.order.api.Order;
import it.unibo.KikiStore.model.order.api.OrderStatus;
public class OrderImpl implements Order {
    private final Customer customer;
    private final CustomerRequest request;
    private OrderStatus status;
    private Potion potion;

    public OrderImpl(final Customer customer, final CustomerRequest request) {
        this.customer = customer;
        this.request = request;
        this.status = OrderStatus.PROPOSED;
        //this.potion = null;
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
    
