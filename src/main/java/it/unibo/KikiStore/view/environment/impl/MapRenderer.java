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
                
                if(tileId != -1){
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
    }

    /**
     * Helper method to associate numeric IDs with their corresponding asset paths.
     * 
     * @param tileId The ID from the map matrix.
     * @return The key used by SpriteManager to locate the texture.
     */
    private String getSpriteNameFromId(int tileId) {
        return switch (tileId) {
            case 0 -> "sprites/map/grass/grassCe"; 
            case 1 -> "sprites/map/grass/grassCo1";   
            case 2 -> "sprites/map/grass/grassCo2";
            case 3 -> "sprites/map/grass/grassCo3";
            case 4 -> "sprites/map/grass/grassCo4";
            case 5 -> "sprites/map/grass/grassS1";
            case 6 -> "sprites/map/grass/grassS2";
            case 7 -> "sprites/map/grass/grassS3";
            case 8 -> "sprites/map/grass/grassS4";
            case 9 -> "sprites/map/grass/grassA1";
            case 10 -> "sprites/map/grass/grassA2";
            case 11 -> "sprites/map/grass/grassA3";
            case 12 -> "sprites/map/grass/grassA4";
            case 13 -> "sprites/map/grass/water";
            case 14 -> "sprites/map/stone/stoneCe";
            case 15 -> "sprites/map/stone/stoneCo1";
            case 16 -> "sprites/map/stone/stoneCo2";
            case 17 -> "sprites/map/stone/stoneCo3";
            case 18 -> "sprites/map/stone/stoneCo4";
            case 19 -> "sprites/map/stone/stoneS1";
            case 20 -> "sprites/map/stone/stoneS2";
            case 21 -> "sprites/map/stone/stoneS3";
            case 22 -> "sprites/map/stone/stoneS4";
            case 23 -> "sprites/map/stone/stoneA1";
            case 24 -> "sprites/map/stone/stoneA2";
            case 25 -> "sprites/map/stone/stoneA3";
            case 26 -> "sprites/map/stone/stoneA4";
            case 27 -> "sprites/map/grass/grassV1";
            case 28 -> "sprites/map/grass/grassV2";
            case 29 -> "sprites/map/grass/grassV3";
            case 30 -> "sprites/map/grass/grassV4";
            case 31 -> "sprites/map/structure/teleUp";
            case 32 -> "sprites/map/structure/teleDown";
            case 33 -> "sprites/map/sand/sandCe";
            case 34 -> "sprites/map/sand/sandCo1";
            case 35 -> "sprites/map/sand/sandCo2";
            case 36 -> "sprites/map/sand/sandCo3";
            case 37 -> "sprites/map/sand/sandCo4";
            case 38 -> "sprites/map/sand/sandS1";
            case 39 -> "sprites/map/sand/sandS2";
            case 40 -> "sprites/map/sand/sandS3";
            case 41 -> "sprites/map/sand/sandS4";
            case 42 -> "sprites/map/sand/sandV1";
            case 43 -> "sprites/map/sand/sandV2";
            case 44 -> "sprites/map/darkStone/darkStoneCe";
            case 45 -> "sprites/map/darkStone/darkStoneCo1";
            case 46 -> "sprites/map/darkStone/darkStoneCo2";
            case 47 -> "sprites/map/darkStone/darkStoneCo3";
            case 48 -> "sprites/map/darkStone/darkStoneCo4";
            case 49 -> "sprites/map/darkStone/darkStoneS1";
            case 50 -> "sprites/map/darkStone/darkStoneS2";
            case 51 -> "sprites/map/darkStone/darkStoneS3";
            case 52 -> "sprites/map/darkStone/darkStoneS4";
            default -> "unknown"; 
        };
    }
}