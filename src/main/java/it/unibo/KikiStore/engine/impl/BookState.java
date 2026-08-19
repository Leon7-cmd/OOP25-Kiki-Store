package it.unibo.KikiStore.engine.impl;

import it.unibo.KikiStore.controller.api.InputHandler;
import it.unibo.KikiStore.controller.api.InventoryController;
import it.unibo.KikiStore.controller.api.OrderController;
import it.unibo.KikiStore.controller.api.RecipeBookController;
import it.unibo.KikiStore.engine.api.GameState;
import it.unibo.KikiStore.engine.api.GameStateManager;
import it.unibo.KikiStore.model.inventory.api.GameCatalog;
import it.unibo.KikiStore.view.book.BookAnimator;
import it.unibo.KikiStore.view.book.BookSection;
import it.unibo.KikiStore.view.book.InventorySection;
import it.unibo.KikiStore.view.book.OrdersSection;
import it.unibo.KikiStore.view.book.RecipeSection;
import it.unibo.KikiStore.view.utility.SpriteManager;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * The magic book — single game state hosting Inventory, Recipes,
 * and Orders sections. Always shown as two open facing pages.
 * Handles open/close animations and page-turn animation (for Recipes only).
 */
public class BookState implements GameState {

    private enum Section { INVENTORY, RECIPES, ORDERS }

    private enum Phase { OPENING, OPEN, CLOSING, CLOSED }

    private static final int OPEN_COLS = 4;
    private static final int OPEN_ROWS = 3;
    private static final int TURN_COLS = 4;
    private static final int TURN_ROWS = 4;
    private static final int BOOKMARK_SHEET_COLS = 2;
    private static final int BOOKMARK_SHEET_ROWS = 3;

    private static final double BOOK_WIDTH_RATIO = 0.75;
    private static final double BOOK_HEIGHT_RATIO = 0.85;
    private static final double BOOKMARK_X_FRAC = 245.0 / 272.0;
    private static final double BOOKMARK_HEIGHT_FRAC = 21.0 / 272.0;
    private static final double BOOKMARK_INV_Y_FRAC = 100.0 / 272.0;
    private static final double BOOKMARK_RCP_Y_FRAC = 120.0 / 272.0;
    private static final double BOOKMARK_ORD_Y_FRAC = 140.0 / 272.0;

    private final GameStateManager gsm;
    private final GameState previousState;
    private final InputHandler input;
    private final SpriteManager spriteManager;

    private final BookAnimator openAnimator;
    private final BookAnimator closeAnimator;
    private final BookAnimator turnLeftAnimator;
    private final BookAnimator turnRightAnimator;

    private final InventorySection inventorySection;
    private final RecipeSection recipeSection;
    private final OrdersSection ordersSection;

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

    //DA TOGLIERE
    private boolean upWasPressed;
    private boolean downWasPressed;

    /**
     * @param inventoryController inventory controller
     * @param recipeBookController recipe book controller
     * @param gameCatalog full item catalog
     * @param spriteManager sprite manager
     * @param gsm game state manager
     * @param previousState state to return to on close
     * @param input input handler
     */
    public BookState(
        final InventoryController inventoryController,
        final RecipeBookController recipeBookController,
        final OrderController orderController,
        final GameCatalog gameCatalog,
        final SpriteManager spriteManager,
        final GameStateManager gsm,
        final GameState previousState,
        final InputHandler input
    ) {
        this.gsm = gsm;
        this.previousState = previousState;
        this.input = input;
        this.spriteManager = spriteManager;
        this.grayscaleBookmark.setSaturation(-1.0);

        final Font loadedTitle = Font.loadFont(
            getClass().getResourceAsStream("/fonts/press_start_2p.ttf"), 12);
        this.pixelFont = loadedTitle != null ? loadedTitle : Font.font("Monospace", 12);
        final Font loadedSmall = Font.loadFont(
            getClass().getResourceAsStream("/fonts/press_start_2p.ttf"), 8);
        this.pixelFontSmall = loadedSmall != null ? loadedSmall : Font.font("Monospace", 8);

        this.openAnimator = new BookAnimator(spriteManager, "sprites/ui_book/Open_book", OPEN_COLS, OPEN_ROWS);
        this.closeAnimator = new BookAnimator(spriteManager, "sprites/ui_book/Close_book", OPEN_COLS, OPEN_ROWS);
        this.turnLeftAnimator = new BookAnimator(spriteManager, "sprites/ui_book/Turning_pages_right", TURN_COLS, TURN_ROWS);
        this.turnRightAnimator = new BookAnimator(spriteManager, "sprites/ui_book/Turning_pages_left", TURN_COLS, TURN_ROWS);

        this.inventorySection = new InventorySection(
            inventoryController, gameCatalog, spriteManager, pixelFontSmall);
        this.recipeSection = new RecipeSection(
            recipeBookController, spriteManager, pixelFont, pixelFontSmall);
        this.ordersSection = new OrdersSection(orderController, input, pixelFontSmall);//new OrdersSection(pixelFontSmall);
    }

    @Override
    public void init() {
        currentSection = Section.RECIPES;
        phase = Phase.OPENING;
        turningPage = false;
        openAnimator.play();
        inventorySection.refresh();
        recipeSection.refresh();
        ordersSection.refresh();
    }

    @Override
    public void update() {
        // TO-DO: sostituire con input.isEsc() quando lo aggiungo a InputHandler
        final boolean escNow = false;

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
                    gsm.setState(previousState);
                }
                break;

            case CLOSED:
                break;
            default:
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

        // TO-DO: click sui bookmark per cambiare sezione, quando aggiungo mouse click a inputhandler
        // if (input.isMouseClicked()) { ... currentSection = ... }

        //SOLUZIONE TEMP DA TOGLIEREEEEEE
        // Cambia sezione con UP/DOWN (temporaneo, finché non c'è il mouse)
        final boolean upNow = input.isUp();
        if (upNow && !upWasPressed) {
            currentSection = switch (currentSection) {
                case RECIPES -> Section.INVENTORY;
                case ORDERS -> Section.RECIPES;
                case INVENTORY -> Section.INVENTORY; 
            };
        }
        upWasPressed = upNow;

        final boolean downNow = input.isDown();
        if (downNow && !downWasPressed) {
            currentSection = switch (currentSection) {
                case INVENTORY -> Section.RECIPES;
                case RECIPES -> Section.ORDERS; 
                case ORDERS -> Section.ORDERS;
            };
        }
        downWasPressed = downNow;
}

    @Override
    public void render(final GraphicsContext gc) {
        final double screenW = gc.getCanvas().getWidth();
        final double screenH = gc.getCanvas().getHeight();

        gc.setFill(Color.rgb(0, 0, 0, 0.6));
        gc.fillRect(0, 0, screenW, screenH);

        final double bookSize = Math.min(screenW * BOOK_WIDTH_RATIO, screenH * BOOK_HEIGHT_RATIO);
        final double bookW = bookSize;
        final double bookH = bookSize;
        final double bookX = (screenW - bookW) / 2;
        final double bookY = (screenH - bookH) / 2;

        //System.out.println("screenW=" + screenW + " screenH=" + screenH
        //    + " bookW=" + bookW + " bookH=" + bookH + " bookY=" + bookY);
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
            default:
                break;
        }
    }

    private void renderOpenBook(final GraphicsContext gc, final double x, final double y,
                             final double w, final double h) {
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
            return;
        }

        
        final double contentX = x;
        final double contentY = y;
        final double contentW = w;
        final double contentH = h;

        getActiveSection().render(gc, contentX, contentY, contentW, contentH);
    }

    /**
     * Draws a single bookmark tab using the bookmark spritesheet.
     * Orders tab is grayed out and non-selectable (not yet implemented).
     *
     * @param gc graphics context
     * @param x bookmark x
     * @param y bookmark y
     * @param spriteCol column of this bookmark's icon in the sheet
     * @param spriteRow row of this bookmark's icon in the sheet
     * @param section which section this bookmark represents
     */
    private void renderBookmark(final GraphicsContext gc, final double x, final double y,
                                final double w, final double h,
                                final int spriteRow, final Section section) {
        final boolean isActive = currentSection == section;
        final boolean isEnabled = true;

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

    /** @return the currently active book section */
    private BookSection getActiveSection() {
        switch (currentSection) {
            case RECIPES:
                return recipeSection;
            case ORDERS:
                return ordersSection;
            case INVENTORY:
            default:
                return inventorySection;
        }
    }
}