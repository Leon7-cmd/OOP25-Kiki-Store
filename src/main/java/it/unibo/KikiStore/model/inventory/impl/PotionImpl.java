package it.unibo.KikiStore.model.inventory.impl;
import it.unibo.KikiStore.model.inventory.api.Potion;
import it.unibo.KikiStore.model.item.impl.GameItemImpl;

public class PotionImpl extends GameItemImpl implements Potion {
    private String description;
    private String effect; // effects provided by the potion, e.g. "help sleeping", "energizing", "help with digestion"
    private boolean isBlack; //TO BE CHANGED - it describes a wrong combination used to create a potion

    public PotionImpl (String name, String imagePath, int quantity, String description, String effect, boolean isBlack) {
        super(name, imagePath, quantity);
        this.description = description;
        this.effect = effect;
        this.isBlack = isBlack;
    }

    @Override public String getDescription() {
        return description;
    }

    @Override public String getEffect() {
        return effect;
    }

    @Override public boolean isBlack() {
        return isBlack;
    }

    @Override public void setDescription(String description) {
        this.description = description;
    }

    @Override public void setEffect(String effect) {
        this.effect = effect;
    }

    @Override public void setBlack(boolean isBlack) {
        this.isBlack = isBlack;
    }
}
