package it.unibo.KikiStore.view.environment.impl;

import it.unibo.KikiStore.view.environment.api.MapRenderData;
import it.unibo.KikiStore.view.utility.SpriteManager;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

/**
 * Responsible for drawing the game world background.
 * It iterates through the map grid and renders the corresponding textures for each tile.
 */
public class MapRenderer {

    private final SpriteManager spriteManager;

    public MapRenderer(SpriteManager spriteManager) {
        this.spriteManager = spriteManager;
    }

    /**
     * Renders the entire tile grid onto the game canvas.
     * The map structure is derived from the loaded .txt files.
     * 
     * @param gc      The GraphicsContext used for drawing.
     * @param mapData The DTO containing the grid matrix and tile size.
     */
    public void render(GraphicsContext gc, MapRenderData mapData) {
        int[][] grid = mapData.grid();
        int tileSize = mapData.tileSize();

        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                
                int tileId = grid[row][col];
                
                // 1. Map the numerical ID (0, 1, 2, etc.) to a logical sprite name
                String spriteName = getSpriteNameFromId(tileId);
                
                // 2. Fetch the associated Image from the SpriteManager
                Image tileImage = spriteManager.getStaticSprite(spriteName);

                // 3. Convert grid indices into pixel-based screen coordinates
                double screenX = col * tileSize;
                double screenY = row * tileSize;

                // 4. Draw the tile or a placeholder if the resource is missing
                if (tileImage != null) {
                    gc.drawImage(tileImage, screenX, screenY, tileSize, tileSize);
                } else {
                    //Gray blocks with outlines help identify missing assets
                    gc.setFill(Color.DARKGRAY);
                    gc.fillRect(screenX, screenY, tileSize, tileSize);
                    gc.setStroke(Color.BLACK);
                    gc.strokeRect(screenX, screenY, tileSize, tileSize);
                }
            }
        }
    }

    /**
     * Helper method to associate numeric IDs with their corresponding asset paths.
     * 
     * @param tileId The ID from the map matrix.
     * @return The key used by SpriteManager to locate the texture.
     */
    private String getSpriteNameFromId(int tileId) {
        return switch (tileId) {
            case 0 -> "sprites/map/grass"; 
            case 1 -> "sprites/map/wall";   
            case 2 -> "sprites/map/water";
            default -> "unknown"; 
        };
    }
}