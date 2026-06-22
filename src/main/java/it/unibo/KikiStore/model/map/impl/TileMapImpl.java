package it.unibo.KikiStore.model.map.impl;

import it.unibo.KikiStore.model.map.api.GameTile;

public class TileMapImpl implements GameTile {

    private final int[][] grid;
    private final int tileSize;

    public TileMapImpl(int[][] grid, int tileSize) {
        this.grid = grid;
        this.tileSize = tileSize;
    }

    @Override
    public int getTileId(int col, int row) {
        if (row < 0 || row >= grid.length || col < 0 || col >= grid[0].length) {
            return 1; 
        }
        return grid[row][col];
    }

    @Override
    public int getWidthInTiles() {
        return grid.length > 0 ? grid[0].length : 0;
    }

    @Override
    public int getHeightInTiles() {
        return grid.length;
    }

    @Override
    public int getTileSize() {
        return tileSize;
    }
}