package it.unibo.KikiStore.engine.state;


import java.util.List;

import it.unibo.KikiStore.controller.api.InputHandler;
import it.unibo.KikiStore.controller.api.OrderController;
import it.unibo.KikiStore.engine.api.GameState;
import it.unibo.KikiStore.engine.api.GameStateTransition;
import it.unibo.KikiStore.model.inventory.api.Ingredient;
import it.unibo.KikiStore.model.inventory.api.Recipe;
import it.unibo.KikiStore.model.order.api.Dialogue;
import it.unibo.KikiStore.model.order.api.DialogueLine;
import it.unibo.KikiStore.model.order.api.Order;
import it.unibo.KikiStore.view.utility.SpriteManager;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

/**
 * State that handles the customer dialogue overlay and recipe details for an order.
 */
public final class DialogueState implements GameState {

    private static final Color COL_OVERLAY = Color.rgb(0, 0, 0, 0.65);
    private static final Color COL_CARD_BG = Color.web("#F5EAD6");
    private static final Color COL_CARD_BORDER = Color.web("#5C3A1E");
    private static final Color COL_TITLE = Color.web("#3B2006");
    private static final Color COL_TEXT = Color.web("#5C4A3A");
    private static final Color COL_WARN = Color.web("#B23A2E");
    private static final Color COL_READY = Color.web("#2E7D32");

    private static final Color COL_DIALOGUE_BG = Color.rgb(25, 18, 12, 0.95);
    private static final Color COL_SPEAKER = Color.web("#E0B84D");
    private static final Color COL_DIALOGUE_TEXT = Color.web("#F5EAD6");

    private final Order order;
    private final OrderController orderController;
    private final SpriteManager spriteManager;
    private final InputHandler input;
    private final GameStateTransition transitionController;
    private final Font pixelFontSmall;

    private final Dialogue dialogue;
    private boolean actionWasPressed;
    private boolean cancelWasPressed;

    /**
     * @param order the target order
     * @param orderController controller to check and complete order
     * @param spriteManager sprite cache
     * @param input user input
     * @param transitionController state transition manager
     * @param pixelFontSmall font for texts
     */
    public DialogueState(final Order order,
                         final OrderController orderController,
                         final SpriteManager spriteManager,
                         final InputHandler input,
                         final GameStateTransition transitionController,
                         final Font pixelFontSmall) {
        this.order = order;
        this.orderController = orderController;
        this.spriteManager = spriteManager;
        this.input = input;
        this.transitionController = transitionController;
        this.pixelFontSmall = pixelFontSmall;
        this.dialogue = orderController.getDialogueForOrder(order);
    }

    @Override
    public void init() {
        actionWasPressed = true; // Evita attivazioni accidentali al cambio stato
        cancelWasPressed = true;
    }

    @Override
    public void update() {
        final boolean cancelNow = input.isCancel();
        if (cancelNow && !cancelWasPressed) {
            transitionController.popState();
            return;
        }
        cancelWasPressed = cancelNow;

        final boolean actionNow = input.isAction();
        if (actionNow && !actionWasPressed) {
            if (dialogue.isFinished()) {
                orderController.confirmOrder(order);
                transitionController.popState();
                return;
            }
            dialogue.advance();
        }
        actionWasPressed = actionNow;
    }

    @Override
    public void render(final GraphicsContext gc) {
        final double screenW = gc.getCanvas().getWidth();
        final double screenH = gc.getCanvas().getHeight();

        // 1. Sfondo scuro semitrasparente
        gc.setFill(COL_OVERLAY);
        gc.fillRect(0, 0, screenW, screenH);

        // 2. Scheda centrale dell'ordine
        final double cardW = Math.min(screenW * 0.8, 380);
        final double cardH = 260;
        final double cardX = (screenW - cardW) / 2;
        final double cardY = (screenH - cardH) / 2 - 30;

        gc.setFill(COL_CARD_BG);
        gc.fillRoundRect(cardX, cardY, cardW, cardH, 12, 12);
        gc.setStroke(COL_CARD_BORDER);
        gc.setLineWidth(3);
        gc.strokeRoundRect(cardX, cardY, cardW, cardH, 12, 12);

        final Recipe recipe = orderController.getRecipeForOrder(order);
        final boolean isReady = orderController.isOrderReady(order);

        renderOrderDetails(gc, recipe, isReady, cardX, cardY, cardW);

        // 3. Fumetto del dialogo in basso
        final double boxW = cardW;
        final double boxH = 75;
        final double boxX = cardX;
        final double boxY = cardY + cardH - boxH - 10;

        gc.setFill(COL_DIALOGUE_BG);
        gc.fillRoundRect(boxX, boxY, boxW, boxH, 8, 8);

        final DialogueLine line = dialogue.getCurrentLine();
        gc.setFont(pixelFontSmall);
        gc.setFill(COL_SPEAKER);
        gc.setTextAlign(TextAlignment.LEFT);
        gc.fillText(line.speaker() + ":", boxX + 12, boxY + 20);

        gc.setFill(COL_DIALOGUE_TEXT);
        gc.fillText(line.text(), boxX + 12, boxY + 38, boxW - 24);

        gc.setTextAlign(TextAlignment.RIGHT);
        final String prompt = dialogue.isFinished() ? "[E] Finish  [ESC] Back" : "[E] Next";
        gc.fillText(prompt, boxX + boxW - 12, boxY + boxH - 8);
    }

    private void renderOrderDetails(final GraphicsContext gc, final Recipe recipe,
                                    final boolean isReady, final double x, final double y, final double w) {
        gc.setFont(pixelFontSmall);
        gc.setTextAlign(TextAlignment.LEFT);

        // Intestazione cliente
        gc.setFill(COL_TITLE);
        gc.fillText("Customer: " + order.getCustomer().getName(), x + 16, y + 26);

        if (isReady) {
            gc.setFill(COL_READY);
            gc.setTextAlign(TextAlignment.CENTER);
            gc.fillText("✔ Ready for delivery!", x + w / 2, y + 60);
        } else if (recipe != null && recipe.isUnlocked()) {
            // Scheda con ingredienti solo se la ricetta è sbloccata
            final Image img = spriteManager.getStaticSprite(recipe.getPotion().getImagePath());
            if (img != null) {
                gc.drawImage(img, x + 16, y + 42, 32, 32);
            }
            gc.setFill(COL_TITLE);
            gc.fillText(recipe.getPotion().getName(), x + 56, y + 60);

            double curY = y + 88;
            gc.setFill(COL_TEXT);
            gc.fillText("Required ingredients:", x + 16, curY);

            final List<Ingredient> ingredients = recipe.getIngredients();
            if (ingredients != null) {
                for (final Ingredient ing : ingredients) {
                    curY += 16;
                    gc.fillText("• " + ing.getQuantity() + "x " + ing.getName(), x + 24, curY);
                }
            }
        } else {
            // Ricetta bloccata: nessun dettaglio
            gc.setFill(COL_WARN);
            gc.setTextAlign(TextAlignment.CENTER);
            gc.fillText("Formula Unknown (Recipe Locked)", x + w / 2, y + 70);
        }
    }
}
