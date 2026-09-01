package it.unibo.KikiStore.player;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.KikiStore.controller.api.InputHandler;
import it.unibo.KikiStore.model.player.impl.PlayerImpl;

/**
 * Unit tests for {@link PlayerImpl}.
 * Verifies initial state, coordinate setters, resources, and movement.
 */
class PlayerTest {

    private static final double START_X = 100.0;
    private static final double START_Y = 150.0;
    private static final double DELTA = 0.001;
    private static final int INITIAL_MONEY = 40;
    private static final int INITIAL_ENERGY = 5;

    private PlayerImpl player;

    @BeforeEach
    void setUp() {
        player = new PlayerImpl(START_X, START_Y);
    }

    @Test
    void testInitialState() {
        assertEquals(START_X, player.getX(), DELTA);
        assertEquals(START_Y, player.getY(), DELTA);
        assertEquals(INITIAL_MONEY, player.getMoney());
        assertEquals(INITIAL_ENERGY, player.getEnergy());
        assertEquals("down", player.getDirection());
        assertEquals("idle", player.getState());
    }

    @Test
    void testPositionUpdate() {
        final double newX = 250.0;
        final double newY = 320.5;

        player.setX(newX);
        player.setY(newY);

        assertEquals(newX, player.getX(), DELTA);
        assertEquals(newY, player.getY(), DELTA);
    }

    @Test
    void testResourcesModification() {
        final int updatedMoney = 120;
        final int updatedEnergy = 3;

        player.setMoney(updatedMoney);
        player.setEnergy(updatedEnergy);

        assertEquals(updatedMoney, player.getMoney());
        assertEquals(updatedEnergy, player.getEnergy());
    }

    @Test
    void testMovementWithoutCollisionHandler() {
        // Faking minimal input
        final InputHandler movingInput = new InputHandler() {
            @Override public boolean isUp() { return false; }
            @Override public boolean isDown() { return false; }
            @Override public boolean isLeft() { return false; }
            @Override public boolean isRight() { return true; }
            @Override public boolean isAction() { return false; }
        };

        player.update(movingInput);

        // Movement should not apply if collisionHandler is null, state changes to walk
        assertEquals(START_X, player.getX(), DELTA);
        assertEquals("right", player.getDirection());
        assertEquals("walk", player.getState());
    }
}