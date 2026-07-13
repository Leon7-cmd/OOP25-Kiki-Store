package it.unibo.KikiStore.model.inventory.api;
import it.unibo.KikiStore.model.item.api.GameItem;
import it.unibo.KikiStore.model.item.api.PriceableItem;

public interface Ingredient extends GameItem, PriceableItem {
    public String getType();
    public void setType(String type);
}