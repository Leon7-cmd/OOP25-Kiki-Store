package it.unibo.KikiStore.model.map.impl;

import it.unibo.KikiStore.model.map.api.GameTile;
/**
 * Handles collision detection logic between game entities and the map environment.
 * It uses a tile-based mask to determine traversable areas.
 */
public class CollisionHandler {

    private final GameTile collisionMask;

    /**
     * Constructs a CollisionHandler with a specific collision map.
     * 
     * @param collisionMask a GameTile implementation containing collision IDs.
     */
    public CollisionHandler(GameTile collisionMask) {
        this.collisionMask = collisionMask;
    }

    /**
     * Checks if a hitbox can move to the projected coordinates.
     * It maps the pixel-based coordinates of the entity's four corners back to the 
     * grid indices to verify if any corner overlaps with a solid tile.
     * 
     * @param nextX the target X-coordinate (left edge) of the hitbox.
     * @param nextY the target Y-coordinate (top edge) of the hitbox.
     * @param width the width of the hitbox in pixels.
     * @param height the height of the hitbox in pixels.
     * @return true if the path is clear, false if an obstacle is detected.
     */
    public boolean canMove(double nextX, double nextY, double width, double height) {
        
        int tileSize = collisionMask.getTileSize();

        // 1. Calculate the grid indices (row/column) for the 4 corners of the Hitbox.
        int leftCol = (int) (nextX / tileSize);
        int rightCol = (int) ((nextX + width - 0.1) / tileSize);
        int topRow = (int) (nextY / tileSize);
        int bottomRow = (int) ((nextY + height - 0.1) / tileSize);

        // 2. Perform the collision check
        return !isSolid(collisionMask.getTileId(leftCol, topRow)) &&
               !isSolid(collisionMask.getTileId(rightCol, topRow)) &&
               !isSolid(collisionMask.getTileId(leftCol, bottomRow)) &&
               !isSolid(collisionMask.getTileId(rightCol, bottomRow));
    }

    /**
     * Internal logic to define what constitutes a 'solid' object.
     * 
     * @param maskValue the ID retrieved from the collision map.
     * @return true if the ID represents a non-traversable obstacle (ID 1).
     */
    private boolean isSolid(int maskValue) {
        return maskValue == 1; 
    }
}