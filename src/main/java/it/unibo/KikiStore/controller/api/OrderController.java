package it.unibo.KikiStore.controller.api;

import java.util.List;

import it.unibo.KikiStore.model.inventory.api.Recipe;
import it.unibo.KikiStore.model.order.api.Dialogue;
import it.unibo.KikiStore.model.order.api.Order;

public interface OrderController {
    List<Order> getOrders();
    Recipe getRecipeForOrder(Order order);
    int getPriceForOrder(Order order);//boolean completeOrder(Order order); seems redundant. Not sure if we can merge it with getPriceForOrder(Order order) and return -1 if the order is not completeable..
    boolean isOrderReady( Order order);
    //Dialogue
    Dialogue getDialogueForOrder(Order order);
    void confirmOrder(Order order);
    boolean completeOrder(Order order);
}
