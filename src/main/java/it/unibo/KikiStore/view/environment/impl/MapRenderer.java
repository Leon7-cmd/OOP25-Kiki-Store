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
            case 53 -> "sprites/map/structure/house01";
            case 54 -> "sprites/map/structure/house02";
            case 55 -> "sprites/map/structure/house03";
            case 56 -> "sprites/map/structure/house04";
            case 57 -> "sprites/map/structure/house05";
            case 58 -> "sprites/map/structure/house06";
            case 59 -> "sprites/map/structure/house07";
            case 60 -> "sprites/map/structure/house08";
            case 61 -> "sprites/map/structure/house09";
            case 62 -> "sprites/map/structure/house10";
            case 63 -> "sprites/map/structure/house11";
            case 64 -> "sprites/map/structure/house12";
            case 65 -> "sprites/map/structure/house13";
            case 66 -> "sprites/map/structure/house14";
            case 67 -> "sprites/map/structure/house15";
            case 68 -> "sprites/map/structure/house16";
            case 69 -> "sprites/map/structure/house17";
            case 70 -> "sprites/map/structure/house18";
            case 71 -> "sprites/map/structure/house19";
            case 72 -> "sprites/map/structure/house20";
            case 73 -> "sprites/map/structure/house21";
            case 74 -> "sprites/map/structure/house22";
            case 75 -> "sprites/map/structure/house23";
            case 76 -> "sprites/map/structure/house24";
            case 77 -> "sprites/map/structure/house25";
            case 78 -> "sprites/map/structure/shop01";
            case 79 -> "sprites/map/structure/shop02";
            case 80 -> "sprites/map/structure/shop03";
            case 81 -> "sprites/map/structure/shop04";
            case 82 -> "sprites/map/structure/shop05";
            case 83 -> "sprites/map/structure/shop06";
            case 84 -> "sprites/map/structure/shop07";
            case 85 -> "sprites/map/structure/shop08";
            case 86 -> "sprites/map/structure/shop09";
            case 87 -> "sprites/map/structure/shop10";
            case 88 -> "sprites/map/structure/shop11";
            case 89 -> "sprites/map/structure/shop12";
            case 90 -> "sprites/map/structure/shop13";
            case 91 -> "sprites/map/structure/shop14";
            case 92 -> "sprites/map/structure/shop15";
            case 93 -> "sprites/map/structure/shop16";
            case 94 -> "sprites/map/structure/shop17";
            case 95 -> "sprites/map/structure/shop18";
            case 96 -> "sprites/map/structure/shop19";
            case 97 -> "sprites/map/structure/shop20";
            case 98 -> "sprites/map/structure/shop21";
            case 99 -> "sprites/map/structure/shop22";
            case 100 -> "sprites/map/structure/shop23";
            case 101 -> "sprites/map/structure/shop24";
            case 102 -> "sprites/map/structure/shop25";
            case 103 -> "sprites/map/structure/shop26";
            case 104 -> "sprites/map/structure/shop27";
            case 105 -> "sprites/map/structure/shop28";
            case 106 -> "sprites/map/structure/shop29";
            case 107 -> "sprites/map/structure/shop30";
            case 108 -> "sprites/map/structure/shop31";
            case 109 -> "sprites/map/structure/shop32";
            case 110 -> "sprites/map/structure/shop33";
            case 111 -> "sprites/map/structure/shop34";
            case 112 -> "sprites/map/structure/shop35";
            case 113 -> "sprites/map/structure/shop36";
            case 114 -> "sprites/map/structure/shop37";
            case 115 -> "sprites/map/structure/shop38";
            case 116 -> "sprites/map/structure/shop39";
            case 117 -> "sprites/map/structure/shop40";
            case 118 -> "sprites/map/structure/shop41";
            case 119 -> "sprites/map/structure/stand1";
            case 120 -> "sprites/map/structure/stand2";
            case 121 -> "sprites/map/structure/stand3";
            case 122 -> "sprites/map/structure/stand4";
            case 123 -> "sprites/map/structure/stand5";
            case 124 -> "sprites/map/structure/stand6";
            case 125 -> "sprites/map/structure/stand7";
            case 126 -> "sprites/map/structure/stand8";
            case 127 -> "sprites/map/structure/stand9";
            case 128 -> "sprites/map/structure/stand10";
            case 129 -> "sprites/map/structure/stand11";
            case 130 -> "sprites/map/structure/stand12";
            case 131 -> "sprites/map/structure/stand13";
            case 132 -> "sprites/map/structure/stand14";
            case 133 -> "sprites/map/structure/stand15";
            case 134 -> "sprites/map/structure/stand16";
            case 135 -> "sprites/map/structure/stand17";
            case 136 -> "sprites/map/structure/stand18";
            case 137 -> "sprites/map/structure/stand19";
            case 138 -> "sprites/map/structure/stand20";
            case 139 -> "sprites/map/structure/stand21";
            case 140 -> "sprites/map/structure/stand22";
            case 141 -> "sprites/map/structure/stand23";
            case 142 -> "sprites/map/structure/stand24";
            case 143 -> "sprites/map/structure/stand25";
            case 144 -> "sprites/map/structure/stand26";
            case 145 -> "sprites/map/structure/stand27";
            case 146 -> "sprites/map/structure/stand28";
            case 147 -> "sprites/map/structure/stand29";
            case 148 -> "sprites/map/structure/stand30";
            case 149 -> "sprites/map/structure/stand31";
            case 150 -> "sprites/map/structure/stand32";
            case 151 -> "sprites/map/structure/stand33";
            case 152 -> "sprites/map/structure/stand34";
            case 153 -> "sprites/map/structure/stand35";

            
            default -> "unknown"; 
        };
    }
}