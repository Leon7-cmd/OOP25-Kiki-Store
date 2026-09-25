package it.unibo.KikiStore.model.item.api;

import java.util.List;

import it.unibo.KikiStore.controller.api.InventoryController;
import it.unibo.KikiStore.model.player.api.Player;

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
    List<Item> checkCollection(Player player, InventoryController inventory);

    /**
     * @return the list of active items currently in the world.
     */
    List<Item> getActiveItems();
}
