package it.unibo.KikiStore.controller.impl;

import java.util.List;

import it.unibo.KikiStore.controller.api.InventoryController;
import it.unibo.KikiStore.controller.api.RecipeBookController;
import it.unibo.KikiStore.controller.api.ShopTradingController;
import it.unibo.KikiStore.model.economy.api.PotionPriceCalculator;
import it.unibo.KikiStore.model.economy.api.TransactionOutcome;
import it.unibo.KikiStore.model.economy.api.TransactionResults;
import it.unibo.KikiStore.model.inventory.api.GameCatalog;
import it.unibo.KikiStore.model.inventory.api.Potion;
import it.unibo.KikiStore.model.inventory.api.Recipe;
import it.unibo.KikiStore.model.player.api.Player;

/**
 * Implementazione di {@link ShopTradingController} specializzata per le pozioni.
 * Usata dallo Stand delle Pozioni: non conosce né tratta ingredienti.
 */
public final class PotionTradingControllerImpl implements ShopTradingController<Potion> {

    private final GameCatalog catalog;
    private final InventoryController inventoryController;
    private final PotionPriceCalculator priceCalculator;
    private final RecipeBookController recipeBookController;    
    private final Player player;

    /**
     * @param catalog catalogo condiviso da cui leggere le pozioni acquistabili
     * @param inventoryController controller condiviso dell'inventario del giocatore
     * @param recipeBookController controller condiviso del libro delle ricette
     * @param player il giocatore, per leggere/modificare le monete possedute
     */
    public PotionTradingControllerImpl(
            final GameCatalog catalog,
            final InventoryController inventoryController,
            final PotionPriceCalculator priceCalculator,
            final RecipeBookController recipeBookController,
            final Player player) {
        this.catalog = catalog;
        this.inventoryController = inventoryController;
        this.priceCalculator = priceCalculator;
        this.recipeBookController = recipeBookController;
        this.player = player;
    }

    @Override
    public List<Potion> getBuyableItems() {
        return catalog.getAllPotions();
    }

    @Override
    public List<Potion> getSellableItems() {
        final List<Potion> potions = inventoryController.getInventory().getPotions();
        System.out.println("DEBUG STAND POZIONI -> Totale pozioni trovate: " + potions.size());
        for (Potion p : potions) {
            System.out.println(" - " + p.getName() + " (qty: " + p.getQuantity() + ")");
        }
        return potions;
    }

    @Override
    public int getBuyPrice(final Potion item) {
        Recipe recipe = recipeBookController.findByName(item.getName());
        return priceCalculator.calculatePrice(recipe); // prezzo di acquisto = prezzo base della pozione, o usare la percentuale ..
    }


    @Override
    public int getSellPrice(final Potion item) {
        final Recipe recipe = recipeBookController.findByName(item.getName());
        if (recipe == null) {
            return 1;
        }
        return priceCalculator.calculatePrice(recipe); // 1:1 senza divisione
    }

    @Override
    public TransactionResults buy(final Potion item) {
        if (!catalog.getAllPotions().contains(item)) {
            return new TransactionResults(TransactionOutcome.ITEM_NOT_AVAILABLE, 0);
        }
        final int price = getBuyPrice(item);
        if (player.getMoney() < price) {
            return new TransactionResults(TransactionOutcome.INSUFFICIENT_FUNDS, price);
        }
        player.setMoney(player.getMoney() - price);
        inventoryController.getInventory().addPotion(item);
        return new TransactionResults(TransactionOutcome.SUCCESS, price);
    }

    @Override
    public TransactionResults sell(final Potion item) {
        if (!inventoryController.getInventory().getPotions().contains(item)) {
            return new TransactionResults(TransactionOutcome.ITEM_NOT_OWNED, 0);
        }
        final int price = getSellPrice(item);
        inventoryController.removePotion(item.getName(),1); // Rimuove 1 unità della pozione dall'inventario
        player.setMoney(player.getMoney() + price);
        return new TransactionResults(TransactionOutcome.SUCCESS, price);
    }
}