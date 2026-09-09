package it.unibo.KikiStore.engine.state;

import it.unibo.KikiStore.controller.api.InputHandler;
import it.unibo.KikiStore.controller.api.ShopTradingController;
import it.unibo.KikiStore.engine.api.GameState;
import it.unibo.KikiStore.engine.api.GameStateTransition;
import it.unibo.KikiStore.model.economy.api.TransactionResults;
import it.unibo.KikiStore.model.item.api.GameItem;
import it.unibo.KikiStore.view.shop.impl.ShopTradingRenderer;
import it.unibo.KikiStore.view.utility.SpriteManager;
import javafx.scene.canvas.GraphicsContext;

/**
 * Stato che gestisce la schermata di compravendita di uno stand del negozio.
 * Generico su T (Potion o Ingredient): ogni stand fornisce il proprio
 * {@link ShopTradingController} già specializzato sul tipo corretto,
 * così questo State non ha mai bisogno di distinguere le categorie a runtime.
 *
 * @param <T> il tipo di item scambiato in questo stand
 */
public final class ShopTradingState<T extends GameItem> implements GameState {

    private static final String GREETING = "Ciao! Hai bisogno di qualche ingrediente o pozione per il tuo viaggio?";

    private final ShopTradingController<T> shopTradingController;
    private final GameStateTransition transitionController;
    private final GameState previousState;
    private final InputHandler input;
    private final ShopTradingRenderer renderer;

    private boolean isBuyingTab = true; // true = COMPRA, false = VENDI
    private int selectedIndex = 0;
    private String currentDialogueText = GREETING;

    private boolean upWasPressed;
    private boolean downWasPressed;
    private boolean actionWasPressed;
    private boolean tabWasPressed;   

    /**
     * @param shopTradingController il controller già specializzato (Potion o Ingredient) per questo stand
     * @param spriteManager gestore sprite condiviso, usato dal renderer
     * @param transitionController per uscire dallo stato (ESC/pop)
     * @param previousState lo stato sottostante, ridisegnato dietro l'overlay
     * @param input gestore input condiviso
     */
    public ShopTradingState(
            final ShopTradingController<T> shopTradingController,
            final SpriteManager spriteManager,
            final GameStateTransition transitionController,
            final GameState previousState,
            final InputHandler input) {
        this.shopTradingController = shopTradingController;
        this.transitionController = transitionController;
        this.previousState = previousState;
        this.input = input;
        this.renderer = new ShopTradingRenderer(spriteManager);
    }

    @Override
    public void init() { }

    @Override
    public void update() {
        // Uscita dallo shop con ESC o tasto dedicato
        if (input.isCancel()) {
            transitionController.popState();
            return;
        }

        // Cambio Tab tra COMPRA e VENDI

        final boolean tabNow = input.isTab();
        if (tabNow && !tabWasPressed) {
            isBuyingTab = !isBuyingTab;
            selectedIndex = 0;
        }
        tabWasPressed = tabNow;

        final int listSize = getCurrentList().size();

        // 2. Navigazione lista con UP e DOWN a singolo scatto (stile OrdersSection)
        if (listSize > 0) {
            final boolean upNow = input.isUp();
            if (upNow && !upWasPressed) {
                selectedIndex = Math.max(0, selectedIndex - 1);
            }
            upWasPressed = upNow;

            final boolean downNow = input.isDown();
            if (downNow && !downWasPressed) {
                selectedIndex = Math.min(listSize - 1, selectedIndex + 1);
            }
            downWasPressed = downNow;
        }

        // 3. Esecuzione transazione con 'E' a singolo scatto (compra o vendi in base alla tab)
        final boolean actionNow = input.isAction();
        if (actionNow && !actionWasPressed) {
            executeTransaction();
        }
        actionWasPressed = actionNow;
    
    }

    private java.util.List<T> getCurrentList() {
        return isBuyingTab ? shopTradingController.getBuyableItems() : shopTradingController.getSellableItems();
    }

    private void executeTransaction() {
        final java.util.List<T> items = getCurrentList();
        if (items.isEmpty() || selectedIndex >= items.size()) {
            return;
        }
        final T selectedItem = items.get(selectedIndex);

        final TransactionResults result = isBuyingTab
                ? shopTradingController.buy(selectedItem)
                : shopTradingController.sell(selectedItem);

        currentDialogueText = feedbackFor(result, selectedItem);

        // Dopo una vendita/acquisto riuscito la lista può ridursi: riallinea l'indice.
        if (result.isSuccess()) {
            final int newSize = getCurrentList().size();
            selectedIndex = Math.max(0, Math.min(selectedIndex, newSize - 1));
        }
    }

    private String feedbackFor(final TransactionResults result, final T item) {
        return switch (result.outcome()) {
            case SUCCESS -> isBuyingTab
                    ? "Hai acquistato " + item.getName() + " per " + result.price() + " euro!"
                    : "Hai venduto " + item.getName() + " per " + result.price() + " euro!";
            case INSUFFICIENT_FUNDS -> "Non hai abbastanza monete per questo acquisto...";
            case ITEM_NOT_AVAILABLE -> "Questo oggetto non è più disponibile.";
            case ITEM_NOT_OWNED -> "Non possiedi questo oggetto.";
        };
    }

    @Override
    public void render(final GraphicsContext gc) {
        // 1. Rende prima lo stato precedente (la mappa sotto rimane visibile sfocata/oscurata)
        if (previousState != null) {
            previousState.render(gc);
        }

        // 2. Disegna la UI divisa (Sinistra: Lista, Destra: sprite/nome/prezzo, Sotto: dialogo)
        renderer.render(gc, shopTradingController, isBuyingTab, selectedIndex, currentDialogueText);
    }
}