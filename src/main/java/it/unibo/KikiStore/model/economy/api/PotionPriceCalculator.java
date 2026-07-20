package it.unibo.KikiStore.model.economy.api;
import it.unibo.KikiStore.model.inventory.api.Recipe;
import it.unibo.KikiStore.model.order.api.CustomerRequest;

public interface PotionPriceCalculator {
    int calculatePrice(Recipe recipe, CustomerRequest request);

    double getProfitMargin();

}
