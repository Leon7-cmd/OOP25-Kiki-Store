package it.unibo.KikiStore.model.map.impl;

import it.unibo.KikiStore.model.map.api.GameTile;

/**
 * Implementation of GameTile interface.
 * Manages a grid-based map 
 */
public class TileMapImpl implements GameTile {

    private static final int OUT_OF_BOUNDS_TILE_ID = 1;
    private final int[][] grid;
    private final int tileSize;

    /**
     * Constructs a new TileMapImpl with specific grid data and tile dimensions.
     * 
     * @param grid     The 2D array representing the tile layout
     * @param tileSize The size of each square tile in pixels
     */
    public TileMapImpl(final int[][] grid, final int tileSize) {
        this.grid = grid.clone();
        this.tileSize = tileSize;
    }

    /**
     * Retrieves the tileId at the specified grid coordinates.
     * 
     * @param col The column index of the tile
     * @param row The row index of the tile
     */
    @Override
    public int getTileId(final int col, final int row) {
        if (row < 0 || row >= grid.length || col < 0 || col >= grid[0].length) {
            return OUT_OF_BOUNDS_TILE_ID; 
        }
        return grid[row][col];
    }

    /**
     * Gets the total width of the map measured in tiles.
     * 
     * @return the number of columns in the grid, or 0 if the grid is empty
     */
    @Override
    public int getWidthInTiles() {
        return grid.length > 0 ? grid[0].length : 0;
    }

    /**
     * Gets the total height of the map measured in tiles.
     * 
     * @return the number of rows in the grid
     */
    @Override
    public int getHeightInTiles() {
        return grid.length;
    }

    /**
     * Gets the size of a single tile side.
     * 
     * @return the tile size in pixels
     */
    @Override
    public int getTileSize() {
        return tileSize;
    }
}
