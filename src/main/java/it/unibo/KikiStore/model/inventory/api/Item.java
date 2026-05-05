package  it.unibo.KikiStore.model.inventory.api;

public interface Item {
    public String getName();
    public String getImagePath();
    public int getQuantity();
    public void setName(String name);
    public void setImagePath(String imagePath);
    public void setQuantity(int quantity);
}