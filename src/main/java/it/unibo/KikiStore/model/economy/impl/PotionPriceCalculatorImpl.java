package it.unibo.KikiStore.model.economy.impl;
import it.unibo.KikiStore.model.economy.api.PotionPriceCalculator;
import it.unibo.KikiStore.model.inventory.api.GameCatalog;
import it.unibo.KikiStore.model.inventory.api.Ingredient;
import it.unibo.KikiStore.model.inventory.api.Recipe;
public class PotionPriceCalculatorImpl implements PotionPriceCalculator {
    private final double profitMargin;
    private final GameCatalog catalog;

    public PotionPriceCalculatorImpl(double profitMargin, GameCatalog catalog) {
        this.profitMargin = profitMargin;
        this.catalog = catalog;
    }

    @Override
    public int calculatePrice(Recipe recipe) {
        int cost = 0;

        for (Ingredient ingredient : recipe.getIngredients()) {
            cost += catalog.getIngredientPrice(ingredient.getName());
        }

        return (int) Math.round(cost * (1 + profitMargin));
    }

    @Override
    public double getProfitMargin() {
        return profitMargin;
    }
}
