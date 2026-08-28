package it.unibo.KikiStore.model.inventory.impl;

import it.unibo.KikiStore.model.inventory.api.Ingredient;
import it.unibo.KikiStore.model.item.impl.GameItemImpl;


/**
 * Concrete ingredient item - extends the base game item with a type
 * (e.g. plant, flower, root) used to categorize it in the inventory.
 */
public final class IngredientImpl extends GameItemImpl implements Ingredient {
    // e.g. it could be a plant, flower, type of wood or a specific root
    private String type;
    private int price; // price of the ingredient(one unit) in the store, to be used for calculating the price of a potion based on its ingredients

    /**
     * @param name the ingredient name
     * @param imagePath the sprite path for the ingredient
     * @param quantity the starting quantity
     * @param type the ingredient type
     * @param price the price of the ingredient in the store
     */
    public IngredientImpl(final String name, final String imagePath, final int quantity, final String type, final int price) {
        super(name, imagePath, quantity);
        this.type = type;
        this.price = price;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void setType(final String type) {
        this.type = type;
    }

    @Override 
    public int getPrice() {
        return price;
    }

    @Override 
    public void setPrice( final int price) {
        this.price = price;  
    }
}
