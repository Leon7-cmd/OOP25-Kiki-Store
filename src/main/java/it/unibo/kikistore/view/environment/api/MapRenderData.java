package it.unibo.kikistore.view.environment.api;

/**
 * A DTO containing all the information required to render the game world.
 * It encapsulates the layout and the scaling factor for the tile grid.
 * 
 * @param grid     The 2D integer array where each value represents a specific tile type.
 * @param tileSize The size of each tile, used to calculate draw positions.
 */
public record MapRenderData(int[][] grid, int tileSize) { 

    /**
     * Constructs a MapRenderData with a defensive copy of the grid.
     *
     * @param grid the 2D tile layout array
     * @param tileSize the size of each tile in pixels
     */
    public MapRenderData(final int[][] grid, final int tileSize) {
        this.tileSize = tileSize;
        this.grid = new int[grid.length][];
        for (int i = 0; i < grid.length; i++) {
            this.grid[i] = grid[i].clone();
        }
    }

    @Override
    public int[][] grid() {
        final int[][] copy = new int[this.grid.length][];
        for (int i = 0; i < this.grid.length; i++) {
            copy[i] = this.grid[i].clone();
        }
        return copy;
    }
}
