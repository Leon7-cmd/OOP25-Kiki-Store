package it.unibo.KikiStore.model.inventory.impl;
import it.unibo.KikiStore.model.inventory.api.Ingredient;
import it.unibo.KikiStore.model.item.impl.GameItemImpl;


public class IngredientImpl extends GameItemImpl implements Ingredient {
    private String type; // e.g. it could be a plant, flower, type of wood or a specific root

    public IngredientImpl (String name, String imagePath, int quantity, String type) {
        super(name, imagePath, quantity);
        this.type = type;
    }

    @Override public String getType() {
        return type;
    }

    @Override public void setType(String type) {
        this.type = type;
    }
}
