package it.unibo.KikiStore.item;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.KikiStore.model.item.impl.AbstractItemImpl;
import javafx.geometry.Rectangle2D;

/**
 * Unit tests for {@link AbstractItemImpl}.
 * Verifies getters, resource tracking, quantity modification, and hitbox generation.
 */
class ItemTest {

    private static final String ITEM_ID = "coin_01";
    private static final String ITEM_NAME = "Gold Coin";
    private static final double POS_X = 64.0;
    private static final double POS_Y = 128.0;
    private static final double WIDTH = 32.0;
    private static final double HEIGHT = 32.0;
    private static final boolean IS_ANIMATED = true;
    private static final int INITIAL_QUANTITY = 10;
    private static final double DELTA = 0.001;

    private AbstractItemImpl testItem;

    private static final class DummyItem extends AbstractItemImpl {
        DummyItem(
            final String id,
            final double x,
            final double y,
            final double width,
            final double height,
            final boolean animated,
            final String name,
            final int quantity
        ) {
            super(id, x, y, width, height, animated, name, quantity);
        }
    }

    @BeforeEach
    void setUp() {
        testItem = new DummyItem(
            ITEM_ID,
            POS_X,
            POS_Y,
            WIDTH,
            HEIGHT,
            IS_ANIMATED,
            ITEM_NAME,
            INITIAL_QUANTITY
        );
    }

    @Test
    void testInitialProperties() {
        assertEquals(ITEM_ID, testItem.getId());
        assertEquals(ITEM_NAME, testItem.getName());
        assertEquals(POS_X, testItem.getX(), DELTA);
        assertEquals(POS_Y, testItem.getY(), DELTA);
        assertEquals(WIDTH, testItem.getWidth(), DELTA);
        assertEquals(HEIGHT, testItem.getHeight(), DELTA);
        assertTrue(testItem.isAnimated());
        assertEquals(INITIAL_QUANTITY, testItem.getQuantity());
    }

    @Test
    void testQuantityUpdate() {
        final int updatedQuantity = 25;
        testItem.setQuantity(updatedQuantity);
        assertEquals(updatedQuantity, testItem.getQuantity());
    }

    @Test
    void testHitboxGeneration() {
        final Rectangle2D hitbox = testItem.getHitbox();

        assertNotNull(hitbox);
        assertEquals(POS_X, hitbox.getMinX(), DELTA);
        assertEquals(POS_Y, hitbox.getMinY(), DELTA);
        assertEquals(WIDTH, hitbox.getWidth(), DELTA);
        assertEquals(HEIGHT, hitbox.getHeight(), DELTA);
    }
}
