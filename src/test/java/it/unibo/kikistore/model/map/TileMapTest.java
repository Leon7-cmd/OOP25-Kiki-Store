package it.unibo.kikistore.model.map;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.kikistore.model.map.impl.TileMapImpl;

/**
 * Unit tests for {@link TileMapImpl}.
 * Verifies grid dimensions, in-bound tile retrieval, and out-of-bounds fallback behavior.
 */
class TileMapTest {

    private static final int TILE_SIZE = 32;
    private static final int OUT_OF_BOUNDS_ID = 1;
    private static final int NEGATIVE_INDEX = -1;
    private static final int NEGATIVE_DEEP_INDEX = -2;
    private static final int EXCEEDING_COL_INDEX = 4;
    private static final int EXCEEDING_ROW_INDEX = 3;
    private static final int OUT_OF_BOUNDS_FAR_INDEX = 10;

    /*
     * Sample 3x4 grid:
     * Row 0: [0, 2, 0, 3]
     * Row 1: [1, 1, 0, 0]
     * Row 2: [0, 0, 4, 1]
     */
    private final int[][] sampleGrid = {
        {0, 2, 0, 3},
        {1, 1, 0, 0},
        {0, 0, 4, 1},
    };

    private TileMapImpl tileMap;

    @BeforeEach
    void setUp() {
        this.tileMap = new TileMapImpl(this.sampleGrid, TILE_SIZE);
    }

    @Test
    void testMapDimensions() {
        assertEquals(4, this.tileMap.getWidthInTiles());
        assertEquals(3, this.tileMap.getHeightInTiles());
        assertEquals(TILE_SIZE, this.tileMap.getTileSize());
    }

    @Test
    void testGetTileIdInBounds() {
        assertEquals(0, this.tileMap.getTileId(0, 0));
        assertEquals(2, this.tileMap.getTileId(1, 0));
        assertEquals(3, this.tileMap.getTileId(3, 0));
        assertEquals(1, this.tileMap.getTileId(0, 1));
        assertEquals(4, this.tileMap.getTileId(2, 2));
    }

    @Test
    void testGetTileIdOutOfBoundsNegativeIndices() {
        assertEquals(OUT_OF_BOUNDS_ID, this.tileMap.getTileId(NEGATIVE_INDEX, 0));
        assertEquals(OUT_OF_BOUNDS_ID, this.tileMap.getTileId(0, NEGATIVE_INDEX));
        assertEquals(OUT_OF_BOUNDS_ID, this.tileMap.getTileId(NEGATIVE_DEEP_INDEX, NEGATIVE_DEEP_INDEX));
    }

    @Test
    void testGetTileIdOutOfBoundsExceedingDimensions() {
        assertEquals(OUT_OF_BOUNDS_ID, this.tileMap.getTileId(EXCEEDING_COL_INDEX, 1));
        assertEquals(OUT_OF_BOUNDS_ID, this.tileMap.getTileId(1, EXCEEDING_ROW_INDEX));
        assertEquals(OUT_OF_BOUNDS_ID, this.tileMap.getTileId(OUT_OF_BOUNDS_FAR_INDEX, OUT_OF_BOUNDS_FAR_INDEX));
    }

    @Test
    void testEmptyGridDimensions() {
        final TileMapImpl emptyMap = new TileMapImpl(new int[0][0], TILE_SIZE);
        assertEquals(0, emptyMap.getWidthInTiles());
        assertEquals(0, emptyMap.getHeightInTiles());
    }
}
