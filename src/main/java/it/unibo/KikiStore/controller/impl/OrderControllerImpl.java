package it.unibo.KikiStore.controller.impl;

import java.util.List;

import it.unibo.KikiStore.controller.api.OrderController;
import it.unibo.KikiStore.model.economy.api.PotionPriceCalculator;
import it.unibo.KikiStore.model.inventory.api.Ingredient;
import it.unibo.KikiStore.model.inventory.api.Inventory;
import it.unibo.KikiStore.model.inventory.api.Potion;
import it.unibo.KikiStore.model.inventory.api.Recipe;
import it.unibo.KikiStore.model.inventory.api.RecipeBook;
import it.unibo.KikiStore.model.order.api.CustomerRequest;
import it.unibo.KikiStore.model.order.api.Need;
import it.unibo.KikiStore.model.order.api.Order;
import it.unibo.KikiStore.model.order.api.OrderBook;
import it.unibo.KikiStore.model.order.impl.IngredientRequest;
import it.unibo.KikiStore.model.order.impl.NeedRequest;
import it.unibo.KikiStore.model.player.api.Player;

public class OrderControllerImpl implements OrderController {

    private final OrderBook orderBook;
    private final RecipeBook recipeBook;
    private final Inventory inventory;
    private final Player player;
    private final PotionPriceCalculator priceCalculator;

    public OrderControllerImpl(final OrderBook orderBook, final RecipeBook recipeBook,
            final Inventory inventory, final Player player, final PotionPriceCalculator priceCalculator) {
        this.orderBook = orderBook;
        this.recipeBook = recipeBook;
        this.inventory = inventory;
        this.player = player;
        this.priceCalculator = priceCalculator;
    }

    @Override
    public List<Order> getOrders() {
        return orderBook.getOrders();
    }

    @Override
    public Recipe getRecipeForOrder(Order order) {
        return resolveRecipe(order.getRequest());
    }

    private Recipe resolveRecipe(CustomerRequest request) {
        if (request instanceof NeedRequest needRequest) {
            Need need = needRequest.getNeed();
            final var matches = recipeBook.findByEffect(need.getName());
            if (matches == null || matches.isEmpty()) {
                return null;
            }
            return matches.get(0);
        }

        if (request instanceof IngredientRequest ingredientRequest) {
            Ingredient ingredient = ingredientRequest.getIngredient();
            return recipeBook.findByIngredients(List.of(ingredient));
        }

        return null;
    }

    @Override
    public boolean completeOrder(Order order) {
        final Recipe recipe = resolveRecipe(order.getRequest());
        if (recipe == null) {
            return false;
        }

        final Potion requestedPotion = recipe.getPotion();
        final Potion inventoryPotion = findPotion(requestedPotion.getName());

        if (inventoryPotion == null) {
            return false;
        }

        inventory.removePotion(inventoryPotion);

        int price = priceCalculator.calculatePrice(recipe, order.getRequest());
        player.setMoney(player.getMoney() + price);

        orderBook.removeOrder(order);
        return true;
    }

    private Potion findPotion(String name) {
        if (name == null) {
            return null;
        }
        for (final Potion p : inventory.getPotions()) {
            if (p != null && name.equalsIgnoreCase(p.getName())) {
                return p;
            }
        }
        return null;
    }
}