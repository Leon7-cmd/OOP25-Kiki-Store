package it.unibo.kikiStore.controller.impl;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import it.unibo.kikiStore.controller.api.InventoryController;
import it.unibo.kikiStore.model.inventory.impl.InventoryImpl;
import it.unibo.kikiStore.model.inventory.impl.IngredientImpl;
import it.unibo.kikiStore.model.inventory.impl.PotionImpl;
import it.unibo.kikiStore.model.item.api.GameItem;
import it.unibo.kikiStore.model.inventory.api.Ingredient;
import it.unibo.kikiStore.model.inventory.api.Inventory;
import it.unibo.kikiStore.model.inventory.api.Recipe;

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
     * @return the matching item, or an empty Optional if it is not present
     */
    private <T extends GameItem> Optional<T> findItem(final String name, final List<T> list) {
        for (final T inventoryItem : list) {
            if (inventoryItem.getName().equalsIgnoreCase(name)) {
                return Optional.of(inventoryItem);
            }
        }
        return Optional.empty();
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
        final Optional<T> existing = findItem(name, items);
        if (existing.isPresent()) {
            existing.get().setQuantity(existing.get().getQuantity() + quantity);
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
        final Optional<T> found = findItem(name, items);
        if (found.isPresent() && found.get().getQuantity() >= quantity) {
            final T item = found.get();
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
        return findItem(name, inventory.getIngredients()).isPresent();
    }

    @Override
    public boolean hasPotion(final String name) {
        return findItem(name, inventory.getPotions()).isPresent();
    }

    @Override
    public int getIngredientQuantity(final String name) {
        return findItem(name, inventory.getIngredients()).map(GameItem::getQuantity).orElse(0);
    }

    @Override
    public int getPotionQuantity(final String name) {
        return findItem(name, inventory.getPotions()).map(GameItem::getQuantity).orElse(0);
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
        return getMissingIngredients(recipe).isEmpty();
    }

    @Override
    public List<Ingredient> getMissingIngredients(final Recipe recipe) {
        return recipe.getIngredients().stream()
                .filter(i -> !hasEnoughIngredient(i.getName(), i.getQuantity()))
                .toList();
    }

}
