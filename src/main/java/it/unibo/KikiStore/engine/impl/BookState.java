package it.unibo.KikiStore.engine.impl;

import it.unibo.KikiStore.controller.api.InputHandler;
import it.unibo.KikiStore.controller.api.InventoryController;
import it.unibo.KikiStore.controller.api.OrderController;
import it.unibo.KikiStore.controller.api.RecipeBookController;
import it.unibo.KikiStore.engine.api.GameState;
import it.unibo.KikiStore.engine.api.GameStateManager;
import it.unibo.KikiStore.engine.api.GameStateTransition;
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
 * Handles open/close animations and page-turn animation.
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
    private static final double SMALL_FONT_SIZE = 8.0;

    private static final double BOOK_WIDTH_RATIO = 0.75;
    private static final double BOOK_HEIGHT_RATIO = 0.85;
    private static final double BOOKMARK_X_FRAC = 245.0 / 272.0;
    private static final double BOOKMARK_HEIGHT_FRAC = 21.0 / 272.0;
    private static final double BOOKMARK_INV_Y_FRAC = 100.0 / 272.0;
    private static final double BOOKMARK_RCP_Y_FRAC = 120.0 / 272.0;
    private static final double BOOKMARK_ORD_Y_FRAC = 140.0 / 272.0;

    private final GameStateManager gsm;
    private final GameStateTransition transitionController;
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
    private boolean upWasPressed;
    private boolean downWasPressed;

    /**
     * @param inventoryController   inventory controller
     * @param recipeBookController  recipe book controller
     * @param orderController       order controller
     * @param gameCatalog           full item catalog
     * @param spriteManager         sprite manager
     * @param gsm                   game state manager
     * @param previousState         state to return to on close
     * @param input                 input handler
     * @param transitionController  state transition controller
     */
    public BookState(
            final InventoryController inventoryController,
            final RecipeBookController recipeBookController,
            final OrderController orderController,
            final GameCatalog gameCatalog,
            final SpriteManager spriteManager,
            final GameStateManager gsm,
            final GameState previousState,
            final InputHandler input,
            final GameStateTransition transitionController
    ) {
        this.gsm = gsm;
        this.previousState = previousState;
        this.input = input;
        this.transitionController = transitionController;
        this.spriteManager = spriteManager;
        this.grayscaleBookmark.setSaturation(-1.0);

        final Font loadedTitle = Font.loadFont(
                getClass().getResourceAsStream("/fonts/press_start_2p.ttf"), TITLE_FONT_SIZE);
        this.pixelFont = loadedTitle != null ? loadedTitle : Font.font("Monospace", TITLE_FONT_SIZE);
        final Font loadedSmall = Font.loadFont(
                getClass().getResourceAsStream("/fonts/press_start_2p.ttf"), SMALL_FONT_SIZE);
        this.pixelFontSmall = loadedSmall != null ? loadedSmall : Font.font("Monospace", SMALL_FONT_SIZE);

        this.openAnimator = new BookAnimator(spriteManager, "sprites/ui_book/Open_book", OPEN_COLS, OPEN_ROWS);
        this.closeAnimator = new BookAnimator(spriteManager, "sprites/ui_book/Close_book", OPEN_COLS, OPEN_ROWS);
        this.turnLeftAnimator = new BookAnimator(spriteManager, "sprites/ui_book/Turning_pages_right", TURN_COLS, TURN_ROWS);
        this.turnRightAnimator = new BookAnimator(spriteManager, "sprites/ui_book/Turning_pages_left", TURN_COLS, TURN_ROWS);

        this.inventorySection = new InventorySection(
                inventoryController, gameCatalog, spriteManager, pixelFontSmall);
        this.recipeSection = new RecipeSection(
                recipeBookController, spriteManager, pixelFont, pixelFontSmall);
        this.ordersSection = new OrdersSection(
    orderController,
    input,
    pixelFontSmall,
    transitionController,
    spriteManager
);
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

        // Uscita dallo shop con ESC o tasto dedicato
        if (input.isCancel()) {
            transitionController.popState();
            return;
        }

        // Uscita dal libro con ESC / Cancel

        /*if (cancelNow && !escWasPressed) {
            if (currentSection == Section.ORDERS && ordersSection.isDialogueActive()) {
                ordersSection.closeDialogue();
            } else if (phase == Phase.OPEN) {
                phase = Phase.CLOSING;
                closeAnimator.play();
            }
        }
        escWasPressed = cancelNow;*/

        switch (phase) {
            case OPENING:
                openAnimator.update();
                if (openAnimator.isFinished()) {
                    phase = Phase.OPEN;
                }
                break;

            case OPEN:
                updateOpenPhase();
                break;

            case CLOSING:
                closeAnimator.update();
                if (closeAnimator.isFinished()) {
                    phase = Phase.CLOSED;
                    transitionController.popState();
                }
                break;

            case CLOSED:
            default:
                break;
        }
    }

    private void updateOpenPhase() {
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

        // Sfoglio pagine con FRECCIA DESTRA
        final boolean rightNow = input.isRight();
        if (rightNow && !rightWasPressed) {
            if (currentSection == Section.RECIPES && recipeSection.canGoNext()) {
                turningPage = true;
                turningRight = true;
                turnRightAnimator.play();
            } else if (currentSection == Section.ORDERS && ordersSection.canGoNext()) {
                ordersSection.goNext();
            }
        }
        rightWasPressed = rightNow;

        // Sfoglio pagine con FRECCIA SINISTRA
        final boolean leftNow = input.isLeft();
        if (leftNow && !leftWasPressed) {
            if (currentSection == Section.RECIPES && recipeSection.canGoPrev()) {
                turningPage = true;
                turningRight = false;
                turnLeftAnimator.play();
            } else if (currentSection == Section.ORDERS && ordersSection.canGoPrev()) {
                ordersSection.goPrev();
            }
        }
        leftWasPressed = leftNow;

        // Esegue l'update specifico della sezione (es. tasto [E] in Orders)
        getActiveSection().update();

        // Navigazione tra le sezioni del libro con UP / DOWN
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

        gc.setFill(Color.rgb(0, 0, 0, OVERLAY_OPACITY));
        gc.fillRect(0, 0, screenW, screenH);

        final double bookSize = Math.min(screenW * BOOK_WIDTH_RATIO, screenH * BOOK_HEIGHT_RATIO);
        final double bookW = bookSize;
        final double bookH = bookSize;
        final double bookX = (screenW - bookW) / 2;
        final double bookY = (screenH - bookH) / 2;

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

        getActiveSection().render(gc, x, y, w, h);
    }

    private void renderBookmark(final GraphicsContext gc, final double x, final double y,
            final double w, final double h,
            final int spriteRow, final Section section) {
        final boolean isActive = currentSection == section;
        final boolean isEnabled = true;

        final Image sheet = spriteManager.getStaticSprite("sprites/ui_book/bookmarks");
        if (sheet != null) {
            final double frameW = sheet.getWidth() / BOOKMARK_SHEET_COLS;
            final double frameH = sheet.getHeight() / BOOKMARK_SHEET_ROWS;
            final int spriteCol = isActive ? 0 : 1;
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

    private BookSection getActiveSection() {
        return switch (currentSection) {
            case RECIPES -> recipeSection;
            case ORDERS -> ordersSection;
            case INVENTORY -> inventorySection;
        };
    }
}
