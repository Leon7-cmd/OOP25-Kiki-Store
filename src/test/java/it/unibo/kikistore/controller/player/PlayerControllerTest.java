package it.unibo.kikistore.controller.player;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.kikistore.controller.api.InputHandler;
import it.unibo.kikistore.controller.api.PlayerController;
import it.unibo.kikistore.controller.impl.PlayerControllerImpl;
import it.unibo.kikistore.model.player.api.Player;
import it.unibo.kikistore.model.player.impl.PlayerImpl;
import it.unibo.kikistore.view.entity.api.EntityRenderData;

/**
 * Unit tests for {@link PlayerControllerImpl}.
 * Verifies input translation, model delegation, and render data generation.
 */
class PlayerControllerTest {

    private static final double START_X = 100.0;
    private static final double START_Y = 150.0;
    private static final double DELTA = 0.001;
    private static final double PLAYER_SPEED = 3.5;
    private static final double EXPECTED_RENDER_SIZE = 64.0;
    private static final String EXPECTED_SPRITE_PATH = "sprites/player/kiki";

    private Player player;
    private MockInputHandler input;
    private PlayerController controller;

    @BeforeEach
    void setUp() {
        this.player = new PlayerImpl(START_X, START_Y);
        this.input = new MockInputHandler();
        this.controller = new PlayerControllerImpl(this.player, this.input);
    }

    @Test
    void testConstructorNullGuards() {
        assertThrows(NullPointerException.class, () -> new PlayerControllerImpl(null, this.input));
        assertThrows(NullPointerException.class, () -> new PlayerControllerImpl(this.player, null));
    }

    @Test
    void testMovementOnRightInput() {
        this.input.setRight(true);
        this.controller.update(null);

        assertEquals("right", this.controller.getDirection());
        assertEquals("walk", this.controller.getState());
        assertEquals(START_X + PLAYER_SPEED, this.controller.getX(), DELTA);
        assertEquals(START_Y, this.controller.getY(), DELTA);
    }

    @Test
    void testDiagonalMovementNormalization() {
        this.input.setRight(true);
        this.input.setDown(true);

        this.controller.update(null);

        final double normalizedComponent = 1.0 / Math.sqrt(2.0);
        final double expectedStep = normalizedComponent * PLAYER_SPEED;

        assertEquals(START_X + expectedStep, this.controller.getX(), DELTA);
        assertEquals(START_Y + expectedStep, this.controller.getY(), DELTA);
        assertEquals("walk", this.controller.getState());
    }

    @Test
    void testIdleStateWhenNoInput() {
        this.controller.update(null);

        assertEquals("idle", this.controller.getState());
        assertEquals(START_X, this.controller.getX(), DELTA);
        assertEquals(START_Y, this.controller.getY(), DELTA);
    }

    @Test
    void testSetPositionDelegation() {
        final double targetX = 500.0;
        final double targetY = 700.0;

        this.controller.setPosition(targetX, targetY);

        assertEquals(targetX, this.controller.getX(), DELTA);
        assertEquals(targetY, this.controller.getY(), DELTA);
    }

    @Test
    void testToRenderDataConversion() {
        final EntityRenderData data = this.controller.toRenderData();

        assertNotNull(data);
        assertEquals(START_X, data.x(), DELTA);
        assertEquals(START_Y, data.y(), DELTA);
        assertEquals(EXPECTED_RENDER_SIZE, data.width(), DELTA);
        assertEquals(EXPECTED_RENDER_SIZE, data.height(), DELTA);
        assertEquals(EXPECTED_SPRITE_PATH, data.entityId());
        assertEquals("down", data.direction());
        assertEquals("idle", data.state());
    }

    /**
     * Configurable input stub for controller testing.
     */
    private static final class MockInputHandler implements InputHandler {
        private boolean right;
        private boolean down;

        void setRight(final boolean right) {
            this.right = right;
        }

        void setDown(final boolean down) {
            this.down = down;
        }

        @Override public boolean isUp() {
            return false;
        }

        @Override public boolean isDown() {
            return this.down;
        }

        @Override public boolean isLeft() {
            return false;
        }

        @Override public boolean isRight() {
            return this.right;
        }

        @Override public boolean isAction() {
            return false;
        }

        @Override public boolean isEscapePressed() {
            return false;
        }

        @Override public boolean isInventoryPressed() {
            return false;
        }

        @Override public boolean isCraftingPressed() {
            return false;
        }

        @Override public boolean isMouseClicked() {
            return false;
        }

        @Override public double getMouseX() {
            return 0.0;
        }

        @Override public double getMouseY() {
            return 0.0;
        }
    }
}
