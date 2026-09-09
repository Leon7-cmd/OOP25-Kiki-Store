package it.unibo.KikiStore.view.book;

import java.util.List;

import it.unibo.KikiStore.controller.api.InputHandler;
import it.unibo.KikiStore.controller.api.OrderController;
import it.unibo.KikiStore.engine.api.GameStateTransition;
import it.unibo.KikiStore.engine.state.DialogueState;
import it.unibo.KikiStore.model.inventory.api.Recipe;
import it.unibo.KikiStore.model.order.api.Order;
import it.unibo.KikiStore.view.utility.SpriteManager;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

/**
 * Sezione del libro degli ordini.
 * Mostra una pagina per ordine, sfogliabile con frecce sinistra/destra.
 * Premendo il tasto azione [E] apre il DialogueState per interagire con il cliente.
 */
public final class OrdersSection implements BookSection {

    private static final double PAGE_TOP_FRAC = 105.0 / 272.0;
    private static final double PAGE_BOTTOM_FRAC = 244.0 / 272.0;
    private static final double PAGE_LEFT_FRAC = 32.0 / 272.0;
    private static final double PAGE_RIGHT_FRAC = 240.0 / 272.0;

    private static final Color COL_TITLE = Color.web("#3B2006");
    private static final Color COL_TEXT = Color.web("#5C4A3A");
    private static final Color COL_READY = Color.web("#2E7D32");
    private static final Color COL_WAITING = Color.web("#B23A2E");
    private static final Color COL_LOCKED = Color.web("#5e696a");

    private final OrderController orderController;
    private final InputHandler input;
    private final Font pixelFontSmall;
    private final GameStateTransition transitionController;
    private final SpriteManager spriteManager;

    private List<Order> orders = List.of();
    private int currentIndex;
    private boolean actionWasPressed;

    /**
     * @param orderController      controller per la gestione e verifica ordini
     * @param input                gestore input
     * @param pixelFontSmall       font pixel
     * @param transitionController gestore delle transizioni tra stati di gioco
     * @param spriteManager        gestore degli asset grafici
     */
    public OrdersSection(final OrderController orderController,
                         final InputHandler input,
                         final Font pixelFontSmall,
                         final GameStateTransition transitionController,
                         final SpriteManager spriteManager) {
        this.orderController = orderController;
        this.input = input;
        this.pixelFontSmall = pixelFontSmall;
        this.transitionController = transitionController;
        this.spriteManager = spriteManager;
        refresh();
    }

    /**
     * Ricarica gli ordini e garantisce che l'indice rimanga nei limiti validi.
     */
    public void refresh() {
        this.orders = orderController.getOrders();
        if (orders.isEmpty()) {
            currentIndex = 0;
        } else {
            currentIndex = Math.min(currentIndex, orders.size() - 1);
        }
    }

    public boolean canGoNext() {
        return currentIndex + 1 < orders.size();
    }

    public boolean canGoPrev() {
        return currentIndex > 0;
    }

    public void goNext() {
        if (canGoNext()) {
            currentIndex++;
        }
    }

    public void goPrev() {
        if (canGoPrev()) {
            currentIndex--;
        }
    }

    @Override
    public void update() {
        // Mantiene la lista sempre allineata con cancellazioni o completamenti
        refresh();

        final boolean actionNow = input.isAction();

        // Premendo [E] apre DialogueState per l'ordine corrente
        if (actionNow && !actionWasPressed && !orders.isEmpty() && currentIndex < orders.size()) {
            final Order selected = orders.get(currentIndex);
            transitionController.pushState(new DialogueState(
                selected,
                orderController,
                spriteManager,
                input,
                transitionController,
                pixelFontSmall
            ));
        }

        actionWasPressed = actionNow;
    }

    @Override
    public void render(final GraphicsContext gc, final double x, final double y,
                       final double w, final double h) {
        gc.setImageSmoothing(false);

        final double bookSize = w;
        final double pageX = x + PAGE_LEFT_FRAC * bookSize;
        final double pageY = y + PAGE_TOP_FRAC * bookSize;
        final double pageW = (PAGE_RIGHT_FRAC - PAGE_LEFT_FRAC) * bookSize;
        final double pageH = (PAGE_BOTTOM_FRAC - PAGE_TOP_FRAC) * bookSize;

        if (orders.isEmpty()) {
            gc.setFill(COL_TEXT);
            gc.setFont(pixelFontSmall);
            gc.setTextAlign(TextAlignment.CENTER);
            gc.fillText("No active orders", pageX + pageW / 2, pageY + pageH / 2);
            return;
        }

        renderOrderPage(gc, orders.get(currentIndex), pageX, pageY, pageW, pageH);
    }

    private void renderOrderPage(final GraphicsContext gc, final Order order,
                                 final double px, final double py,
                                 final double pw, final double ph) {
        gc.setFont(pixelFontSmall);

        // Header: Cliente e numero pagina
        gc.setFill(COL_TITLE);
        gc.setTextAlign(TextAlignment.LEFT);
        gc.fillText("Client: " + order.getCustomer().getName(), px + 10, py + 20);

        gc.setTextAlign(TextAlignment.RIGHT);
        gc.fillText("(" + (currentIndex + 1) + "/" + orders.size() + ")", px + pw - 10, py + 20);

        // Testo richiesta del cliente
        gc.setFill(COL_TEXT);
        gc.setTextAlign(TextAlignment.LEFT);
        gc.fillText("\"" + order.getRequest().getDialogue() + "\"", px + 10, py + 50, pw - 20);

        // Ricompensa in monete
        final int price = orderController.getPriceForOrder(order);
        gc.setFill(COL_TITLE);
        gc.fillText("Reward: " + price + " coins", px + 10, py + 85);

        // Risoluzione stato e blocco ricetta
        final Recipe recipe = orderController.getRecipeForOrder(order);
        final boolean isReady = orderController.isOrderReady(order);
        final boolean isLocked = recipe != null && !recipe.isUnlocked();

        final String statusMsg;
        final Color statusColor;

        if (isReady) {
            statusMsg = "[ READY - Press E to complete ]";
            statusColor = COL_READY;
        } else if (isLocked) {
            statusMsg = "[ LOCKED - Recipe unknown (E to reject) ]";
            statusColor = COL_LOCKED;
        } else {
            statusMsg = "[ IN PROGRESS - Press E for details ]";
            statusColor = COL_WAITING;
        }

        gc.setFill(statusColor);
        gc.fillText(statusMsg, px + 10, py + 115);
        
        // Guida tasti
        gc.setFill(COL_TEXT);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText("< [LEFT]  --  [RIGHT] >", px + pw / 2, py + ph - 10);
    }
}