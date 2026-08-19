package it.unibo.KikiStore.controller.impl;

import java.util.ArrayList;
import java.util.List;

import it.unibo.KikiStore.controller.api.OrderController;
import it.unibo.KikiStore.model.economy.api.PotionPriceCalculator;
import it.unibo.KikiStore.model.inventory.api.Ingredient;
import it.unibo.KikiStore.model.inventory.api.Inventory;
import it.unibo.KikiStore.model.inventory.api.Potion;
import it.unibo.KikiStore.model.inventory.api.Recipe;
import it.unibo.KikiStore.model.inventory.api.RecipeBook;
import it.unibo.KikiStore.model.order.api.CustomerRequest;
import it.unibo.KikiStore.model.order.api.Dialogue;
import it.unibo.KikiStore.model.order.api.DialogueLine;
import it.unibo.KikiStore.model.order.api.Need;
import it.unibo.KikiStore.model.order.api.Order;
import it.unibo.KikiStore.model.order.api.OrderBook;
import it.unibo.KikiStore.model.order.api.OrderStatus;
import it.unibo.KikiStore.model.order.impl.DialogueImpl;
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

    @Override
    public int getPriceForOrder(Order order) {
        final Recipe recipe = resolveRecipe(order.getRequest());
        if (recipe == null) {
            return 0;
        }
        return priceCalculator.calculatePrice(recipe, order.getRequest());
    }

    @Override
    public boolean isOrderReady(final Order order) {
        final Recipe recipe = resolveRecipe(order.getRequest());
        if (recipe == null) {
            return false;
        }
        return findPotion(recipe.getPotion().getName()) != null; //if the potion is in the inventory, it is ready//before having crafting implemented, we can check if the potion is in the inventory to determine if the order is ready
    }

    //order is completed only when order status is ready ->order is removed from orderBook and potion is removed from inventory, and money is added to player
    @Override
    public boolean completeOrder(Order order) {
        final Recipe recipe = resolveRecipe(order.getRequest());
        if (recipe == null) {
            return false;
        }

        final Potion requestedPotion = recipe.getPotion();
        final Potion inventoryPotion = findPotion(requestedPotion.getName()); //if the potion is in the inventory, it will be returned, otherwise null //questo equivale a potion ready if potion ready remove from inventory and add money to player and remove order from orderBook

        if (inventoryPotion == null) {
            return false;
        } // potion is not ready or it does not exist.
        //order.getStatus() == OrderStatus.READY
        inventory.removePotion(inventoryPotion); //remove from inventory

        final int price = priceCalculator.calculatePrice(recipe, order.getRequest());
        player.setMoney(player.getMoney() + price); //add money(from order) to player

        order.setStatus(OrderStatus.DELIVERED);
        orderBook.removeOrder(order);
        return true;
    }

    @Override
    public Dialogue getDialogueForOrder(final Order order) {
    final List<DialogueLine> lines = new ArrayList<>();
    final String customerName = order.getCustomer().getName();
    final String kikiName = player.getName();
    final Recipe recipe = resolveRecipe(order.getRequest());
    final String potionName = recipe != null ? recipe.getPotion().getName() : "that potion";

    lines.add(new DialogueLine(customerName, order.getRequest().getDialogue()));

    if (isOrderReady(order)) {
        final int price = getPriceForOrder(order);
        lines.add(new DialogueLine(kikiName, "Don't worry, you don't have to wait — it's ready! That's " + price + " coins."));
        lines.add(new DialogueLine(customerName, "Thank you! See you!"));
    } else {
        lines.add(new DialogueLine(kikiName, "You can make the " + potionName + " for that."));
        lines.add(new DialogueLine(customerName, "Ok, thanks, I'll wait for you!"));
    }

    return new DialogueImpl(lines);
}


   @Override
    public void confirmOrder(final Order order) {
        if (order.getStatus() == OrderStatus.READY) {
            completeOrder(order);
        } else {
            order.setStatus(OrderStatus.PENDING_CRAFT);
        }
    }

    
}