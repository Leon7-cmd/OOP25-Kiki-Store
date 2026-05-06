package it.unibo.KikiStore.model.player.impl;

import it.unibo.KikiStore.controller.api.InputHandler;
import it.unibo.KikiStore.model.map.impl.CollisionHandler;
import it.unibo.KikiStore.model.player.api.Player;

public class PlayerImpl implements Player {

    private double x;
    private double y;
    private final double speed = 2.0;
    
    // Hitbox dimensions: Defines the physical size of the player for collisions
    private final double hitboxWidth = 40; 
    private final double hitboxHeight = 20; 
    
    // Hitbox offsets: Positions the hitbox relative to the top-left corner of the sprite
    private final double hitboxOffsetX = 12; 
    private final double hitboxOffsetY = 44; 

    private String direction = "down"; 
    private String state = "idle";
    private CollisionHandler collisionHandler;

    /**
     * Initializes the player at a specific starting position.
     * 
     * @param startX Initial horizontal position.
     * @param startY Initial vertical position.
     */
    public PlayerImpl(double startX, double startY) {
        this.x = startX;
        this.y = startY;
    }

    /**
     * Injects the collision handler needed to validate movement against the map.
     * 
     * @param handler The collision logic engine.
     */
    public void setCollisionHandler(CollisionHandler handler) {
        this.collisionHandler = handler;
    }

    /**
     * Core logic method called every frame. 
     * Translates raw input into movement while checking for obstacles.
     * 
     * @param input The current state of the keyboard/controller.
     */
    @Override
    public void update(InputHandler input) {
        boolean isMoving = false;
        double nextX = x;
        double nextY = y;

        // 1. Calculate the intended next position based on input
        if (input.isUp()) { nextY -= speed; direction = "up"; isMoving = true; }
        if (input.isDown()) { nextY += speed; direction = "down"; isMoving = true; }
        if (input.isLeft()) { nextX -= speed; direction = "left"; isMoving = true; }
        if (input.isRight()) { nextX += speed; direction = "right"; isMoving = true; }

        // 2. Collision Resolution
        if (isMoving && collisionHandler != null) {
            // Check horizontal movement
            double hitboxNextX = nextX + hitboxOffsetX;
            double hitboxCurrentY = y + hitboxOffsetY;
            if (collisionHandler.canMove(hitboxNextX, hitboxCurrentY, hitboxWidth, hitboxHeight)) {
                this.x = nextX;
            }
            
            // Check vertical movement
            double hitboxCurrentX = x + hitboxOffsetX;
            double hitboxNextY = nextY + hitboxOffsetY;
            if (collisionHandler.canMove(hitboxCurrentX, hitboxNextY, hitboxWidth, hitboxHeight)) {
                this.y = nextY;
            }
        }

        // 3. Update the logical state for the animation system
        this.state = isMoving ? "walk" : "idle";
    }

    @Override public double getX() { return x; }
    @Override public double getY() { return y; }
    public String getDirection() { return direction; }
    public String getState() { return state; }
}