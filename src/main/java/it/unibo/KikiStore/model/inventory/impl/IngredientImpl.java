package it.unibo.KikiStore.model.inventory.impl;
import it.unibo.KikiStore.model.inventory.api.Ingredient;
import it.unibo.KikiStore.model.item.impl.GameItemImpl;


public class IngredientImpl extends GameItemImpl implements Ingredient {
    private String type; // e.g. it could be a plant, flower, type of wood or a specific root
    private final int price; // price of the ingredient(one unit) in the store, to be used for calculating the price of a potion based on its ingredients
    public IngredientImpl (String name, String imagePath, int quantity, String type, int price) {
        super(name, imagePath, quantity);
        this.type = type;
        this.price = price;
    }

    @Override public String getType() {
        return type;
    }

    @Override public void setType(String type) {
        this.type = type;
    }

    @Override public int getPrice() {
        return price;
    }
}
