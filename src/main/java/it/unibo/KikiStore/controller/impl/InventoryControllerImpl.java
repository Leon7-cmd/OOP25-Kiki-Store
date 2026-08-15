package it.unibo.KikiStore.controller.impl;

import java.util.ArrayList;
import java.util.List;

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
    public InventoryControllerImpl() {
    }

    @Override
    public boolean isFull() {
        return (inventory.getIngredients().size() + inventory.getPotions().size()) == MAX_CAPACITY;
    }

    /**
     * Finds an item by name within the given list, case-insensitively.
     * TO-DO: replace with a hashmap-based lookup for better performance.
     *
     * @param name the item name to search for
     * @param list the list to search in (ingredients or potions)
     * @return the matching item, or null if not found
     */
    private GameItem findItem(final String name, final List<? extends GameItem> list) {
        for (final GameItem inventoryItem : list) {
            if (inventoryItem.getName().equalsIgnoreCase(name)) {
                return inventoryItem;
            }
        }
        return null;
        // return inventory.getIngredients().contains(ingredient); ----alternative
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
    public boolean hasEnoughIngredient(final String name, final int quantity) { // da modificare
        return getIngredientQuantity(name) >= quantity;
    }

    @Override
    public boolean hasEnoughPotion(final String name, final int quantity) { // da modificare
        return getPotionQuantity(name) >= quantity;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public void addIngredient(final String name, final String imagePath, final int quantity, final String type) {
        if (isFull()) {
            System.out.println("Cannot add " + name + ", inventory is full");
            return;
        }
        final GameItem item = findItem(name, inventory.getIngredients());

        if (item != null) {
            item.setQuantity(item.getQuantity() + quantity);
            return;
        }
        inventory.addIngredient(new IngredientImpl(name, imagePath, quantity, type));
    }

    @Override
    public void addPotion(final String name, final String imagePath, final int quantity, final String description,
            final String effect, final boolean isBlack) {
        if (isFull()) {
            System.out.println("Cannot add " + name + ", inventory is full");
            return;
        }
        final GameItem item = findItem(name, inventory.getPotions());

        if (item != null) {
            item.setQuantity(item.getQuantity() + quantity);
            return;
        }
        inventory.addPotion(new PotionImpl(name, imagePath, quantity, description, effect, isBlack));
    }

    @Override
    public void removeIngredient(final String name, final int quantity) {
        if (hasEnoughIngredient(name, quantity)) {
            final GameItem item = findItem(name, inventory.getIngredients());

            if (item != null) {
                item.setQuantity(item.getQuantity() - quantity);
                return;
            }
        }
    }

    @Override
    public void removePotion(final String name, final int quantity) {
        if (hasEnoughPotion(name, quantity)) {
            final GameItem item = findItem(name, inventory.getPotions());

            if (item != null) {
                item.setQuantity(item.getQuantity() - quantity);
                return;
            }
        }
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
