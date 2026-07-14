package it.unibo.KikiStore.model.economy.impl;
import it.unibo.KikiStore.model.economy.api.PotionPriceCalculator;
import it.unibo.KikiStore.model.inventory.api.Ingredient;
import it.unibo.KikiStore.model.inventory.api.Recipe;
import it.unibo.KikiStore.model.order.api.CustomerRequest;
import it.unibo.KikiStore.model.order.impl.IngredientRequest;
public class PotionPriceCalculatorImpl implements PotionPriceCalculator {
    private final double profitMargin;

    public PotionPriceCalculatorImpl(double profitMargin) {
        this.profitMargin = profitMargin;
    }

    @Override
    public int calculatePrice(Recipe recipe, CustomerRequest request) {
        int cost = 0;

        for (Ingredient ingredient : recipe.getIngredients()) {
            cost += ingredient.getPrice();
        }

        int price = (int) Math.round(cost * (1 + profitMargin));
        if (request instanceof IngredientRequest) {
            IngredientRequest ingredientRequest = (IngredientRequest) request;
            price -= ingredientRequest.getIngredient().getPrice();
        }
        return Math.max(price,0);
    }

    @Override
    public double getProfitMargin() {
        return profitMargin;
    }
}
