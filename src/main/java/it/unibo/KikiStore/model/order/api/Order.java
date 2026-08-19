package it.unibo.KikiStore.model.order.api;

import it.unibo.KikiStore.model.inventory.api.Potion;
public interface Order {
    Customer getCustomer();
    CustomerRequest getRequest();  // può essere NeedRequest o IngredientRequest
    OrderStatus getStatus();
    Potion getPotion();
    void setStatus(OrderStatus status);
    void setPotion(Potion potion);
}