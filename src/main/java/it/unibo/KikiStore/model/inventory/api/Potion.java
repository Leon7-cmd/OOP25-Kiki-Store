package it.unibo.KikiStore.model.inventory.api;

public interface Potion extends Item {
    public String getDescription();
    public String getEffect();
    public boolean isBlack();
    public void setDescription(String description);
    public void setEffect(String effect);
    public void setBlack(boolean isBlack);
}