package it.unibo.KikiStore.model.item.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

import it.unibo.KikiStore.controller.api.InventoryController;
import it.unibo.KikiStore.model.inventory.api.Ingredient;
import it.unibo.KikiStore.model.inventory.impl.IngredientImpl;
import it.unibo.KikiStore.model.item.api.Item;
import it.unibo.KikiStore.model.item.api.ItemSpawner;
import it.unibo.KikiStore.model.map.api.GameTile;
import it.unibo.KikiStore.model.player.api.Player;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;

/**
 * Manages periodic spawning, tracking, and player collection of items in the game world.
 */
public final class ItemSpawnerImpl implements ItemSpawner {

    private static final int WALKABLE_TILE_ID = 0;
    private static final int MAX_SPAWN_ATTEMPTS = 50;

    private static final int SPAWN_INTERVAL_TICKS = 18_000;
    private static final int ITEMS_PER_WAVE = 10;
    private static final int MAX_ACTIVE_ITEMS = 30;

    private final GameTile collisionMask;
    private final List<Ingredient> ingredientPool;
    private final List<Item> activeItems;
    private final Random random;

    private int tickCounter;

    /**
     * Constructs an item spawner with a default random generator.
     *
     * @param collisionMask  the map mask used to verify walkable tiles.
     * @param ingredientPool available ingredients to spawn randomly from.
     */
    public ItemSpawnerImpl(final GameTile collisionMask, final List<Ingredient> ingredientPool) {
        this(collisionMask, ingredientPool, new Random());
    }

    /**
     * Constructs an item spawner with a deterministic random generator for testing.
     *
     * @param collisionMask  the map mask used to verify walkable tiles.
     * @param ingredientPool available ingredients to spawn randomly from.
     * @param random         random instance for deterministic testing.
     */
    public ItemSpawnerImpl(
        final GameTile collisionMask,
        final List<Ingredient> ingredientPool,
        final Random random
    ) {
        this.collisionMask = Objects.requireNonNull(collisionMask, "Collision mask cannot be null");
        this.ingredientPool = List.copyOf(Objects.requireNonNull(ingredientPool, "Ingredient pool cannot be null"));
        this.random = Objects.requireNonNull(random, "Random generator cannot be null");
        this.activeItems = new ArrayList<>();
        this.tickCounter = 0;
    }

    @Override
    public void update() {
        tickCounter++;
        if (tickCounter >= SPAWN_INTERVAL_TICKS) {
            tickCounter = 0;
            // Spawn items only if the world has not exceeded the maximum active capacity
            final int availableSlots = MAX_ACTIVE_ITEMS - activeItems.size();
            if (availableSlots > 0) {
                spawnRandomItems(Math.min(ITEMS_PER_WAVE, availableSlots));
            }
        }
    }

    @Override
    public void spawnRandomItems(final int count) {
        if (ingredientPool.isEmpty() || count <= 0) {
            return;
        }

        final int maxCols = collisionMask.getWidthInTiles();
        final int maxRows = collisionMask.getHeightInTiles();
        final int tileSize = collisionMask.getTileSize();
        final Set<Point2D> occupiedTiles = new HashSet<>();

        // Register coordinates of tiles already occupied by active items
        for (final Item item : activeItems) {
            occupiedTiles.add(new Point2D(item.getX() / tileSize, item.getY() / tileSize));
        }

        for (int i = 0; i < count; i++) {
            boolean spawned = false;
            int attempts = 0;

            while (!spawned && attempts < MAX_SPAWN_ATTEMPTS) {
                final int col = random.nextInt(maxCols);
                final int row = random.nextInt(maxRows);
                final Point2D tilePoint = new Point2D(col, row);

                // Target tile must be walkable (ID 0) and not already occupied by another item
                if (collisionMask.getTileId(col, row) == WALKABLE_TILE_ID && !occupiedTiles.contains(tilePoint)) {
                    final double worldX = col * (double) tileSize;
                    final double worldY = row * (double) tileSize;

                    // Randomly select an ingredient template and instantiate a new one
                    final Ingredient chosen = ingredientPool.get(random.nextInt(ingredientPool.size()));
                    final Ingredient spawnedIngredient = new IngredientImpl(
                        chosen.getName(),
                        chosen.getImagePath(),
                        1,
                        chosen.getType()
                    );

                    activeItems.add(new GroundItem(
                        chosen.getImagePath(),
                        worldX,
                        worldY,
                        tileSize,
                        tileSize,
                        false,
                        spawnedIngredient
                    ));

                    occupiedTiles.add(tilePoint);
                    spawned = true;
                }
                attempts++;
            }
        }
    }

    @Override
    public List<Item> checkCollection(final Player player, final InventoryController inventory) {
        Objects.requireNonNull(player, "Player cannot be null");
        final List<Item> collected = new ArrayList<>();
        final Rectangle2D playerHitbox = player.getHitbox();

        final Iterator<Item> iterator = activeItems.iterator();
        while (iterator.hasNext()) {
            final Item item = iterator.next();
            // Verify collision between player hitbox and item bounding box
            if (item.getHitbox().intersects(playerHitbox)) {
                collected.add(item);
                if (inventory != null && item instanceof GroundItem groundItem) {
                    inventory.addIngredient(
                        groundItem.getIngredient().getName(), 
                        groundItem.getIngredient().getImagePath(), 
                        groundItem.getQuantity(), 
                        groundItem.getIngredient().getType()
                    );
                }
                iterator.remove();
            }
        }
        return collected;
    }

    @Override
    public List<Item> getActiveItems() {
        return Collections.unmodifiableList(activeItems);
    }
}
