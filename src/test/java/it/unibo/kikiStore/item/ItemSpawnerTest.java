package it.unibo.kikistore.item;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.kikistore.controller.api.InputHandler;
import it.unibo.kikistore.controller.api.PlayerController;
import it.unibo.kikistore.controller.impl.InventoryControllerImpl;
import it.unibo.kikistore.controller.impl.PlayerControllerImpl;
import it.unibo.kikistore.model.inventory.api.Ingredient;
import it.unibo.kikistore.model.inventory.impl.IngredientImpl;
import it.unibo.kikistore.model.item.api.GroundItem;
import it.unibo.kikistore.model.item.api.ItemSpawner;
import it.unibo.kikistore.model.item.impl.ItemSpawnerImpl;
import it.unibo.kikistore.model.map.api.GameTile;
import it.unibo.kikistore.model.player.impl.PlayerImpl;

/**
 * Unit tests for {@link ItemSpawnerImpl}.
 * Verifies random item spawning, collection logic, and unmodifiable state guarantees.
 */
class ItemSpawnerTest {

    private static final int TILE_SIZE = 32;
    private static final int REQUESTED_ITEMS_OVER_CAP = 5;
    private static final int SOLID_SPAWN_ATTEMPTS = 3;
    private static final String INGREDIENT_NAME = "Aloe";

    private static final int[][] DEFAULT_GRID = {
        {1, 1, 1},
        {1, 0, 1},
        {1, 1, 1},
    };

    private InventoryControllerImpl inventory;
    private List<Ingredient> samplePool;
    private GameTile testMap;

    @BeforeEach
    void setUp() {
        this.inventory = new InventoryControllerImpl();
        this.samplePool = List.of(new IngredientImpl(INGREDIENT_NAME, "sprites/ingredients/aloe", 2, "plant"));
        this.testMap = this.createTileMap(DEFAULT_GRID);
    }

    @Test
    void testSpawnOnWalkableTile() {
        final ItemSpawner spawner = new ItemSpawnerImpl(this.testMap, this.samplePool, new Random(1));
        spawner.spawnRandomItems(1);

        final List<GroundItem> items = spawner.getActiveItems();
        assertEquals(1, items.size());
        assertEquals(32.0, items.get(0).getX());
        assertEquals(32.0, items.get(0).getY());
    }

    @Test
    void testSpawnCappedToAvailableTiles() {
        final ItemSpawner spawner = new ItemSpawnerImpl(this.testMap, this.samplePool);
        spawner.spawnRandomItems(REQUESTED_ITEMS_OVER_CAP);

        assertEquals(1, spawner.getActiveItems().size());
    }

    @Test
    void testZeroSpawnOnSolidMap() {
        final int[][] solidGrid = {
            {1, 1},
            {1, 1},
        };
        final ItemSpawner spawner = new ItemSpawnerImpl(this.createTileMap(solidGrid), this.samplePool);

        spawner.spawnRandomItems(SOLID_SPAWN_ATTEMPTS);
        assertTrue(spawner.getActiveItems().isEmpty());
    }

    @Test
    void testCollectItemAndAddToInventory() {
        final ItemSpawner spawner = new ItemSpawnerImpl(this.testMap, this.samplePool, new Random(1));
        spawner.spawnRandomItems(1);

        final InputHandler dummyInput = new IdleInputHandler();
        final PlayerController player = new PlayerControllerImpl(new PlayerImpl(0, 0), dummyInput);

        final List<GroundItem> collected = spawner.checkCollection(player, this.inventory);

        assertEquals(1, collected.size());
        assertTrue(spawner.getActiveItems().isEmpty());
        assertTrue(this.inventory.hasIngredient(INGREDIENT_NAME));
        assertEquals(1, this.inventory.getIngredientQuantity(INGREDIENT_NAME));
    }

    @Test
    void testActiveItemsIsUnmodifiable() {
        final ItemSpawner spawner = new ItemSpawnerImpl(this.testMap, this.samplePool);
        spawner.spawnRandomItems(1);

        final List<GroundItem> items = spawner.getActiveItems();
        assertThrows(UnsupportedOperationException.class, items::clear);
    }

    private GameTile createTileMap(final int[][] grid) {
        return new GameTile() {
            @Override
            public int getTileId(final int col, final int row) {
                return grid[row][col];
            }

            @Override
            public int getWidthInTiles() {
                return grid[0].length;
            }

            @Override
            public int getHeightInTiles() {
                return grid.length;
            }

            @Override
            public int getTileSize() {
                return TILE_SIZE;
            }
        };
    }

    /**
     * Dummy input handler providing default idle inputs for headless testing.
     */
    private static final class IdleInputHandler implements InputHandler {

        @Override
        public boolean isUp() {
            return false;
        }

        @Override
        public boolean isDown() {
            return false;
        }

        @Override
        public boolean isLeft() {
            return false;
        }

        @Override
        public boolean isRight() {
            return false;
        }

        @Override
        public boolean isAction() {
            return false;
        }

        @Override
        public boolean isEscapePressed() {
            return false;
        }

        @Override
        public boolean isInventoryPressed() {
            return false;
        }

        @Override
        public boolean isCraftingPressed() {
            return false;
        }

        @Override
        public boolean isMouseClicked() {
            return false;
        }

        @Override
        public double getMouseX() {
            return 0.0;
        }

        @Override
        public double getMouseY() {
            return 0.0;
        }
    }
}
