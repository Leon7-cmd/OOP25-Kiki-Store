package it.unibo.KikiStore.player;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.KikiStore.controller.api.InputHandler;
import it.unibo.KikiStore.controller.api.PlayerController;
import it.unibo.KikiStore.controller.impl.PlayerControllerImpl;
import it.unibo.KikiStore.model.player.api.Player;
import it.unibo.KikiStore.model.player.impl.PlayerImpl;

/**
 * Unit tests for {@link PlayerImpl}.
 * Verifies initial state, coordinate setters, resource accumulation, and movement logic.
 */
class PlayerTest {

    private static final double START_X = 100.0;
    private static final double START_Y = 150.0;
    private static final double DELTA = 0.001;
    private static final int INITIAL_MONEY = 40;
    private static final int INITIAL_ENERGY = 5;

    private Player player;

    /**
     * Initializes a fresh Player instance before each test.
     */
    @BeforeEach
    void setUp() {
        player = new PlayerImpl(START_X, START_Y);
    }

    /**
     * Tests default attributes upon instantiation, ensuring orientation, idle state,
     * base resources, and spatial positioning match defaults.
     */
    @Test
    void testInitialState() {
        assertEquals(START_X, player.getX(), DELTA);
        assertEquals(START_Y, player.getY(), DELTA);
        assertEquals(INITIAL_MONEY, player.getMoney());
        assertEquals(INITIAL_ENERGY, player.getEnergy());
        assertEquals("down", player.getDirection());
        assertEquals("idle", player.getState());
    }

    /**
     * Verifies that manual coordinate updates mutate spatial coordinates correctly.
     */
    @Test
    void testPositionUpdate() {
        final double newX = 250.0;
        final double newY = 320.5;

        player.setX(newX);
        player.setY(newY);

        assertEquals(newX, player.getX(), DELTA);
        assertEquals(newY, player.getY(), DELTA);
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
        player.addMoney(addedMoney);
        assertEquals(INITIAL_MONEY + addedMoney, player.getMoney());

        // Verify energy consumption (5 - 3 = 2)
        player.consumeEnergy(energyToConsume);
        assertEquals(INITIAL_ENERGY - energyToConsume, player.getEnergy());

        // Verify partial energy restoration (2 + 2 = 4)
        player.restoreEnergy(energyToRestore);
        assertEquals(INITIAL_ENERGY - energyToConsume + energyToRestore, player.getEnergy());

        // Verify ceiling clamping against MAX_ENERGY
        player.restoreEnergy(excessiveEnergy);
        assertEquals(INITIAL_ENERGY, player.getEnergy());
    }

    /**
     * Verifies player behavior under directional input in the absence of a CollisionHandler.
     * Ensures unrestricted spatial displacement alongside correct orientation and state updates.
     */
    @Test
    void testMovementWithoutCollisionHandler() {
        final double speed = 3.5;

        // Stub providing simulated rightward directional input
        final InputHandler movingInput = new InputHandler() {
            @Override public boolean isUp() { return false; }
            @Override public boolean isDown() { return false; }
            @Override public boolean isLeft() { return false; }
            @Override public boolean isRight() { return true; }
            @Override public boolean isAction() { return false; }
            @Override public void resetAction() { }
        };

        // Bind controller to player model and advance one input frame
        final PlayerController controller = new PlayerControllerImpl(player, movingInput);
        controller.update();

        // Without collision constraints, player advances freely by its base speed
        assertEquals(START_X + speed, player.getX(), DELTA);
        assertEquals(START_Y, player.getY(), DELTA);
        assertEquals("right", player.getDirection());
        assertEquals("walk", player.getState());
    }
}
