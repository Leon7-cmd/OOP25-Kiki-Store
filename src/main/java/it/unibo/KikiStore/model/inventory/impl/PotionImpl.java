package it.unibo.KikiStore.model.inventory.impl;

import it.unibo.KikiStore.model.inventory.api.Potion;
import it.unibo.KikiStore.model.item.impl.GameItemImpl;

/**
 * Concrete potion item - extends the base game item with a
 * description, an effect, and a flag marking failed (black) potions.
 */
public final class PotionImpl extends GameItemImpl implements Potion {
    private String description;
    private String effect;
    // effects provided by the potion, e.g. "help sleeping", "energizing", "help
    // with digestion"
    private boolean isBlack;
    // TO BE CHANGED - it describes a wrong combination used to create a potion

    /**
     * @param name the potion name
     * @param imagePath the sprite path for the potion
     * @param quantity the starting quantity
     * @param description the potion description
     * @param effect the potion effect
     * @param isBlack whether this is a failed/black potion
     */
    public PotionImpl(final String name, final String imagePath, final int quantity, final String description,
            final String effect, final boolean isBlack) {
        super(name, imagePath, quantity);
        this.description = description;
        this.effect = effect;
        this.isBlack = isBlack;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getEffect() {
        return effect;
    }

    @Override
    public boolean isBlack() {
        return isBlack;
    }

    @Override
    public void setDescription(final String description) {
        this.description = description;
    }

    @Override
    public void setEffect(final String effect) {
        this.effect = effect;
    }

    @Override
    public void setBlack(final boolean black) {
        this.isBlack = black;
    }
}
