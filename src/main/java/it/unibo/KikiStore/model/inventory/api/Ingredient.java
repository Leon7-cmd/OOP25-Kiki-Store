package it.unibo.KikiStore.model.inventory.api;
import it.unibo.KikiStore.model.item.api.Item;

public interface Ingredient extends Item {
    public String getType();
    public void setType(String type);
}