package it.unibo.KikiStore.model.map.api;

/**
 * Interface representing a grid-based map structure.
 * It provides the necessary methods to handle tile-specific data and map dimensions.
 */
public interface GameTile {

    /**
     * Retrieves the specific ID of a tile at a given coordinate.
     * This ID is typically used to determine the tile type (e.g., grass, wall, water).
     * 
     * @param col the horizontal index (column) in the grid.
     * @param row the vertical index (row) in the grid.
     * @return the unique identifier for the tile at the specified position.
     */
    int getTileId(int col, int row);

    /**
     * @return the total number of columns in the map grid.
     */
    int getWidthInTiles();

    /**
     * @return the total number of rows in the map grid.
     */
    int getHeightInTiles();

    /**
     * Returns the size of a single tile in pixels. 
     * 
     * @return the width and height of a square tile.
     */
    int getTileSize();
}
