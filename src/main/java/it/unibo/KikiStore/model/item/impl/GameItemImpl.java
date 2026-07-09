package it.unibo.KikiStore.model.item.impl;
import it.unibo.KikiStore.model.item.api.GameItem;

public abstract class GameItemImpl implements GameItem {
    private String name;
    private String imagePath;
    private int quantity;
    private int price;

    public GameItemImpl(String name, String imagePath, int quantity, int price) {
        this.name = name;
        this.imagePath = imagePath;
        this.quantity = quantity;
        this.price = price;
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

    @Override public int getPrice() {
        return price;
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

    @Override public void setPrice(final int price) {
        this.price = price;
    }
}
