package it.unibo.KikiStore.model.inventory.impl;
import it.unibo.KikiStore.model.inventory.api.Ingredient;
import it.unibo.KikiStore.model.item.impl.ItemImpl;


public class IngredientImpl extends ItemImpl implements Ingredient {
    private String type; // e.g. it could be a plant, flower, type of wood or a specific root

    public IngredientImpl (String id, double x, double y, double width, double height, boolean animated, String name, int quantity, String type) {
        super(id, x, y, width, height, animated, name, quantity);
        this.type = type;
    }

    @Override public String getType() {
        return type;
    }

    @Override public void setType(String type) {
        this.type = type;
    }
}
