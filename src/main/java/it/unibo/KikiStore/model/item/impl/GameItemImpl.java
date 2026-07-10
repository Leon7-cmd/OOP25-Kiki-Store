package it.unibo.KikiStore.model.item.impl;
import it.unibo.KikiStore.model.item.api.GameItem;

public abstract class GameItemImpl implements GameItem {
    private String name;
    private String imagePath;
    private int quantity;

    public GameItemImpl(String name, String imagePath, int quantity) {
        this.name = name;
        this.imagePath = imagePath;
        this.quantity = quantity;
    }

    @Override public String getName() {
        return name;
    }

    @Override public String getImagePath() {
        return imagePath;
    }

    @Override public int getQuantity() {
        return quantity;
    }


    @Override public void setName(String name) {
        this.name = name;
    }

    @Override public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

}
