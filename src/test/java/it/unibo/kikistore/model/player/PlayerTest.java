package it.unibo.kikistore.model.player;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.kikistore.model.player.api.Player;
import it.unibo.kikistore.model.player.impl.PlayerImpl;

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
        this.player = new PlayerImpl(START_X, START_Y);
    }

    /**
     * Tests default attributes upon instantiation.
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
     * Tests currency add, energy remove, restoration, and max capacity.
     */
    @Test
    void testResourcesModification() {
        final int addedMoney = 120;
        final int energyToConsume = 3;
        final int energyToRestore = 2;
        final int excessiveEnergy = 10;

        // Verify currency add
        this.player.addMoney(addedMoney);
        assertEquals(INITIAL_MONEY + addedMoney, this.player.getMoney());

        // Verify energy removal (5 - 3 = 2)
        this.player.consumeEnergy(energyToConsume);
        assertEquals(INITIAL_ENERGY - energyToConsume, this.player.getEnergy());

        // Verify energy add (2 + 2 = 4)
        this.player.restoreEnergy(energyToRestore);
        assertEquals(INITIAL_ENERGY - energyToConsume + energyToRestore, this.player.getEnergy());

        // Verify energy not exceding max capacity
        this.player.restoreEnergy(excessiveEnergy);
        assertEquals(INITIAL_ENERGY, this.player.getEnergy());
    }
}
