package it.unibo.KikiStore.controller.impl;
import java.util.List;

import it.unibo.KikiStore.controller.api.OrderController;
import it.unibo.KikiStore.model.inventory.api.Inventory;
import it.unibo.KikiStore.model.inventory.api.Potion;
import it.unibo.KikiStore.model.inventory.api.Recipe;
import it.unibo.KikiStore.model.inventory.api.RecipeBook;
import it.unibo.KikiStore.model.order.api.Order;
import it.unibo.KikiStore.model.order.api.OrderBook;
import it.unibo.KikiStore.model.player.api.Player;

public class OrderControllerImpl implements OrderController{

    private final OrderBook orderBook;
    private final RecipeBook recipeBook;
    private final Inventory inventory;
    private final Player player;


    public OrderControllerImpl(final OrderBook orderBook, final RecipeBook recipeBook,
            final Inventory inventory, final Player player) {
        this.orderBook = orderBook;
        this.recipeBook = recipeBook;
        this.inventory = inventory;
        this.player = player;
    }


    @Override
    public List<Order> getOrders() {
        return orderBook.getOrders();
    }


    @Override
    public Recipe getRecipeForOrder(Order order) {
        final var matches = recipeBook.findByEffect(order.getNeed());
        if (matches == null || matches.isEmpty()) {
            return null;
        }
        return matches.get(0);
    }


    @Override
    public boolean completeOrder(Order order) {
        final var matches = recipeBook.findByEffect(order.getNeed());
        if (matches == null || matches.isEmpty()) {
            return false;
        }

        final Recipe recipe = matches.get(0); // take first matching recipe
        final Potion requestedPotion = recipe.getPotion();
        final Potion inventoryPotion = findPotion(requestedPotion.getName());

        if (inventoryPotion == null) {
            return false;
        }

        inventory.removePotion(inventoryPotion);

        // TODO: Potion prices not yet implemented in the catalog
        // player.setMoney(player.getMoney() + requestedPotion.getPrice());

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
