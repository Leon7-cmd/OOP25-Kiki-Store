package it.unibo.KikiStore.map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.KikiStore.model.map.api.GameTile;
import it.unibo.KikiStore.model.map.impl.CollisionHandler;

/**
 * Unit tests for {@link CollisionHandler}.
 * Verifies standard movement, solid obstacle blocking, and interactable tile detection.
 */
class CollisionHandlerTest {

    private static final int TILE_SIZE = 32;

    /*
     * Mock Grid Layout (4x4):
     * [0, 0, 1, 0]  (Row 0: col 2 is Solid)
     * [0, 0, 0, 0]  (Row 1: all walkable)
     * [0, 2, 0, 3]  (Row 2: col 1 is Interactable ID 2, col 3 is Interactable ID 3)
     * [1, 1, 1, 1]  (Row 3: all Solid)
     */
    private static final int[][] TEST_GRID = {
        {0, 0, 1, 0},
        {0, 0, 0, 0},
        {0, 2, 0, 3},
        {1, 1, 1, 1}
    };

    private CollisionHandler collisionHandler;

    @BeforeEach
    void setUp() {
        final GameTile mockMap = new GameTile() {
            @Override
            public int getTileSize() {
                return TILE_SIZE;
            }

            @Override
            public int getTileId(final int col, final int row) {
                if (row < 0 || row >= TEST_GRID.length || col < 0 || col >= TEST_GRID[0].length) {
                    return 1; // Out of bounds treated as solid
                }
                return TEST_GRID[row][col];
            }

            @Override
            public int getWidthInTiles() {
                return TEST_GRID[0].length;
            }

            @Override
            public int getHeightInTiles() {
                return TEST_GRID.length;
            }
        };

        collisionHandler = new CollisionHandler(mockMap);
    }

    @Test
    void testCanMoveOnEmptyTiles() {
        // Position on row 1, col 0 (32x32 hitbox completely in free space)
        final double x = 0;
        final double y = 32;
        assertTrue(collisionHandler.canMove(x, y, TILE_SIZE, TILE_SIZE));
    }

    @Test
    void testCannotMoveIntoSolidTile() {
        // Target overlaps with (col 2, row 0) which is solid (ID 1)
        final double targetX = 64;
        final double targetY = 0;
        assertFalse(collisionHandler.canMove(targetX, targetY, TILE_SIZE, TILE_SIZE));
    }

    @Test
    void testCannotMoveIntoSolidBottomRow() {
        // Target overlaps with bottom row (row 3)
        final double targetX = 32;
        final double targetY = 96;
        assertFalse(collisionHandler.canMove(targetX, targetY, TILE_SIZE, TILE_SIZE));
    }

    @Test
    void testGetInteractableTileIdPresent() {
        // Overlapping interactable tile at (col 1, row 2) -> ID 2
        final double x = 32;
        final double y = 64;
        final int foundTileId = collisionHandler.getInteractableTileId(x, y, TILE_SIZE, TILE_SIZE);
        assertEquals(2, foundTileId);
    }

    @Test
    void testGetInteractableTileIdAbsent() {
        // Free area without interactable tiles (col 0, row 0) -> ID 0
        final double x = 0;
        final double y = 0;
        final int foundTileId = collisionHandler.getInteractableTileId(x, y, TILE_SIZE, TILE_SIZE);
        assertEquals(0, foundTileId);
    }

    @Test
    void testInteractableTileIsTraversable() {
        // Interactable tiles (ID 2, 3) are not solid (ID 1), so canMove needs to be true
        final double x = 32;
        final double y = 64;
        assertTrue(collisionHandler.canMove(x, y, TILE_SIZE, TILE_SIZE));
    }
}