package it.unibo.kikistore.engine.state;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import it.unibo.kikistore.controller.api.CraftingController;
import it.unibo.kikistore.controller.api.InputHandler;
import it.unibo.kikistore.controller.api.InventoryController;
import it.unibo.kikistore.controller.api.PlayerController;
import it.unibo.kikistore.controller.api.RecipeBookController;
import it.unibo.kikistore.controller.impl.CraftingControllerImpl;
import it.unibo.kikistore.engine.api.GameState;
import it.unibo.kikistore.engine.api.GameStateTransition;
import it.unibo.kikistore.model.inventory.api.GameCatalog;
import it.unibo.kikistore.model.inventory.api.Ingredient;
import it.unibo.kikistore.model.inventory.api.Recipe;
import it.unibo.kikistore.model.inventory.impl.IngredientImpl;
import it.unibo.kikistore.view.utility.SpriteManager;

/**
 * Crafting screen — backpack grid on the left, cauldron with 3 ingredient
 * slots on the right. States: SELECTING (pick ingredients, confirm brew),
 * BREWING (closed cauldron, timer), RESULT (potion or black potion + retry).
 */
public final class CraftingState implements GameState {

    private enum Phase {
        SELECTING, BREWING, RESULT
    }

    private static final int COLUMNS = 3;
    private static final int ROWS = 6;
    private static final int SLOTS = COLUMNS * ROWS;
    private static final int REQUIRED_INGREDIENTS = 3;
    private static final int BREW_DURATION_TICKS = 90; // ~1.5s a 60fps
    private static final int ENERGY_COST_PER_CRAFT = 1;

    private static final double SLOT_PADDING = 6.0;
    private static final double ITEM_PADDING = 6.0;
    private static final double BACKPACK_WIDTH_RATIO = 0.4;
    private static final double CAULDRON_SLOT_SIZE = 90.0;
    private static final Color COL_BG = Color.web("#c5925b");

    private static final Color COL_TEXT = Color.web("#3B2006");
    private static final Color COL_TEXT_ERROR = Color.web("#781a0d");
    private static final Color COL_TEXT_DIM = Color.web("#5C4A3A");
    private static final Color COL_CURSOR = Color.web("#e8da8a");
    private static final Color COL_SELECTED_BORDER = Color.web("#5fc6a6");

    private static final double TITLE_FONT_SIZE = 40.0;
    private static final double SMALL_FONT_SIZE = 20.0;

    private static final double BACKPACK_X = 20.0;
    private static final double BACKPACK_Y = 60.0;
    private static final double BACKPACK_WIDTH_MARGIN = 40.0;
    private static final double BACKPACK_HEIGHT_MARGIN = 80.0;
    private static final double CURSOR_LABEL_Y = 35.0;

    private static final double ITEM_QTY_BOTTOM_SPACE = 12.0;
    private static final double QTY_TEXT_OFFSET_X = 3.0;
    private static final double QTY_TEXT_OFFSET_Y = 2.0;
    private static final double BORDER_WIDTH = 3.0;
    private static final double BORDER_INSET = 1.0;
    private static final double BORDER_SIZE_REDUCTION = 2.0;

    private static final double TITLE_AREA_RATIO = 0.30;
    private static final double CAULDRON_MARGIN = 40.0;
    private static final double CAULDRON_SLOT_GAP = 16.0;
    private static final double CAULDRON_BELOW_OFFSET = 40.0;
    private static final double CAULDRON_ITEM_INSET = 10.0;
    private static final double CAULDRON_ITEM_SIZE_REDUCTION = 20.0;
    private static final double CAULDRON_BOTTOM_RESERVE = 90.0;

    // Centro dell'apertura d'acqua, misurato sullo sprite cauldron.png (128x128):
    // apertura a x=42 y=39, larghezza 70, altezza 55
    private static final double APERTURE_CENTER_X_FRAC = 63.0 / 128.0;
    private static final double APERTURE_CENTER_Y_FRAC = 48.0 / 128.0;

    private static final double RESULT_IMAGE_SIZE = 80.0;
    private static final double RESULT_TEXT_OFFSET_Y = 20.0;

    private static final double PROMPT_OFFSET_X = 40.0;
    private static final double PROMPT_OFFSET_Y = 26.0;

    private final InventoryController inventoryController;
    private final CraftingController craftingController;
    private final RecipeBookController recipeBookController;
    private final PlayerController player;
    private final GameCatalog gameCatalog;
    private final SpriteManager spriteManager;
    private final GameStateTransition gsm;
    private final InputHandler input;

    private final Font pixelFont;
    private final Font pixelFontSmall;
    private final ColorAdjust grayscale = new ColorAdjust();

    private List<Ingredient> allIngredients;
    private final List<Ingredient> selectedIngredients = new ArrayList<>();

    private int cursorIndex;
    private boolean askingBrewConfirm;
    private boolean brewYesSelected = true;

    private Phase phase = Phase.SELECTING;
    private int brewTimer;
    private boolean lastCraftSucceeded;
    private String lastResultImagePath;

    private boolean upWasPressed;
    private boolean downWasPressed;
    private boolean leftWasPressed;
    private boolean rightWasPressed;
    private boolean actionWasPressed;

    /**
     * @param inventoryController  inventory controller
     * @param recipeBookController recipe book controller
     * @param player               the player, whose energy is spent on each
     *                             succefful craft
     * @param gameCatalog          full item catalog
     * @param spriteManager        sprite manager
     * @param gsm                  game state manager
     * @param input                input handler
     */
    @SuppressFBWarnings(
        value = "EI_EXPOSE_REP2",
        justification = "Controllers and resources are injected on purpose and shared between game states"
    )
    public CraftingState(
            final InventoryController inventoryController,
            final RecipeBookController recipeBookController,
            final PlayerController player,
            final GameCatalog gameCatalog,
            final SpriteManager spriteManager,
            final GameStateTransition gsm,
            final InputHandler input) {
        this.inventoryController = inventoryController;
        this.recipeBookController = recipeBookController;
        this.player = player;
        this.gameCatalog = gameCatalog;
        this.spriteManager = spriteManager;
        this.gsm = gsm;
        this.input = input;
        this.grayscale.setSaturation(-1.0);

        this.craftingController = new CraftingControllerImpl(this.inventoryController, this.recipeBookController);

        final Font loadedTitle = Font.loadFont(
                getClass().getResourceAsStream("/fonts/PressStart2P.ttf"), TITLE_FONT_SIZE);
        this.pixelFont = loadedTitle != null ? loadedTitle : Font.font("Monospace", TITLE_FONT_SIZE);
        final Font loadedSmall = Font.loadFont(
                getClass().getResourceAsStream("/fonts/PressStart2P.ttf"), SMALL_FONT_SIZE);
        this.pixelFontSmall = loadedSmall != null ? loadedSmall : Font.font("Monospace", SMALL_FONT_SIZE);
    }

    @Override
    public void init() {
        phase = Phase.SELECTING;
        cursorIndex = 0;
        askingBrewConfirm = false;
        selectedIngredients.clear();
        buildIngredientList();
    }

    /** Rebuilds the backpack list with current inventory quantities. */
    private void buildIngredientList() {
        allIngredients = new ArrayList<>();
        for (final Ingredient ing : gameCatalog.getAllIngredients()) {
            final int qty = inventoryController.getIngredientQuantity(ing.getName());
            allIngredients.add(new IngredientImpl(ing.getName(), ing.getImagePath(), qty, ing.getType()));
        }
    }

    @Override
    public void update() {
        switch (phase) {
            case SELECTING:
                updateSelecting();
                break;
            case BREWING:
                updateBrewing();
                break;
            case RESULT:
                updateResult();
                break;
        }
    }

    private void updateSelecting() {
        if (input.isEscapePressed()) {
            gsm.popState();
            return;
        }

        if (askingBrewConfirm) {
            final boolean leftNow = input.isLeft();
            final boolean rightNow = input.isRight();
            if (leftNow && !leftWasPressed || rightNow && !rightWasPressed) {
                brewYesSelected = !brewYesSelected;
            }
            leftWasPressed = leftNow;
            rightWasPressed = rightNow;

            final boolean actionNow = input.isAction();
            if (actionNow && !actionWasPressed) {
                if (brewYesSelected) {
                    startBrewing();
                } else {
                    askingBrewConfirm = false;
                    brewYesSelected = true;
                }
            }
            actionWasPressed = actionNow;
            return;
        }

        // Movimento cursore nella griglia zaino
        final boolean upNow = input.isUp();
        if (upNow && !upWasPressed && cursorIndex - COLUMNS >= 0) {
            cursorIndex -= COLUMNS;
        }
        upWasPressed = upNow;

        final boolean downNow = input.isDown();
        if (downNow && !downWasPressed && cursorIndex + COLUMNS < SLOTS) {
            cursorIndex += COLUMNS;
        }
        downWasPressed = downNow;

        final boolean leftNow = input.isLeft();
        if (leftNow && !leftWasPressed && cursorIndex % COLUMNS > 0) {
            cursorIndex--;
        }
        leftWasPressed = leftNow;

        final boolean rightNow = input.isRight();
        if (rightNow && !rightWasPressed && cursorIndex % COLUMNS < COLUMNS - 1) {
            cursorIndex++;
        }
        rightWasPressed = rightNow;

        // Selezione/deselezione con action
        final boolean actionNow = input.isAction();
        if (actionNow && !actionWasPressed) {

            toggleSelection();
        }
        actionWasPressed = actionNow;
    }

    /** Selects or deselects the ingredient currently under the cursor. */
    private void toggleSelection() {
        if (player.getEnergy() <= 0) {
            return;
        }
        if (cursorIndex >= allIngredients.size()) {
            return;
        }
        final Ingredient target = allIngredients.get(cursorIndex);
        if (target.getQuantity() == 0) {
            return; // non selezionabile se non posseduto
        }

        final int alreadyIndex = indexOfSelected(target.getName());
        if (alreadyIndex >= 0) {
            selectedIngredients.remove(alreadyIndex);
            askingBrewConfirm = false;
            return;
        }

        if (selectedIngredients.size() < REQUIRED_INGREDIENTS) {
            selectedIngredients.add(target);
        }

        if (selectedIngredients.size() == REQUIRED_INGREDIENTS) {
            askingBrewConfirm = true;
            brewYesSelected = true;
        }
    }

    private int indexOfSelected(final String name) {
        for (int i = 0; i < selectedIngredients.size(); i++) {
            if (selectedIngredients.get(i).getName().equalsIgnoreCase(name)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Locks in the craft result now (before the brewing wait) and starts the timer.
     */
    private void startBrewing() {
        final Optional<Recipe> matched = recipeBookController.findByIngredients(selectedIngredients);
        lastCraftSucceeded = matched.isPresent();
        if (lastCraftSucceeded) {
            player.consumeEnergy(ENERGY_COST_PER_CRAFT);
        }
        lastResultImagePath = matched.map(r -> r.getPotion().getImagePath())
                .orElse("sprites/potions/black_potion");
        craftingController.craftPotion(selectedIngredients);
        phase = Phase.BREWING;
        brewTimer = 0;
        askingBrewConfirm = false;
    }

    private void updateBrewing() {
        brewTimer++;
        if (brewTimer >= BREW_DURATION_TICKS) {
            phase = Phase.RESULT;
            brewYesSelected = true;
        }
    }

    private void updateResult() {
        final boolean leftNow = input.isLeft();
        final boolean rightNow = input.isRight();
        if (leftNow && !leftWasPressed || rightNow && !rightWasPressed) {
            brewYesSelected = !brewYesSelected;
        }
        leftWasPressed = leftNow;
        rightWasPressed = rightNow;

        final boolean actionNow = input.isAction();
        if (actionNow && !actionWasPressed) {
            if (brewYesSelected) {
                selectedIngredients.clear();
                buildIngredientList();
                cursorIndex = 0;
                phase = Phase.SELECTING;
            } else {
                gsm.popState();
            }
        }
        actionWasPressed = actionNow;
    }

    @Override
    public void render(final GraphicsContext gc) {
        final double screenW = gc.getCanvas().getWidth();
        final double screenH = gc.getCanvas().getHeight();

        gc.save();
        gc.setImageSmoothing(false);

        gc.setFill(Color.rgb(0, 0, 0));
        gc.fillRect(0, 0, screenW, screenH);

        gc.setFill(COL_BG);
        gc.fillRect(0, 0, screenW, screenH);

        final double backpackW = screenW * BACKPACK_WIDTH_RATIO;
        final boolean interactive = phase == Phase.SELECTING;

        if (interactive) {
            renderCursorLabel(gc, BACKPACK_X);
        }

        renderBackpack(gc, BACKPACK_X, BACKPACK_Y, backpackW - BACKPACK_WIDTH_MARGIN,
                screenH - BACKPACK_HEIGHT_MARGIN, interactive);
        renderCauldronArea(gc, backpackW, 0, screenW - backpackW, screenH);
        gc.restore();

    }

    /**
     * Draws the backpack area — the cube background,
     * fitted inside the available space (aspect ratio preserved), then
     * the ingredient sprites drawn on top of it.
     *
     * @param gc          graphics context
     * @param x           backpack area x
     * @param y           backpack area y
     * @param w           backpack area width
     * @param h           backpack area height
     * @param interactive whether cursor/selection borders are shown
     */
    private void renderBackpack(final GraphicsContext gc, final double x, final double y,
            final double w, final double h, final boolean interactive) {
        final Image cubeSprite = spriteManager.getStaticSprite("sprites/ui_book/sells_full");
        if (cubeSprite == null) {
            return;
        }

        // Mantiene il rapporto originale dello sprite (96:144) dentro l'area
        // disponibile
        final double spriteAspect = cubeSprite.getWidth() / cubeSprite.getHeight();
        double drawW = w;
        double drawH = drawW / spriteAspect;
        if (drawH > h) {
            drawH = h;
            drawW = drawH * spriteAspect;
        }

        final double drawX = x + (w - drawW) / 2;
        final double drawY = y + (h - drawH) / 2;

        gc.drawImage(cubeSprite, drawX, drawY, drawW, drawH);
        renderItemsOnly(gc, drawX, drawY, drawW, drawH, interactive);
    }

    /**
     * Draws only the ingredient sprites, cursor, and selection borders
     * inside the exact area where the cube background was drawn.
     *
     * @param gc          graphics context
     * @param x           exact drawn background x
     * @param y           exact drawn background y
     * @param w           exact drawn background width
     * @param h           exact drawn background height
     * @param interactive whether cursor/selection borders are shown
     */
    private void renderItemsOnly(final GraphicsContext gc, final double x, final double y,
            final double w, final double h, final boolean interactive) {
        final double slotW = (w - SLOT_PADDING * (COLUMNS - 1)) / COLUMNS;
        final double slotH = (h - SLOT_PADDING * (ROWS - 1)) / ROWS;

        for (int i = 0; i < SLOTS && i < allIngredients.size(); i++) {
            final int col = i % COLUMNS;
            final int row = i / COLUMNS;
            final double sx = x + col * (slotW + SLOT_PADDING);
            final double sy = y + row * (slotH + SLOT_PADDING);

            final Ingredient item = allIngredients.get(i);
            final boolean isEmpty = item.getQuantity() == 0;
            final boolean isSelected = indexOfSelected(item.getName()) >= 0;

            final Image sprite = spriteManager.getStaticSprite(item.getImagePath());
            final double spriteX = sx + ITEM_PADDING;
            final double spriteY = sy + ITEM_PADDING;
            final double spriteW = slotW - ITEM_PADDING * 2;
            final double spriteH = slotH - ITEM_PADDING * 2 - ITEM_QTY_BOTTOM_SPACE;

            if (sprite != null) {
                final double spriteAspect = sprite.getWidth() / sprite.getHeight();
                double itemW = spriteW;
                double itemH = itemW / spriteAspect;
                if (itemH > spriteH) {
                    itemH = spriteH;
                    itemW = itemH * spriteAspect;
                }
                final double itemX = spriteX + (spriteW - itemW) / 2;
                final double itemY = spriteY + (spriteH - itemH) / 2;

                if (isEmpty) {
                    gc.setEffect(grayscale);
                }
                gc.drawImage(sprite, itemX, itemY, itemW, itemH);
                gc.setEffect(null);
            }

            gc.setFill(isEmpty ? COL_TEXT_DIM : COL_TEXT);
            gc.setFont(pixelFontSmall);
            gc.setTextAlign(TextAlignment.RIGHT);
            gc.fillText(String.valueOf(item.getQuantity()), sx + slotW - QTY_TEXT_OFFSET_X,
                    sy + slotH - QTY_TEXT_OFFSET_Y);

            if (interactive && isSelected) {
                gc.setStroke(COL_SELECTED_BORDER);
                gc.setLineWidth(BORDER_WIDTH);
                gc.strokeRect(sx + BORDER_INSET, sy + BORDER_INSET, slotW - BORDER_SIZE_REDUCTION,
                        slotH - BORDER_SIZE_REDUCTION);
            }

            if (interactive && i == cursorIndex) {
                gc.setStroke(COL_CURSOR);
                gc.setLineWidth(BORDER_WIDTH);
                gc.strokeRect(sx + BORDER_INSET, sy + BORDER_INSET, slotW - BORDER_SIZE_REDUCTION,
                        slotH - BORDER_SIZE_REDUCTION);
            }
        }
    }

    /**
     * Draws the name of the ingredient currently under the cursor, in the
     * margin above the backpack grid.
     *
     * @param gc graphics context
     * @param x  label x position
     */
    private void renderCursorLabel(final GraphicsContext gc, final double x) {
        if (cursorIndex >= allIngredients.size()) {
            return;
        }
        final Ingredient hovered = allIngredients.get(cursorIndex);

        gc.setFill(COL_TEXT);
        gc.setFont(pixelFontSmall);
        gc.setTextAlign(TextAlignment.LEFT);
        gc.fillText(hovered.getName(), x, CURSOR_LABEL_Y);
    }

    /**
     * Draws the cauldron with 3 ingredient slots, and the state-dependent
     * content below it (Brew Potion? / brewing timer / result).
     *
     * @param gc graphics context
     * @param x  area x
     * @param y  area y
     * @param w  area width
     * @param h  area height
     */
    private void renderCauldronArea(final GraphicsContext gc, final double x, final double y,
            final double w, final double h) {
        final double centerX = x + w / 2;

        // 30% superiore riservato al titolo, 70% inferiore al calderone
        final double titleAreaHeight = h * TITLE_AREA_RATIO;
        final double cauldronAreaY = y + titleAreaHeight;
        final double cauldronAreaHeight = h - titleAreaHeight;

        gc.setFill(COL_TEXT);
        gc.setFont(pixelFont);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText("CAULDRON", centerX, y + titleAreaHeight / 2);

        // Il calderone resta quadrato: la sua dimensione e' il minimo tra
        // larghezza e altezza disponibili nel 70% inferiore, con margine
        final double availableW = w - CAULDRON_MARGIN * 2;
        final double availableH = cauldronAreaHeight - CAULDRON_MARGIN - CAULDRON_BOTTOM_RESERVE;
        final double cauldronSize = Math.min(availableW, availableH);
        final double cauldronX = x + (w - cauldronSize) / 2;
        final double cauldronY = cauldronAreaY;

        renderCauldronSprite(gc, cauldronX, cauldronY, cauldronSize, cauldronSize);

        // Slot centrati sul centro esatto dell'apertura, dimensione invariata
        final double slotsTotalW = CAULDRON_SLOT_SIZE * REQUIRED_INGREDIENTS + CAULDRON_SLOT_GAP * 2;
        final double apertureCenterX = cauldronX + cauldronSize * APERTURE_CENTER_X_FRAC;
        final double apertureCenterY = cauldronY + cauldronSize * APERTURE_CENTER_Y_FRAC;
        final double slotsX = apertureCenterX - slotsTotalW / 2;
        final double slotsY = apertureCenterY - CAULDRON_SLOT_SIZE / 2;

        for (int i = 0; i < REQUIRED_INGREDIENTS; i++) {
            final double sx = slotsX + i * (CAULDRON_SLOT_SIZE + CAULDRON_SLOT_GAP);

            gc.setEffect(null);
            gc.setFill(Color.web("#a09482"));
            gc.fillRect(sx, slotsY, CAULDRON_SLOT_SIZE, CAULDRON_SLOT_SIZE);

            if (phase != Phase.BREWING && phase != Phase.RESULT && i < selectedIngredients.size()) {
                final Ingredient chosen = selectedIngredients.get(i);
                final Image sprite = spriteManager.getStaticSprite(chosen.getImagePath());
                if (sprite != null) {
                    gc.drawImage(sprite, sx + CAULDRON_ITEM_INSET, slotsY + CAULDRON_ITEM_INSET,
                            CAULDRON_SLOT_SIZE - CAULDRON_ITEM_SIZE_REDUCTION,
                            CAULDRON_SLOT_SIZE - CAULDRON_ITEM_SIZE_REDUCTION);
                }
            }
        }

        final double belowY = cauldronY + cauldronSize + CAULDRON_BELOW_OFFSET;

        switch (phase) {
            case SELECTING:
                if (player.getEnergy() <= 0) {
                    gc.setFill(COL_TEXT_ERROR);
                    gc.setFont(pixelFontSmall);
                    gc.setTextAlign(TextAlignment.CENTER);
                    gc.fillText("NO ENERGY LEFT TO CRAFT", centerX, belowY);
                } else if (askingBrewConfirm) {
                    renderYesNoPrompt(gc, centerX, belowY, "Brew potion?");
                }
                break;
            case BREWING:
                gc.setFill(COL_TEXT);
                gc.setFont(pixelFontSmall);
                gc.setTextAlign(TextAlignment.CENTER);
                gc.fillText("Brewing...", centerX, belowY);
                break;
            case RESULT:
                renderResult(gc, centerX, slotsY, belowY);
                break;
        }
    }

    /**
     * Draws the cauldron sprite filling the given area.
     * Falls back to a plain fill if the sprite is missing.
     *
     * @param gc    graphics context
     * @param areaX cauldron area x
     * @param areaY cauldron area y
     * @param areaW cauldron area width
     * @param areaH cauldron area height
     */
    private void renderCauldronSprite(final GraphicsContext gc, final double areaX, final double areaY,
            final double areaW, final double areaH) {
        final Image cauldronSprite = spriteManager.getStaticSprite("sprites/items/cauldron");

        if (cauldronSprite != null) {
            gc.drawImage(cauldronSprite, areaX, areaY, areaW, areaH);
        } else {
            gc.setFill(Color.web("#8B7355"));
            gc.fillRect(areaX, areaY, areaW, areaH);
        }
    }

    /**
     * @param gc      graphics context
     * @param centerX cauldron center x
     * @param slotsY  top of the ingredient slots (used to place the potion image)
     * @param belowY  y for the outcome text and retry prompt
     */
    private void renderResult(final GraphicsContext gc, final double centerX,
            final double slotsY, final double belowY) {
        final Image resultSprite = spriteManager.getStaticSprite(lastResultImagePath);

        if (resultSprite != null) {
            gc.drawImage(resultSprite, centerX - RESULT_IMAGE_SIZE / 2, slotsY, RESULT_IMAGE_SIZE, RESULT_IMAGE_SIZE);
        } else {
            gc.setFill(lastCraftSucceeded ? Color.web("#9AD1E8") : Color.web("#2A2A2A"));
            gc.fillRect(centerX - RESULT_IMAGE_SIZE / 2, slotsY, RESULT_IMAGE_SIZE, RESULT_IMAGE_SIZE);
        }

        gc.setFill(COL_TEXT);
        gc.setFont(pixelFontSmall);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText(
                lastCraftSucceeded ? "Potion added to inventory" : "Potion failed",
                centerX, belowY - RESULT_TEXT_OFFSET_Y);

        renderYesNoPrompt(gc, centerX, belowY, "Continue brewing?");
    }

    /**
     * Draws a "question / Yes No" prompt with the current selection highlighted.
     *
     * @param gc       graphics context
     * @param centerX  horizontal center for the prompt
     * @param y        y position of the question line
     * @param question the question text
     */
    private void renderYesNoPrompt(final GraphicsContext gc, final double centerX, final double y,
            final String question) {
        gc.setFill(COL_TEXT);
        gc.setFont(pixelFontSmall);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText(question, centerX, y);

        gc.setFont(pixelFontSmall);
        gc.setFill(brewYesSelected ? COL_SELECTED_BORDER : COL_TEXT_DIM);
        gc.fillText("Yes", centerX - PROMPT_OFFSET_X, y + PROMPT_OFFSET_Y);
        gc.setFill(!brewYesSelected ? COL_SELECTED_BORDER : COL_TEXT_DIM);
        gc.fillText("No", centerX + PROMPT_OFFSET_X, y + PROMPT_OFFSET_Y);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }
}
