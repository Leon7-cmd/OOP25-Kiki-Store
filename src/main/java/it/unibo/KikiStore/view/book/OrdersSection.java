package it.unibo.KikiStore.view.book;

import java.util.List;

import it.unibo.KikiStore.controller.api.InputHandler;
import it.unibo.KikiStore.controller.api.OrderController;
import it.unibo.KikiStore.model.order.api.Dialogue;
import it.unibo.KikiStore.model.order.api.DialogueLine;
import it.unibo.KikiStore.model.order.api.Order;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

/**
 * Orders section — single scrolling list of all current orders (pending and ready
 * mixed together, since at any point they could all be one or the other).
 * Navigate with UP/DOWN, confirm the highlighted order with the action key.
 * Confirming opens a short linear dialogue overlay, built and driven by the
 * OrderController; this section only reads/advances it and draws it.
 */
public class OrdersSection implements BookSection {

    private static final int MAX_VISIBLE_ROWS = 5;
    private static final double ROW_HEIGHT = 34.0;
    private static final double ROW_PADDING = 6.0;

    private static final double PAGE_TOP_FRAC = 100.0 / 272.0;
    private static final double PAGE_BOTTOM_FRAC = 244.0 / 272.0;
    private static final double PAGE_LEFT_FRAC = 25.0 / 272.0;
    private static final double PAGE_RIGHT_FRAC = 250.0 / 272.0;

    private static final Color COL_TEXT = Color.web("#3B2006");
    private static final Color COL_MISSING = Color.web("#B23A2E");
    private static final Color COL_READY = Color.web("#2E7D32");
    private static final Color COL_SELECTED_BG = Color.web("#C8A96E");
    private static final Color COL_DIALOGUE_BG = Color.rgb(30, 20, 10, 0.85);
    private static final Color COL_DIALOGUE_SPEAKER = Color.web("#E0B84D");
    private static final Color COL_DIALOGUE_TEXT = Color.web("#F5EAD6");

    private final OrderController orderController;
    private final InputHandler input;
    private final Font pixelFontSmall;

    private List<Order> orders = List.of();
    private int selectedIndex;

    private boolean upWasPressed;
    private boolean downWasPressed;
    private boolean actionWasPressed;

    private boolean dialogueActive;
    private Order dialogueOrder;
    private Dialogue dialogue;

    /**
     * @param orderController controller used to read orders, resolve dialogue, and confirm them
     * @param input input handler, read directly since this section manages its own cursor/dialogue
     * @param pixelFontSmall pixel font used for rows and dialogue text
     */
    public OrdersSection(final OrderController orderController,
                          final InputHandler input,
                          final Font pixelFontSmall) {
        this.orderController = orderController;
        this.input = input;
        this.pixelFontSmall = pixelFontSmall;
        refresh();
    }

    /** Reloads the order list and resets the cursor. Call when reopening the book. */

    public void refresh() {
        orders = orderController.getOrders();
        selectedIndex = Math.min(selectedIndex, Math.max(0, orders.size() - 1));
        dialogueActive = false;
    }

    @Override
    public void update() {
        if (dialogueActive) {
            updateDialogue();
        } else {
            updateCursor();
        }
    }

    private void updateCursor() {
        if (orders.isEmpty()) {
            return;
        }

        final boolean upNow = input.isUp();
        if (upNow && !upWasPressed) {
            selectedIndex = Math.max(0, selectedIndex - 1);
        }
        upWasPressed = upNow;

        final boolean downNow = input.isDown();
        if (downNow && !downWasPressed) {
            selectedIndex = Math.min(orders.size() - 1, selectedIndex + 1);
        }
        downWasPressed = downNow;

        final boolean actionNow = input.isAction();
        if (actionNow && !actionWasPressed) {
            startDialogue(orders.get(selectedIndex));
        }
        actionWasPressed = actionNow;
    }

    private void updateDialogue() {
        final boolean actionNow = input.isAction();
        if (actionNow && !actionWasPressed) {
            if (dialogue.isFinished()) {
                orderController.confirmOrder(dialogueOrder);
                dialogueActive = false;
                refresh();
            } else {
                dialogue.advance();
            }
        }
        actionWasPressed = actionNow;
    }

    private void startDialogue(final Order order) {
        dialogueOrder = order;
        dialogue = orderController.getDialogueForOrder(order);
        dialogueActive = true;
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
            gc.fillText("No orders yet", pageX + pageW / 2, pageY + pageH / 2);
            return;
        }

        renderList(gc, pageX, pageY, pageW, pageH);

        if (dialogueActive) {
            renderDialogue(gc, x, y, w, h);
        }
    }

    private void renderList(final GraphicsContext gc, final double pageX, final double pageY,
                             final double pageW, final double pageH) {
        gc.setFont(pixelFontSmall);
        gc.setTextAlign(TextAlignment.LEFT);

        final int visibleCount = Math.min(orders.size(), MAX_VISIBLE_ROWS);
        for (int i = 0; i < visibleCount; i++) {
            final Order order = orders.get(i);
            final double rowY = pageY + i * (ROW_HEIGHT + ROW_PADDING);

            if (i == selectedIndex) {
                gc.setFill(COL_SELECTED_BG);
                gc.fillRect(pageX, rowY, pageW, ROW_HEIGHT);
            }

            final boolean ready = orderController.isOrderReady(order);
            final String customerName = order.getCustomer().getName();
            final String statusLabel = ready ? "READY" : "waiting...";
            final Color statusColor = ready ? COL_READY : COL_MISSING;

            gc.setFill(COL_TEXT);
            gc.setTextAlign(TextAlignment.LEFT);
            gc.fillText(customerName, pageX + 8, rowY + ROW_HEIGHT / 2 + 4);

            gc.setFill(statusColor);
            gc.setTextAlign(TextAlignment.RIGHT);
            gc.fillText(statusLabel, pageX + pageW - 8, rowY + ROW_HEIGHT / 2 + 4);
        }
    }

    private void renderDialogue(final GraphicsContext gc, final double x, final double y,
                                 final double w, final double h) {
        final double boxH = h * 0.28;
        final double boxY = y + h - boxH - 10;
        final double boxX = x + 10;
        final double boxW = w - 20;

        gc.setFill(COL_DIALOGUE_BG);
        gc.fillRoundRect(boxX, boxY, boxW, boxH, 12, 12);

        final DialogueLine line = dialogue.getCurrentLine();

        gc.setFont(pixelFontSmall);
        gc.setTextAlign(TextAlignment.LEFT);

        gc.setFill(COL_DIALOGUE_SPEAKER);
        gc.fillText(line.speaker() + ":", boxX + 14, boxY + 22);

        gc.setFill(COL_DIALOGUE_TEXT);
        gc.fillText(line.text(), boxX + 14, boxY + 42, boxW - 28);

        gc.setTextAlign(TextAlignment.RIGHT);
        gc.fillText("[E]", boxX + boxW - 14, boxY + boxH - 10);
        gc.setTextAlign(TextAlignment.LEFT);
    }
}