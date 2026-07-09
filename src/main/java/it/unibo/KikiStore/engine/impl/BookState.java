package it.unibo.KikiStore.engine.impl;

import it.unibo.KikiStore.controller.api.InputHandler;
import it.unibo.KikiStore.controller.api.InventoryController;
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
import javafx.scene.text.TextAlignment;

/**
 * The magic book — single game state hosting Inventory, Recipes,
 * and Orders sections. Always shown as two open facing pages.
 * Handles open/close animations and page-turn animation (for Recipes only).
 */
public class BookState implements GameState {

    private enum Section { INVENTORY, RECIPES, ORDERS }

    private enum Phase { OPENING, OPEN, CLOSING, CLOSED }

    // TODO calibra questi valori in base ai tuoi sprite reali-> da modificare in scala per dimensione spritesheets e frames
    private static final int OPEN_COLS = 4;
    private static final int OPEN_ROWS = 3;
    private static final int TURN_COLS = 4;
    private static final int TURN_ROWS = 2;
    private static final int BOOKMARK_SHEET_COLS = 4;
    private static final int BOOKMARK_SHEET_ROWS = 3;

    private static final double BOOK_WIDTH_RATIO = 0.75;
    private static final double BOOK_HEIGHT_RATIO = 0.85;
    private static final double BOOKMARK_WIDTH = 40.0;
    private static final double BOOKMARK_HEIGHT = 90.0;
    private static final double BOOKMARK_GAP = 10.0;
    private static final double CONTENT_MARGIN_X = 35.0;
    private static final double CONTENT_MARGIN_Y = 55.0;

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
        this.turnLeftAnimator = new BookAnimator(spriteManager, "sprites/ui_book/Turning_pages_left", TURN_COLS, TURN_ROWS);
        this.turnRightAnimator = new BookAnimator(spriteManager, "sprites/ui_book/Turning_pages_right", TURN_COLS, TURN_ROWS);

        this.inventorySection = new InventorySection(
            inventoryController, gameCatalog, spriteManager, pixelFontSmall);
        this.recipeSection = new RecipeSection(
            recipeBookController, spriteManager, pixelFont, pixelFontSmall);
        this.ordersSection = null;//new OrdersSection(pixelFontSmall);
    }

    @Override
    public void init() {
        currentSection = Section.INVENTORY;
        phase = Phase.OPENING;
        turningPage = false;
        openAnimator.play();
        inventorySection.refresh();
        recipeSection.refresh();
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

        System.out.println("screenW=" + screenW + " screenH=" + screenH
            + " bookW=" + bookW + " bookH=" + bookH + " bookY=" + bookY);
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

    /**
     * Renders the fully open book: background, bookmarks, and the active
     * section's content spread across both facing pages.
     *
     * @param gc graphics context
     * @param x book left x
     * @param y book top y
     * @param w book width
     * @param h book height
     */
    private void renderOpenBook(final GraphicsContext gc, final double x, final double y,
                                 final double w, final double h) {
        openAnimator.render(gc, x, y, w, h);
        //tutto da rifare il render dei bookmark, crea sprite nuovi con diversa risoluzione
        renderBookmark(gc, x + w - 8, y + 20, 0, 0, Section.INVENTORY);
        renderBookmark(gc, x + w - 8, y + 20 + BOOKMARK_HEIGHT + BOOKMARK_GAP, 0, 1, Section.RECIPES);
        renderBookmark(gc, x + w - 8, y + 20 + (BOOKMARK_HEIGHT + BOOKMARK_GAP) * 2, 0, 2, Section.ORDERS);

        if (turningPage) {
            final BookAnimator activeTurn = turningRight ? turnRightAnimator : turnLeftAnimator;
            activeTurn.render(gc, x, y, w, h);
            return;
        }

        // Tutte le sezioni occupano l'intero spread a due pagine —
        // ogni sezione gestisce internamente la divisione sinistra/destra
        final double contentX = x + CONTENT_MARGIN_X;
        final double contentY = y + CONTENT_MARGIN_Y;
        final double contentW = w - CONTENT_MARGIN_X * 2 - BOOKMARK_WIDTH;
        final double contentH = h - CONTENT_MARGIN_Y * 2;

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
                                 final int spriteCol, final int spriteRow, final Section section) {
        final boolean isActive = currentSection == section;
        final boolean isEnabled = section != Section.ORDERS;

        final Image sheet = spriteManager.getStaticSprite("sprites/ui_book/bookmarks");
        if (sheet != null) {
            final double frameW = sheet.getWidth() / BOOKMARK_SHEET_COLS;
            final double frameH = sheet.getHeight() / BOOKMARK_SHEET_ROWS;
            final double sourceX = spriteCol * frameW;
            final double sourceY = spriteRow * frameH;

            if (!isEnabled) {
                gc.setEffect(grayscaleBookmark);
            }
            gc.drawImage(sheet, sourceX, sourceY, frameW, frameH,
                x, y, BOOKMARK_WIDTH, BOOKMARK_HEIGHT);
            gc.setEffect(null);

            if (isActive) {
                gc.setStroke(Color.web("#FFD700"));
                gc.setLineWidth(2);
                gc.strokeRect(x, y, BOOKMARK_WIDTH, BOOKMARK_HEIGHT);
            }
        } else {
            gc.setFill(isEnabled ? Color.web("#5C3A1E") : Color.web("#8B7355"));
            gc.fillRect(x, y, BOOKMARK_WIDTH, BOOKMARK_HEIGHT);
            gc.setFill(Color.WHITE);
            gc.setFont(pixelFontSmall);
            gc.setTextAlign(TextAlignment.CENTER);
            gc.fillText(section.name().substring(0, 3),
                x + BOOKMARK_WIDTH / 2, y + BOOKMARK_HEIGHT / 2);
        }
    }

    /** @return the currently active book section */
    private BookSection getActiveSection() {
        switch (currentSection) {
            case RECIPES:
                return recipeSection;
            case ORDERS:
                //return ordersSection;
            case INVENTORY:
            default:
                return inventorySection;
        }
    }
}