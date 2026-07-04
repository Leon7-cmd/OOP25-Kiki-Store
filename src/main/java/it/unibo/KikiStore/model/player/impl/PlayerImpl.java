package it.unibo.KikiStore.model.player.impl;

import it.unibo.KikiStore.controller.api.InputHandler;
import it.unibo.KikiStore.model.map.impl.CollisionHandler;
import it.unibo.KikiStore.model.player.api.Player;

/**
 * Implementation of Player.
 */
public final class PlayerImpl implements Player {
    private static final double SPEED = 3.5;
    private static final int BASE_ENERGY = 5;
    private static final int BASE_MONEY = 40;

    // Hitbox dimensions: Defines the physical size of the player for collisions
    private static final double HITBOX_WIDTH = 32; 
    private static final double HITBOX_HEIGHT = 32; 

    // Hitbox offsets: Positions the hitbox relative to the top-left corner of the sprite
    private static final double HITBOX_OFFSET_X = 12; 
    private static final double HITBOX_OFFSET_Y = 44; 

    private static int money;
    private static int energy;
    private double x;
    private double y;
    private String direction = "down"; 
    private String state = "idle";
    private CollisionHandler collisionHandler;

    /**
     * Initializes the player at a specific starting position.
     * 
     * @param startX Initial horizontal position.
     * @param startY Initial vertical position.
     */
    public PlayerImpl(final double startX, final double startY) {
        this.x = startX;
        this.y = startY;
        money = BASE_MONEY;
        energy = BASE_ENERGY;
    }

    /**
     * Injects the collision handler needed to validate movement against the map.
     * 
     * @param handler The collision logic engine.
     */
    public void setCollisionHandler(final CollisionHandler handler) {
        this.collisionHandler = handler;
    }

    /**
     * Core logic method called every frame. 
     * Translates raw input into movement while checking for obstacles.
     * 
     * @param input The current state of the keyboard/controller.
     */
    @Override
    public void update(final InputHandler input) {
        boolean isMoving = false;
        double nextX = x;
        double nextY = y;

        // 1. Calculate the intended next position based on input
        if (input.isUp()) { 
            nextY -= SPEED; 
            direction = "up"; 
            isMoving = true; 
        }
        if (input.isDown()) { 
            nextY += SPEED; 
            direction = "down"; 
            isMoving = true; 
        }
        if (input.isLeft()) { 
            nextX -= SPEED; 
            direction = "left"; 
            isMoving = true; 
        }
        if (input.isRight()) { 
            nextX += SPEED; 
            direction = "right"; 
            isMoving = true; 
        }

        // 2. Collision Resolution
        if (isMoving && collisionHandler != null) {
            // Check horizontal movement
            final double hitboxNextX = nextX + HITBOX_OFFSET_X;
            final double hitboxCurrentY = y + HITBOX_OFFSET_Y;
            if (collisionHandler.canMove(hitboxNextX, hitboxCurrentY, HITBOX_WIDTH, HITBOX_HEIGHT)) {
                this.x = nextX;
            }

            // Check vertical movement
            final double hitboxCurrentX = x + HITBOX_OFFSET_X;
            final double hitboxNextY = nextY + HITBOX_OFFSET_Y;
            if (collisionHandler.canMove(hitboxCurrentX, hitboxNextY, HITBOX_WIDTH, HITBOX_HEIGHT)) {
                this.y = nextY;
            }
        }

        // 3. Update the logical state for the animation system
        this.state = isMoving ? "walk" : "idle";
    }

    @Override public double getX() { 
        return x; 
    }

    @Override public double getY() { 
        return y; 
    }

    @Override public void setX(final double newX) { 
        x = newX; 
    }

    @Override public void setY(final double newY) { 
        y = newY; 
    }

    @Override public String getDirection() { 
        return direction; 
    }

    @Override public String getState() { 
        return state; 
    }

    @Override public int getMoney() {
        return money;
    }

    @Override public void setMoney(final int newMoney) {
        money = newMoney;
    }

    @Override public int getEnergy() {
        return energy;
    }

    @Override public void setEnergy(final int newEnergy) {
        energy = newEnergy;
    }
}
