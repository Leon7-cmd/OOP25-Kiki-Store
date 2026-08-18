package it.unibo.KikiStore.view.environment.api;

/**
 * A DTO containing all the information required to render the game world.
 * It encapsulates the layout and the scaling factor for the tile grid.
 * 
 * @param grid     The 2D integer array where each value represents a specific tile type.
 * @param tileSize The size of each tile, used to calculate draw positions.
 */
public record MapRenderData(
    int[][] grid, 
    int tileSize
) { }
