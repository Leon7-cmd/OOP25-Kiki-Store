package it.unibo.kikiStore.item;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.kikiStore.controller.api.InputHandler;
import it.unibo.kikiStore.controller.api.PlayerController;
import it.unibo.kikiStore.controller.impl.InventoryControllerImpl;
import it.unibo.kikiStore.controller.impl.PlayerControllerImpl;
import it.unibo.kikiStore.model.inventory.api.Ingredient;
import it.unibo.kikiStore.model.inventory.impl.IngredientImpl;
import it.unibo.kikiStore.model.item.api.GroundItem;
import it.unibo.kikiStore.model.item.api.ItemSpawner;
import it.unibo.kikiStore.model.item.impl.ItemSpawnerImpl;
import it.unibo.kikiStore.model.map.api.GameTile;
import it.unibo.kikiStore.model.player.impl.PlayerImpl;

public class ItemSpawnerTest {

    private static final int TILE_SIZE = 32;

    private InventoryControllerImpl inventory;
    private List<Ingredient> samplePool;
    private GameTile testMap;

    private static final int[][] DEFAULT_GRID = {
        {1, 1, 1},
        {1, 0, 1},
        {1, 1, 1}
    };

    @BeforeEach
    public void setUp() {
        this.inventory = new InventoryControllerImpl();
        this.samplePool = List.of(new IngredientImpl("Aloe", "sprites/ingredients/aloe", 2, "plant"));
        this.testMap = createTileMap(DEFAULT_GRID);
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

    @Test
    public void testSpawnOnWalkableTile() {
        final ItemSpawner spawner = new ItemSpawnerImpl(testMap, samplePool, new Random(1));
        spawner.spawnRandomItems(1);

        final List<GroundItem> items = spawner.getActiveItems();
        assertEquals(1, items.size());
        assertEquals(32.0, items.get(0).getX());
        assertEquals(32.0, items.get(0).getY());
    }

    @Test
    public void testSpawnCappedToAvailableTiles() {
        final ItemSpawner spawner = new ItemSpawnerImpl(testMap, samplePool);
        // Chiediamo 5 item ma c'è solo 1 tile libero
        spawner.spawnRandomItems(5);

        assertEquals(1, spawner.getActiveItems().size());
    }

    @Test
    public void testZeroSpawnOnSolidMap() {
        final int[][] solidGrid = {{1, 1}, {1, 1}};
        final ItemSpawner spawner = new ItemSpawnerImpl(createTileMap(solidGrid), samplePool);

        spawner.spawnRandomItems(3);
        assertTrue(spawner.getActiveItems().isEmpty());
    }

    @Test
    public void testCollectItemAndAddToInventory() {
        final ItemSpawner spawner = new ItemSpawnerImpl(testMap, samplePool, new Random(1));
        spawner.spawnRandomItems(1);

        final InputHandler dummyInput = new InputHandler() {
            @Override public boolean isUp() { return false; }
            @Override public boolean isDown() { return false; }
            @Override public boolean isLeft() { return false; }
            @Override public boolean isRight() { return false; }
            @Override public boolean isAction() { return false; }
            @Override public boolean isEscapePressed() { return false; }
            @Override public boolean isInventoryPressed() { return false; }
            @Override public boolean isCraftingPressed() { return false; }
            @Override public boolean isMouseClicked() { return false; }
            @Override public double getMouseX() { return 0.0; }
            @Override public double getMouseY() { return 0.0; }
        };
        final PlayerController player = new PlayerControllerImpl(new PlayerImpl(0, 0), dummyInput);

        final List<GroundItem> collected = spawner.checkCollection(player, inventory);

        assertEquals(1, collected.size());
        assertTrue(spawner.getActiveItems().isEmpty());
        assertEquals(1, inventory.getInventory().getIngredients().size());
        assertEquals("Aloe", inventory.getInventory().getIngredients().get(0).getName());
    }

    @Test
    public void testActiveItemsIsUnmodifiable() {
        final ItemSpawner spawner = new ItemSpawnerImpl(testMap, samplePool);
        spawner.spawnRandomItems(1);

        final List<GroundItem> items = spawner.getActiveItems();
        assertThrows(UnsupportedOperationException.class, () -> items.clear());
    }
}
