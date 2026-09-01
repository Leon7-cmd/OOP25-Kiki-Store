package it.unibo.KikiStore.map;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.KikiStore.model.map.impl.TileMapImpl;

/**
 * Unit tests for {@link TileMapImpl}.
 * Verifies grid dimensions, in-bound tile retrieval, and out-of-bounds fallback behavior.
 */
class TileMapTest {

    private static final int TILE_SIZE = 32;
    private static final int OUT_OF_BOUNDS_ID = 1;

    /*
     * Sample 3x4 grid:
     * Row 0: [0, 2, 0, 3]
     * Row 1: [1, 1, 0, 0]
     * Row 2: [0, 0, 4, 1]
     */
    private static final int[][] SAMPLE_GRID = {
        {0, 2, 0, 3},
        {1, 1, 0, 0},
        {0, 0, 4, 1}
    };

    private TileMapImpl tileMap;

    @BeforeEach
    void setUp() {
        tileMap = new TileMapImpl(SAMPLE_GRID, TILE_SIZE);
    }

    @Test
    void testMapDimensions() {
        assertEquals(4, tileMap.getWidthInTiles());
        assertEquals(3, tileMap.getHeightInTiles());
        assertEquals(TILE_SIZE, tileMap.getTileSize());
    }

    @Test
    void testGetTileIdInBounds() {
        assertEquals(0, tileMap.getTileId(0, 0));
        assertEquals(2, tileMap.getTileId(1, 0));
        assertEquals(3, tileMap.getTileId(3, 0));
        assertEquals(1, tileMap.getTileId(0, 1));
        assertEquals(4, tileMap.getTileId(2, 2));
    }

    @Test
    void testGetTileIdOutOfBoundsNegativeIndices() {
        assertEquals(OUT_OF_BOUNDS_ID, tileMap.getTileId(-1, 0));
        assertEquals(OUT_OF_BOUNDS_ID, tileMap.getTileId(0, -1));
        assertEquals(OUT_OF_BOUNDS_ID, tileMap.getTileId(-2, -2));
    }

    @Test
    void testGetTileIdOutOfBoundsExceedingDimensions() {
        assertEquals(OUT_OF_BOUNDS_ID, tileMap.getTileId(4, 1)); // col index out (max is 3)
        assertEquals(OUT_OF_BOUNDS_ID, tileMap.getTileId(1, 3)); // row index out (max is 2)
        assertEquals(OUT_OF_BOUNDS_ID, tileMap.getTileId(10, 10));
    }

    @Test
    void testEmptyGridDimensions() {
        final TileMapImpl emptyMap = new TileMapImpl(new int[0][0], TILE_SIZE);
        assertEquals(0, emptyMap.getWidthInTiles());
        assertEquals(0, emptyMap.getHeightInTiles());
    }
}