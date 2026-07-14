package it.unibo.KikiStore.model.order.api;

public interface Order {
    Customer getCustomer();
    CustomerRequest getRequest();  // può essere NeedRequest o IngredientRequest
}