package it.unibo.kikistore.player;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.kikistore.controller.api.InputHandler;
import it.unibo.kikistore.controller.api.PlayerController;
import it.unibo.kikistore.controller.impl.PlayerControllerImpl;
import it.unibo.kikistore.model.player.api.Player;
import it.unibo.kikistore.model.player.impl.PlayerImpl;

/**
 * Unit tests for {@link PlayerImpl}.
 * Verifies initial state, coordinate setters, resource accumulation, and movement logic.
 */
class PlayerTest {

    private static final double START_X = 100.0;
    private static final double START_Y = 150.0;
    private static final double PLAYER_SPEED = 3.5;
    private static final double DELTA = 0.001;
    private static final int INITIAL_MONEY = 40;
    private static final int INITIAL_ENERGY = 5;

    private Player player;

    /**
     * Initializes a fresh Player instance before each test.
     */
    @BeforeEach
    void setUp() {
        this.player = new PlayerImpl(START_X, START_Y);
    }

    /**
     * Tests default attributes upon instantiation, ensuring orientation, idle state,
     * base resources, and spatial positioning match defaults.
     */
    @Test
    void testInitialState() {
        assertEquals(START_X, this.player.getX(), DELTA);
        assertEquals(START_Y, this.player.getY(), DELTA);
        assertEquals(INITIAL_MONEY, this.player.getMoney());
        assertEquals(INITIAL_ENERGY, this.player.getEnergy());
        assertEquals("down", this.player.getDirection());
        assertEquals("idle", this.player.getState());
    }

    /**
     * Verifies that manual coordinate updates mutate spatial coordinates correctly.
     */
    @Test
    void testPositionUpdate() {
        final double newX = 250.0;
        final double newY = 320.5;

        this.player.setX(newX);
        this.player.setY(newY);

        assertEquals(newX, this.player.getX(), DELTA);
        assertEquals(newY, this.player.getY(), DELTA);
    }

    /**
     * Tests currency accumulation, energy depletion, restoration, and boundary caps.
     */
    @Test
    void testResourcesModification() {
        final int addedMoney = 120;
        final int energyToConsume = 3;
        final int energyToRestore = 2;
        final int excessiveEnergy = 10;

        // Verify currency accumulation
        this.player.addMoney(addedMoney);
        assertEquals(INITIAL_MONEY + addedMoney, this.player.getMoney());

        // Verify energy consumption (5 - 3 = 2)
        this.player.consumeEnergy(energyToConsume);
        assertEquals(INITIAL_ENERGY - energyToConsume, this.player.getEnergy());

        // Verify partial energy restoration (2 + 2 = 4)
        this.player.restoreEnergy(energyToRestore);
        assertEquals(INITIAL_ENERGY - energyToConsume + energyToRestore, this.player.getEnergy());

        // Verify ceiling clamping against MAX_ENERGY
        this.player.restoreEnergy(excessiveEnergy);
        assertEquals(INITIAL_ENERGY, this.player.getEnergy());
    }

    /**
     * Verifies player behavior under directional input in the absence of a CollisionHandler.
     * Ensures unrestricted spatial displacement alongside correct orientation and state updates.
     */
    @Test
    void testMovementWithoutCollisionHandler() {
        final InputHandler movingInput = new RightMovingInputHandler();

        // Bind controller to player model and advance one input frame
        final PlayerController controller = new PlayerControllerImpl(this.player, movingInput);
        controller.update();

        // Without collision constraints, player advances freely by its base speed
        assertEquals(START_X + PLAYER_SPEED, this.player.getX(), DELTA);
        assertEquals(START_Y, this.player.getY(), DELTA);
        assertEquals("right", this.player.getDirection());
        assertEquals("walk", this.player.getState());
    }

    /**
     * Test stub providing simulated rightward directional input.
     */
    private static final class RightMovingInputHandler implements InputHandler {

        @Override
        public boolean isUp() {
            return false;
        }

        @Override
        public boolean isDown() {
            return false;
        }

        @Override
        public boolean isLeft() {
            return false;
        }

        @Override
        public boolean isRight() {
            return true;
        }

        @Override
        public boolean isAction() {
            return false;
        }

        @Override
        public boolean isEscapePressed() {
            return false;
        }

        @Override
        public boolean isInventoryPressed() {
            return false;
        }

        @Override
        public boolean isCraftingPressed() {
            return false;
        }

        @Override
        public boolean isMouseClicked() {
            return false;
        }

        @Override
        public double getMouseX() {
            return 0.0;
        }

        @Override
        public double getMouseY() {
            return 0.0;
        }
    }
}
