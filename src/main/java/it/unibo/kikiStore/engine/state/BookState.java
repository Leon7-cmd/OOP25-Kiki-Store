package it.unibo.kikiStore.engine.state;

import it.unibo.kikiStore.controller.api.InputHandler;
import it.unibo.kikiStore.controller.api.InventoryController;
import it.unibo.kikiStore.controller.api.RecipeBookController;
import it.unibo.kikiStore.engine.api.GameState;
import it.unibo.kikiStore.engine.api.GameStateTransition;
import it.unibo.kikiStore.model.inventory.api.GameCatalog;
import it.unibo.kikiStore.view.book.BookAnimator;
import it.unibo.kikiStore.view.book.BookSection;
import it.unibo.kikiStore.view.book.InventorySection;
import it.unibo.kikiStore.view.book.RecipeSection;
import it.unibo.kikiStore.view.utility.SpriteManager;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * The magic book - one single game state hosting Inventory, Recipes,
 * and Orders sections. Always shown as two open facing pages.
 * Handles open/close animations and page-turn animation (for Recipes only).
 */
public final class BookState implements GameState {

    private enum Section {
        INVENTORY, RECIPES, ORDERS
    }

    private enum Phase {
        OPENING, OPEN, CLOSING, CLOSED
    }

    private static final int OPEN_COLS = 4;
    private static final int OPEN_ROWS = 3;
    private static final int TURN_COLS = 4;
    private static final int TURN_ROWS = 4;
    private static final int BOOKMARK_SHEET_COLS = 2;
    private static final int BOOKMARK_SHEET_ROWS = 3;
    private static final double OVERLAY_OPACITY = 0.6;
    private static final double TITLE_FONT_SIZE = 12.0;
    private static final double SMALL_FONT_SIZE = 9.0;

    private static final double BOOK_WIDTH_RATIO = 0.75;
    private static final double BOOK_HEIGHT_RATIO = 0.85;
    private static final double BOOKMARK_X_FRAC = 245.0 / 272.0;
    private static final double BOOKMARK_HEIGHT_FRAC = 21.0 / 272.0;
    private static final double BOOKMARK_INV_Y_FRAC = 100.0 / 272.0;
    private static final double BOOKMARK_RCP_Y_FRAC = 120.0 / 272.0;
    private static final double BOOKMARK_ORD_Y_FRAC = 140.0 / 272.0;

    private static final double BOOK_ZOOM = 1.25; // 1.0 = normale, aumenta per ingrandire tutto
    private static final double ZOOM_VERTICAL_ANCHOR_RATIO = 1.10;

    private final GameStateTransition gsm;
    private final InputHandler input;
    private final SpriteManager spriteManager;

    private final BookAnimator openAnimator;
    private final BookAnimator closeAnimator;
    private final BookAnimator turnLeftAnimator;
    private final BookAnimator turnRightAnimator;

    private final InventorySection inventorySection;
    private final RecipeSection recipeSection;

    private final Font pixelFont;
    private final Font pixelFontSmall;

    private final ColorAdjust grayscaleBookmark = new ColorAdjust();

    private Section currentSection = Section.INVENTORY;
    private Phase phase = Phase.CLOSED;

    private boolean turningPage;
    private boolean turningRight;

    private boolean escWasPressed;
    private boolean rightWasPressed;
    private boolean leftWasPressed;

    // DA TOGLIERE
    private boolean upWasPressed;
    private boolean downWasPressed;

    /**
     * @param inventoryController  inventory controller
     * @param recipeBookController recipe book controller
     * @param gameCatalog          full item catalog
     * @param spriteManager        sprite manager
     * @param gsm                  game state manager
     * @param input                input handler
     */
    public BookState(
            final InventoryController inventoryController,
            final RecipeBookController recipeBookController,
            final GameCatalog gameCatalog,
            final SpriteManager spriteManager,
            final GameStateTransition gsm,
            final InputHandler input) {
        this.gsm = gsm;
        this.input = input;
        this.spriteManager = spriteManager;
        this.grayscaleBookmark.setSaturation(-1.0);

        final Font loadedTitle = Font.loadFont(
                getClass().getResourceAsStream("/fonts/PressStart2P.ttf"), TITLE_FONT_SIZE);
        this.pixelFont = loadedTitle != null ? loadedTitle : Font.font("Monospace", TITLE_FONT_SIZE);
        final Font loadedSmall = Font.loadFont(
                getClass().getResourceAsStream("/fonts/PressStart2P.ttf"), SMALL_FONT_SIZE);
        this.pixelFontSmall = loadedSmall != null ? loadedSmall : Font.font("Monospace", SMALL_FONT_SIZE);

        this.openAnimator = new BookAnimator(spriteManager, "sprites/ui_book/Open_book", OPEN_COLS, OPEN_ROWS);
        this.closeAnimator = new BookAnimator(spriteManager, "sprites/ui_book/Close_book", OPEN_COLS, OPEN_ROWS);
        this.turnLeftAnimator = new BookAnimator(spriteManager, "sprites/ui_book/Turning_pages_right", TURN_COLS,
                TURN_ROWS);
        this.turnRightAnimator = new BookAnimator(spriteManager, "sprites/ui_book/Turning_pages_left", TURN_COLS,
                TURN_ROWS);

        this.inventorySection = new InventorySection(
                inventoryController, gameCatalog, spriteManager, pixelFontSmall);
        this.recipeSection = new RecipeSection(
                recipeBookController, spriteManager, pixelFont, pixelFontSmall);
    }

    @Override
    public void init() {
        currentSection = Section.RECIPES;
        phase = Phase.OPENING;
        turningPage = false;
        openAnimator.play();
        inventorySection.refresh();
        recipeSection.refresh();
    }

    @Override
    public void update() {
        final boolean escNow = input.isEscapePressed();

        switch (phase) {
            case OPENING:
                openAnimator.update();
                if (openAnimator.isFinished()) {
                    phase = Phase.OPEN;
                }
                break;

            case OPEN:
                updateOpenPhase(escNow);
                break;

            case CLOSING:
                closeAnimator.update();
                if (closeAnimator.isFinished()) {
                    phase = Phase.CLOSED;
                    gsm.popState();
                }
                break;

            case CLOSED:
                break;
        }

        escWasPressed = escNow;
    }

    /**
     * Handles input while the book is fully open — closing, page turning
     * (Recipes only), and section-specific updates.
     *
     * @param escNow whether ESC is currently pressed
     */
    private void updateOpenPhase(final boolean escNow) {
        if (escNow && !escWasPressed) {
            phase = Phase.CLOSING;
            closeAnimator.play();
            return;
        }

        if (turningPage) {
            final BookAnimator activeTurn = turningRight ? turnRightAnimator : turnLeftAnimator;
            activeTurn.update();
            if (activeTurn.isFinished()) {
                turningPage = false;
                if (turningRight) {
                    recipeSection.goNext();
                } else {
                    recipeSection.goPrev();
                }
            }
            return;
        }

        if (currentSection == Section.RECIPES) {
            final boolean rightNow = input.isRight();
            if (rightNow && !rightWasPressed && recipeSection.canGoNext()) {
                turningPage = true;
                turningRight = true;
                turnRightAnimator.play();
            }
            rightWasPressed = rightNow;

            final boolean leftNow = input.isLeft();
            if (leftNow && !leftWasPressed && recipeSection.canGoPrev()) {
                turningPage = true;
                turningRight = false;
                turnLeftAnimator.play();
            }
            leftWasPressed = leftNow;
        } else {
            getActiveSection().update();
        }

        final boolean upNow = input.isUp();
        if (upNow && !upWasPressed) {
            currentSection = switch (currentSection) {
                case RECIPES -> Section.INVENTORY;
                case ORDERS -> Section.RECIPES;
                case INVENTORY -> Section.INVENTORY; // per ora non arriva a Orders, disabilitata
            };
        }
        upWasPressed = upNow;

        final boolean downNow = input.isDown();
        if (downNow && !downWasPressed) {
            currentSection = switch (currentSection) {
                case INVENTORY -> Section.RECIPES;
                case RECIPES -> Section.RECIPES; // per ora non arriva a Orders, disabilitata
                case ORDERS -> Section.ORDERS;
            };
        }
        downWasPressed = downNow;
    }

    @Override
    public void render(final GraphicsContext gc) {
        final double screenW = gc.getCanvas().getWidth();
        final double screenH = gc.getCanvas().getHeight();

        gc.setImageSmoothing(false);
        gc.setFill(Color.rgb(0, 0, 0, OVERLAY_OPACITY));
        gc.fillRect(0, 0, screenW, screenH);

        final double bookSize = Math.min(screenW * BOOK_WIDTH_RATIO, screenH * BOOK_HEIGHT_RATIO);
        final double bookW = bookSize;
        final double bookH = bookSize;
        final double bookX = (screenW - bookW) / 2;
        final double bookY = (screenH - bookH) / 2;

        gc.save();
        // Centro dello zoom = centro del libro
        final double zoomCenterX = bookX + bookW / 2;
        final double zoomCenterY = bookY + bookH * ZOOM_VERTICAL_ANCHOR_RATIO;
        gc.translate(zoomCenterX, zoomCenterY);
        gc.scale(BOOK_ZOOM, BOOK_ZOOM);
        gc.translate(-zoomCenterX, -zoomCenterY);

        switch (phase) {
            case OPENING:
                openAnimator.render(gc, bookX, bookY, bookW, bookH);
                break;

            case CLOSING:
                closeAnimator.render(gc, bookX, bookY, bookW, bookH);
                break;

            case OPEN:
                renderOpenBook(gc, bookX, bookY, bookW, bookH);
                break;

            case CLOSED:
                break;
        }

        gc.restore();
    }

    private void renderOpenBook(final GraphicsContext gc, final double x, final double y,
            final double w, final double h) {
        gc.save();

        openAnimator.render(gc, x, y, w, h);
        final double bookmarkAspect = 27.5 / 26.67;
        final double bookSize = w;
        final double bookmarkX = x + BOOKMARK_X_FRAC * bookSize;
        final double bookmarkH = BOOKMARK_HEIGHT_FRAC * bookSize;
        final double bookmarkW = bookmarkH * bookmarkAspect;

        renderBookmark(gc, bookmarkX, y + BOOKMARK_INV_Y_FRAC * bookSize,
                bookmarkW, bookmarkH, 0, Section.INVENTORY);
        renderBookmark(gc, bookmarkX, y + BOOKMARK_RCP_Y_FRAC * bookSize,
                bookmarkW, bookmarkH, 1, Section.RECIPES);
        renderBookmark(gc, bookmarkX, y + BOOKMARK_ORD_Y_FRAC * bookSize,
                bookmarkW, bookmarkH, 2, Section.ORDERS);

        if (turningPage) {
            final BookAnimator activeTurn = turningRight ? turnRightAnimator : turnLeftAnimator;
            activeTurn.render(gc, x, y, w, h);
            gc.restore();
            return;
        }

        final double contentX = x;
        final double contentY = y;
        final double contentW = w;
        final double contentH = h;

        getActiveSection().render(gc, contentX, contentY, contentW, contentH);

        gc.restore();
    }

    /**
     * Draws a single bookmark tab using the bookmark spritesheet.
     * Orders tab is grayed out and non-selectable (not yet implemented).
     *
     * @param gc        graphics context
     * @param x         bookmark x
     * @param y         bookmark y
     * @param w         bookmark width
     * @param h         bookmark height
     * @param spriteRow row of this bookmark's icon in the sheet
     * @param section   which section this bookmark represents
     */
    private void renderBookmark(final GraphicsContext gc, final double x, final double y,
            final double w, final double h,
            final int spriteRow, final Section section) {
        final boolean isActive = currentSection == section;
        final boolean isEnabled = section != Section.ORDERS;

        final Image sheet = spriteManager.getStaticSprite("sprites/ui_book/bookmarks");
        if (sheet != null) {
            final double frameW = sheet.getWidth() / BOOKMARK_SHEET_COLS;
            final double frameH = sheet.getHeight() / BOOKMARK_SHEET_ROWS;
            final int spriteCol = isActive ? 0 : 1; // colonna destra se selezionato
            final double sourceX = spriteCol * frameW;
            final double sourceY = spriteRow * frameH;

            if (!isEnabled) {
                gc.setEffect(grayscaleBookmark);
            }
            gc.drawImage(sheet, sourceX, sourceY, frameW, frameH, x, y, w, h);
            gc.setEffect(null);
        } else {
            gc.setFill(isEnabled ? Color.web("#5C3A1E") : Color.web("#8B7355"));
            gc.fillRect(x, y, w, h);
        }
    }

    /**
     * Returns the section currently shown to the player, based on
     * which bookmark tab is selected.
     *
     * @return the active book section
     */
    private BookSection getActiveSection() {
        switch (currentSection) {
            case RECIPES:
                return recipeSection;
            case ORDERS:
                // return ordersSection;
            case INVENTORY:
            default:
                return inventorySection;
        }
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }
}
