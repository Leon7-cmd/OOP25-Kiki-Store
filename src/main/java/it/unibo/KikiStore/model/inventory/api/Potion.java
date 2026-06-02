package it.unibo.KikiStore.model.inventory.api;
import it.unibo.KikiStore.model.item.api.GameItem;

public interface Potion extends GameItem {
    public String getDescription();
    public String getEffect();
    public boolean isBlack();
    public void setDescription(String description);
    public void setEffect(String effect);
    public void setBlack(boolean isBlack);
}