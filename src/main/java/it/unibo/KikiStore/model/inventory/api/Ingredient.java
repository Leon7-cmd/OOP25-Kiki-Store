package it.unibo.KikiStore.model.inventory.api;
import it.unibo.KikiStore.model.item.api.GameItem;

public interface Ingredient extends GameItem {
    public String getType();
    public void setType(String type);
}