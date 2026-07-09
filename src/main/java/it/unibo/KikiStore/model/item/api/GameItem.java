package  it.unibo.KikiStore.model.item.api;

public interface GameItem {
    public String getName();
    public String getImagePath();
    public int getQuantity();
    public int getPrice();
    public void setPrice(int price);
    public void setName(String name);
    public void setImagePath(String imagePath);
    public void setQuantity(int quantity);
}