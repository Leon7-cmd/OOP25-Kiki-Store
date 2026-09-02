package it.unibo.KikiStore.model.item.impl;

import java.util.Objects;

import it.unibo.KikiStore.model.inventory.api.Ingredient;

/**
 * Entity representing an ingredient placed in the game world.
 */
public final class GroundItem extends AbstractItemImpl {

    private final Ingredient ingredient;

    /**
     * Constructs a ground item in the world.
     *
     * @param id         The asset ID used by SpriteManager.
     * @param x          World X coordinate in pixels.
     * @param y          World Y coordinate in pixels.
     * @param width      Hitbox and render width in pixels.
     * @param height     Hitbox and render height in pixels.
     * @param animated   True if the sprite uses frame-based animation.
     * @param ingredient The logical ingredient instance attached to this entity.
     */
    public GroundItem(
        final String id,
        final double x,
        final double y,
        final double width,
        final double height,
        final boolean animated,
        final Ingredient ingredient
    ) {
        super(
            id,
            x,
            y,
            width,
            height,
            animated,
            Objects.requireNonNull(ingredient, "Ingredient payload cannot be null").getName(),
            ingredient.getQuantity()
        );
        this.ingredient = ingredient;
    }

    /**
     * Retrieves the ingredient to add to the inventory.
     *
     * @return the encapsulated {@link Ingredient}.
     */
    public Ingredient getIngredient() {
        return ingredient;
    }
}
