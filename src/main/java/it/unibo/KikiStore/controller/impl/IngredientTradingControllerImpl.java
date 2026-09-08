package it.unibo.KikiStore.controller.impl;

import java.util.List;

import it.unibo.KikiStore.controller.api.InventoryController;
import it.unibo.KikiStore.controller.api.ShopTradingController;
import it.unibo.KikiStore.model.economy.api.TransactionOutcome;
import it.unibo.KikiStore.model.economy.api.TransactionResults;
import it.unibo.KikiStore.model.inventory.api.GameCatalog;
import it.unibo.KikiStore.model.inventory.api.Ingredient;
import it.unibo.KikiStore.model.player.api.Player;

/**
 * Implementazione di {@link ShopTradingController} specializzata per gli ingredienti.
 * Usata dallo Stand degli Ingredienti: non conosce né tratta pozioni.
 */
public final class IngredientTradingControllerImpl implements ShopTradingController<Ingredient> {

    private final GameCatalog catalog;
    private final InventoryController inventoryController;
    private final Player player;

    /**
     * @param catalog catalogo condiviso da cui leggere gli ingredienti acquistabili
     * @param inventoryController controller condiviso dell'inventario del giocatore
     * @param player il giocatore, per leggere/modificare le monete possedute
     */
    public IngredientTradingControllerImpl(
            final GameCatalog catalog,
            final InventoryController inventoryController,
            final Player player) {
        this.catalog = catalog;
        this.inventoryController = inventoryController;
        this.player = player;
    }

    @Override
    public List<Ingredient> getBuyableItems() {
        return catalog.getAllIngredients();
    } 

    @Override //con inventory condiviso
    public List<Ingredient> getSellableItems() {
        return inventoryController.getInventory().getIngredients();
    }

    @Override
    public int getBuyPrice(final Ingredient item) {
        return item.getPrice();
    }

    @Override
    public int getSellPrice(final Ingredient item) {
        return item.getPrice(); // prezzo di vendita
    }

    @Override
    public TransactionResults buy(final Ingredient item) {
        if (!catalog.getAllIngredients().contains(item)) {
            return new TransactionResults(TransactionOutcome.ITEM_NOT_AVAILABLE, 0);
        }
        final int price = getBuyPrice(item);
        if (player.getMoney() < price) {
            return new TransactionResults(TransactionOutcome.INSUFFICIENT_FUNDS, price);
        }
        int currentMoney = player.getMoney();
        int newMoney = currentMoney - price;
        player.setMoney(newMoney);
        inventoryController.addIngredient(item.getName(),item.getImagePath(),1,item.getType(),item.getPrice()); // Aggiunge 1 unità dell'ingrediente all'inventario
        return new TransactionResults(TransactionOutcome.SUCCESS, price);
    }

    @Override
    public TransactionResults sell(final Ingredient item) {

        if (!inventoryController.hasIngredient(item.getName())) {
            return new TransactionResults(TransactionOutcome.ITEM_NOT_OWNED, 0);
        }
        final int price = getSellPrice(item);
        inventoryController.removeIngredient(item.getName(), 1);// Rimuove 1 unità dell'ingrediente dall'inventario
        int currentMoney = player.getMoney();
        int newMoney = currentMoney + price;
        player.setMoney(newMoney);
        return new TransactionResults(TransactionOutcome.SUCCESS, price);
    }
}