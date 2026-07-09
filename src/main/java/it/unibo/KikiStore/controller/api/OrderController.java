package it.unibo.KikiStore.controller.api;

import java.util.List;

import it.unibo.KikiStore.model.inventory.api.Recipe;
import it.unibo.KikiStore.model.order.api.Order;

public interface OrderController {
    List<Order> getOrders();
    Recipe getRecipeForOrder(Order order);
    boolean completeOrder(Order order);
}
