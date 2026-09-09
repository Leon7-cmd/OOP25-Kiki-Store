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

/**
 * Controller implementation for handling orders lifecycle, pricing,
 * dialog generation, and player state synchronization.
 */
public final class OrderControllerImpl implements OrderController {

    private final OrderBook orderBook;
    private final RecipeBook recipeBook;
    private final Inventory inventory;
    private final Player player;
    private final PotionPriceCalculator priceCalculator;

    /**
     * @param orderBook the shared order repository
     * @param recipeBook the recipe book repository
     * @param inventory the player's inventory
     * @param player the player model
     * @param priceCalculator the pricing logic
     */
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
    public Recipe getRecipeForOrder(final Order order) {
        return resolveRecipe(order.getRequest());
    }

    private Recipe resolveRecipe(final CustomerRequest request) {
        // 1. Caso NeedRequest: cerca la pozione per effetto
        if (request instanceof NeedRequest needRequest) {
            final Need need = needRequest.getNeed();
            if (need == null || need.getName() == null) {
                return null;
            }

            final List<Recipe> matches = recipeBook.findByEffect(need.getName());
            if (matches != null && !matches.isEmpty()) {
                return matches.get(0);
            }

            for (final Recipe r : recipeBook.getRecipes()) {
                if (r.getPotion() != null && r.getPotion().getEffect() != null
                        && r.getPotion().getEffect().equalsIgnoreCase(need.getName())) {
                    return r;
                }
            }
            return null;
        }

        // 2. Caso IngredientRequest: cliente porta un ingrediente, cerchiamo la pozione che lo contiene
        if (request instanceof IngredientRequest ingredientRequest) {
            final Ingredient brought = ingredientRequest.getIngredient();
            if (brought == null || brought.getName() == null) {
                return null;
            }

            // Prima controlla tra le ricette sbloccate
            for (final Recipe r : recipeBook.getUnlockedRecipes()) {
                if (recipeContainsIngredient(r, brought.getName())) {
                    return r;
                }
            }

            // Se non ne trova tra le sbloccate, controlla tra tutte le ricette note
            for (final Recipe r : recipeBook.getRecipes()) {
                if (recipeContainsIngredient(r, brought.getName())) {
                    return r;
                }
            }
        }

        return null;
    }

    private boolean recipeContainsIngredient(final Recipe recipe, final String ingredientName) {
        if (recipe == null || recipe.getIngredients() == null) {
            return false;
        }
        for (final Ingredient ing : recipe.getIngredients()) {
            if (ing != null && ing.getName() != null && ing.getName().equalsIgnoreCase(ingredientName)) {
                return true;
            }
        }
        return false;
    }

    private Potion findPotion(final String name) {
        if (name == null) {
            return null;
        }
        for (final Potion p : inventory.getPotions()) {
            if (p != null && name.equalsIgnoreCase(p.getName()) && p.getQuantity() > 0) {
                return p;
            }
        }
        return null;
    }

    @Override
    public int getPriceForOrder(final Order order) {
        final Recipe recipe = resolveRecipe(order.getRequest());
        if (recipe == null) {
            return 5;
        }
        final int calculated = priceCalculator.calculatePrice(recipe, order.getRequest());
        return Math.max(calculated, 1);
    }

    @Override
    public boolean isOrderReady(final Order order) {
        final Recipe recipe = resolveRecipe(order.getRequest());
        if (recipe == null) {
            return false;
        }
        return findPotion(recipe.getPotion().getName()) != null;
    }

    @Override
    public boolean completeOrder(final Order order) {
        if (!isOrderReady(order)) {
            return false;
        }

        final Recipe recipe = resolveRecipe(order.getRequest());
        final Potion potion = findPotion(recipe.getPotion().getName());
        if (potion != null) {
            if (potion.getQuantity() > 1) {
                potion.setQuantity(potion.getQuantity() - 1);
            } else {
                inventory.removePotion(potion);
            }
        }

        // Accredito monete al giocatore
        final int price = getPriceForOrder(order);
        player.setMoney(player.getMoney() + price);

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

        lines.add(new DialogueLine(customerName, order.getRequest().getDialogue()));

        // Caso 1: L'ordine è pronto (pozione in inventario)
        if (isOrderReady(order)) {
            final int price = getPriceForOrder(order);
            lines.add(new DialogueLine(kikiName, "Don't worry, you don't have to wait — it's ready! That's " + price + " coins."));
            lines.add(new DialogueLine(customerName, "Thank you so much! See you soon!"));
            return new DialogueImpl(lines);
        }

        // Caso 2: Ricetta bloccata
        if (recipe != null && !recipe.isUnlocked()) {
            lines.add(new DialogueLine(kikiName, "It is locked, I don't have that potion!"));
            lines.add(new DialogueLine(customerName, "Oh, what a pity... I'll come back another time!"));
            return new DialogueImpl(lines);
        }

        // Caso 3: Ricetta sbloccata ma da craftare
        final String potionName = recipe != null ? recipe.getPotion().getName() : "it";
        lines.add(new DialogueLine(kikiName, "I can brew a " + potionName + " for you. Please give me some time!"));
        lines.add(new DialogueLine(customerName, "Ok, thanks, I'll wait for you!"));

        return new DialogueImpl(lines);
    }

    @Override
    public void confirmOrder(final Order order) {
        final Recipe recipe = resolveRecipe(order.getRequest());

        // Se la ricetta è bloccata: l'ordine viene rifiutato e rimosso dalla lista
        if (recipe != null && !recipe.isUnlocked()) {
            order.setStatus(OrderStatus.DELIVERED);
            orderBook.removeOrder(order);
            return;
        }

        // Se l'oggetto è già pronto, completa e incassa
        if (isOrderReady(order)) {
            completeOrder(order);
        } else {
            order.setStatus(OrderStatus.PENDING_CRAFT);
        }
    }
}