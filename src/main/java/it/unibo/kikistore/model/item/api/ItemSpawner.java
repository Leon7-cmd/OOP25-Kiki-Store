package it.unibo.kikistore.model.item.api;

import java.util.List;

import it.unibo.kikistore.controller.api.InventoryController;
import it.unibo.kikistore.controller.api.PlayerController;

/**
 * Manages spawning, tracking, and player collection of items in the game world.
 */
public interface ItemSpawner {

    /**
     * Updates the internal spawn timer.
     */
    void update();

    /**
     * Spawns a given number of items in valid map positions.
     *
     * @param count the number of items to spawn.
     */
    void spawnRandomItems(int count);

    /**
     * Checks if the player collides with any active item on the ground.
     * Collected items are added to the inventory and removed from the world.
     *
     * @param player    the player entity.
     * @param inventory the player's inventory.
     * @return the list of items collected during this check.
     */
    List<GroundItem> checkCollection(PlayerController player, InventoryController inventory);

    /**
     * @return the list of active items currently in the world.
     */
    List<GroundItem> getActiveItems();
}
