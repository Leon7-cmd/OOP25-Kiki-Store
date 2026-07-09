package it.unibo.KikiStore.model.order.api;
import java.util.List;

public interface OrderBook {
    public List<Order> getOrders();
    //public List<Order> getOrdersByCustomer(Customer customer);
    void addOrder(Order order);
    void removeOrder(Order order);
}
