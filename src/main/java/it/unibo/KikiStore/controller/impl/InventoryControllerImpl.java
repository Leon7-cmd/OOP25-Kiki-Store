package it.unibo.KikiStore.controller.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import it.unibo.KikiStore.controller.api.InventoryController;
import it.unibo.KikiStore.model.inventory.impl.InventoryImpl;
import it.unibo.KikiStore.model.inventory.impl.IngredientImpl;
import it.unibo.KikiStore.model.inventory.impl.PotionImpl;
import it.unibo.KikiStore.model.item.api.GameItem;
import it.unibo.KikiStore.model.inventory.api.Ingredient;
import it.unibo.KikiStore.model.inventory.api.Inventory;
import it.unibo.KikiStore.model.inventory.api.Recipe;

/**
 * Manages the player's inventory - adding, removing, and querying
 * ingredients and potions, and checking recipe craftability.
 */
public final class InventoryControllerImpl implements InventoryController {
    private static final int MAX_CAPACITY = 50;
    private final Inventory inventory = new InventoryImpl();

    /**
     * Creates an empty inventory controller.
     */
    @Override
    public boolean isFull() {
        return inventory.getIngredients().size() + inventory.getPotions().size() >= MAX_CAPACITY;
    }

    /**
     * Searches an item by name, ignoring case, in the given list.
     *
     * @param <T>  the concrete item type (ingredient or potion)
     * @param name the name of the item to look for
     * @param list the list to search in
     * @return the matching item, or null if it is not present
     */
    private <T extends GameItem> T findItem(final String name, final List<T> list) {
        for (final T inventoryItem : list) {
            if (inventoryItem.getName().equalsIgnoreCase(name)) {
                return inventoryItem;
            }
        }
        return null;
    }

    /**
     * Increases the quantity of an item already in the given list, or adds a new
     * item if it is not present yet and the inventory still has room.
     *
     * @param <T>      the concrete item type (ingredient or potion)
     * @param name     the name of the item to look for
     * @param quantity the amount to add
     * @param items    the inventory list the item belongs to
     * @param newItem  creates the new item, called only if the item is not present
     * @param adder    adds the new item to the inventory
     */
    private <T extends GameItem> void increase(final String name, final int quantity,
            final List<T> items, final Supplier<T> newItem, final Consumer<T> adder) {
        final T existing = findItem(name, items);
        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + quantity);
        } else if (!isFull()) {
            adder.accept(newItem.get());
        }
    }

    /**
     * Decreases the quantity of an item in the given list, removing it once its
     * quantity reaches zero. Does nothing if the item is missing or there is not
     * enough of it.
     *
     * @param <T>      the concrete item type (ingredient or potion)
     * @param name     the name of the item to look for
     * @param quantity the amount to remove
     * @param items    the inventory list the item belongs to
     * @param remover  removes the item from the inventory
     */
    private <T extends GameItem> void decrease(final String name, final int quantity,
            final List<T> items, final Consumer<T> remover) {
        final T item = findItem(name, items);
        if (item != null && item.getQuantity() >= quantity) {
            final int newQuantity = item.getQuantity() - quantity;
            if (newQuantity == 0) {
                remover.accept(item);
            } else {
                item.setQuantity(newQuantity);
            }
        }
    }

    @Override
    public boolean hasIngredient(final String name) {
        return findItem(name, inventory.getIngredients()) != null;
    }

    @Override
    public boolean hasPotion(final String name) {
        return findItem(name, inventory.getPotions()) != null;
    }

    @Override
    public int getIngredientQuantity(final String name) {
        final GameItem item = findItem(name, inventory.getIngredients());
        return item != null ? item.getQuantity() : 0;
    }

    @Override
    public int getPotionQuantity(final String name) {
        final GameItem item = findItem(name, inventory.getPotions());
        return item != null ? item.getQuantity() : 0;
    }

    @Override
    public boolean hasEnoughIngredient(final String name, final int quantity) {
        return getIngredientQuantity(name) >= quantity;
    }

    @Override
    public boolean hasEnoughPotion(final String name, final int quantity) {
        return getPotionQuantity(name) >= quantity;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public void addIngredient(final String name, final String imagePath, final int quantity, final String type) {
        increase(name, quantity, inventory.getIngredients(),
                () -> new IngredientImpl(name, imagePath, quantity, type), inventory::addIngredient);
    }

    @Override
    public void addPotion(final String name, final String imagePath, final int quantity, final String description,
            final String effect, final boolean isBlack) {
        increase(name, quantity, inventory.getPotions(),
                () -> new PotionImpl(name, imagePath, quantity, description, effect, isBlack),
                inventory::addPotion);
    }

    @Override
    public void removeIngredient(final String name, final int quantity) {
        decrease(name, quantity, inventory.getIngredients(), inventory::removeIngredient);
    }

    @Override
    public void removePotion(final String name, final int quantity) {
        decrease(name, quantity, inventory.getPotions(), inventory::removePotion);
    }

    @Override
    public boolean canCraftPotion(final Recipe recipe) {
        for (final Ingredient ingredient : recipe.getIngredients()) {
            if (!hasEnoughIngredient(ingredient.getName(), ingredient.getQuantity())) {
                return false;
            }
        }
        return true;
        // or otherwise to implement DRY concept -> return
        // getMissingIngredients(recipe).isEmpty();
    }

    @Override
    public List<Ingredient> getMissingIngredients(final Recipe recipe) {
        final List<Ingredient> missing = new ArrayList<>();
        for (final Ingredient ingredient : recipe.getIngredients()) {
            if (!hasEnoughIngredient(ingredient.getName(), ingredient.getQuantity())) {
                missing.add(ingredient);
            }
        }
        return missing;
    }

}
