package it.unibo.KikiStore.model.map.impl;

import it.unibo.KikiStore.model.map.api.GameTile;

/**
 * Handles collision detection logic between game entities and the map environment.
 * It uses a tile-based mask to determine traversable areas.
 */
public class CollisionHandler {

    private static final double OFFSET = 0.1;
    private final GameTile collisionMask;

    /**
     * Constructs a CollisionHandler with a specific collision map.
     * 
     * @param collisionMask a GameTile implementation containing collision IDs.
     */
    public CollisionHandler(final GameTile collisionMask) {
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
    public boolean canMove(final double nextX, final double nextY, final double width, final double height) {

        final int tileSize = collisionMask.getTileSize();

        // 1. Calculate the grid indices (row/column) for the 4 corners of the Hitbox.
        final int leftCol = (int) (nextX / tileSize);
        final int rightCol = (int) ((nextX + width - OFFSET) / tileSize);
        final int topRow = (int) (nextY / tileSize);
        final int bottomRow = (int) ((nextY + height - OFFSET) / tileSize);

        // 2. Perform the collision check
        return !isSolid(collisionMask.getTileId(leftCol, topRow))
        && !isSolid(collisionMask.getTileId(rightCol, topRow))
        && !isSolid(collisionMask.getTileId(leftCol, bottomRow))
        && !isSolid(collisionMask.getTileId(rightCol, bottomRow));
    }

    /**
     * Checks if the hitbox of the player is on an interactable tile.
     * 
     * @param x the X position of the player
     * @param y the Y position of the player
     * @param width the width of the hitbox
     * @param height the height of the hitbox
     * @return the tileI of the interactable tile (0 if there is none)
     */
    public int getInteractableTileId(final double x, final double y, final double width, final double height) {
        final int tileSize = collisionMask.getTileSize();
        final int startCol = (int) (x / tileSize);
        final int endCol = (int) ((x + width - OFFSET) / tileSize);
        final int startRow = (int) (y / tileSize);
        final int endRow = (int) ((y + height - OFFSET) / tileSize);

        for (int col = startCol; col <= endCol; col++) {
            for (int row = startRow; row <= endRow; row++) {
                final int tileId = collisionMask.getTileId(col, row);
                if (tileId != 0 && tileId != 1) { 
                    return tileId;
                }
            }
        }
        return 0;
    }

    /**
     * Internal logic to define what constitutes a 'solid' object.
     * 
     * @param maskValue the ID retrieved from the collision map.
     * @return true if the ID represents a non-traversable obstacle (ID 1).
     */
    private boolean isSolid(final int maskValue) {
        return maskValue == 1; 
    }
}
