package it.unibo.kikiStore.item;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.kikiStore.model.inventory.api.Ingredient;
import it.unibo.kikiStore.model.inventory.impl.IngredientImpl;
import it.unibo.kikiStore.model.item.impl.GroundItemImpl;
import it.unibo.kikiStore.model.utility.BoundingBox;

public class GroundItemTest {

    private static final String ID = "sprites/ingredients/aloe";
    private static final String NAME = "Aloe";
    private static final String TYPE = "plant";
    private static final double X = 64.0;
    private static final double Y = 128.0;
    private static final double SIZE = 32.0;

    private Ingredient ingredient;
    private GroundItemImpl groundItem;

    @BeforeEach
    public void setUp() {
        this.ingredient = new IngredientImpl(NAME, ID, 2, TYPE);
        this.groundItem = new GroundItemImpl(ID, X, Y, SIZE, SIZE, false, ingredient);
    }

    @Test
    public void testGroundItemAttributes() {
        assertEquals(ID, groundItem.getId());
        assertEquals(X, groundItem.getX());
        assertEquals(Y, groundItem.getY());
        assertEquals(SIZE, groundItem.getWidth());
        assertEquals(SIZE, groundItem.getHeight());
        assertFalse(groundItem.isAnimated());
        assertEquals(NAME, groundItem.getItem().getName());
        assertEquals(2, groundItem.getItem().getQuantity());
    }

    @Test
    public void testGroundItemHitbox() {
        final BoundingBox hitbox = groundItem.getHitbox();
        assertNotNull(hitbox);
        assertEquals(X, hitbox.x());
        assertEquals(Y, hitbox.y());
        assertEquals(SIZE, hitbox.width());
        assertEquals(SIZE, hitbox.height());
    }

    @Test
    public void testPayloadRetrieval() {
        final Ingredient payload = groundItem.getItem();
        assertNotNull(payload);
        assertEquals(NAME, payload.getName());
        assertEquals(TYPE, payload.getType());
        assertEquals(ID, payload.getImagePath());
        assertEquals(2, payload.getQuantity());
    }
}
