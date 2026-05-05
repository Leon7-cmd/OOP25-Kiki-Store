package it.unibo.KikiStore.model.inventory.api;

public interface Ingredient extends Item {
    public String getType();
    public void setType(String type);
}