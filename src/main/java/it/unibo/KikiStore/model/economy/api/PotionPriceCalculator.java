package it.unibo.KikiStore.model.economy.api;
import it.unibo.KikiStore.model.inventory.api.Recipe;

public interface PotionPriceCalculator {
    int calculatePrice(Recipe recipe);

    double getProfitMargin();
    /*
 * TODO: If the customer provides some ingredients for the recipe,their value should be subtracted from the final reward.
 */

}
