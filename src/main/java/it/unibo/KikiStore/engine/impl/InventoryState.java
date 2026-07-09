package it.unibo.KikiStore.engine.impl;

import java.util.ArrayList;
import java.util.List;

import it.unibo.KikiStore.controller.api.InputHandler;
import it.unibo.KikiStore.controller.api.InventoryController;
import it.unibo.KikiStore.engine.api.GameState;
import it.unibo.KikiStore.engine.api.GameStateManager;
import it.unibo.KikiStore.model.inventory.api.GameCatalog;
import it.unibo.KikiStore.model.inventory.api.Ingredient;
import it.unibo.KikiStore.model.inventory.api.Potion;
import it.unibo.KikiStore.model.inventory.impl.IngredientImpl;
import it.unibo.KikiStore.model.inventory.impl.PotionImpl;
import it.unibo.KikiStore.model.item.api.GameItem;
import it.unibo.KikiStore.view.utility.SpriteManager;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

/**
 * Inventory screen rendered as a pixel art wooden panel.
 * Shows all possible ingredients and potions.
 * Gray slot if quantity is 0, colored sprite if owned.
 * Paginated with left/right arrow keys.
 */
public class InventoryState implements GameState {

    // --- margine esterno dal bordo ---
    private static final double SCREEN_MARGIN = 24.0;

    // --- Bordo shelf ---
    private static final double BORDER = 6.0;
    private static final double SHADOW = 4.0;

    // --- Griglia ---
    private static final int COLUMNS = 5;
    private static final int ROWS = 3;
    private static final int ITEMS_PER_PAGE = COLUMNS * ROWS;
    private static final double SLOT_PADDING = 10.0; // spazio tra slot
    private static final double ITEM_PADDING = 8.0;  // spazio sprite dentro slot

    // --- Colori pixel art ---
    // Bordo ombra hard (angolo basso destra) 
    private static final Color COL_SHADOW = Color.web("#3B2006");
    // Bordo esterno scuro
    private static final Color COL_BORDER = Color.web("#5C3A1E");
    // Sfondo interno pannello marrone
    private static final Color COL_BG = Color.web("#C68642");
    // Slot vuoto grigio
    private static final Color COL_SLOT_EMPTY = Color.web("#8B7355");
    // Slot con item
    private static final Color COL_SLOT_FULL = Color.web("#F5DEB3");
    // Bordo slot scuro
    private static final Color COL_SLOT_BORDER = Color.web("#3B2006");
    // Testo quantità
    private static final Color COL_QTY = Color.web("#3B2006");
    // Testo quantità se vuoto
    private static final Color COL_QTY_EMPTY = Color.web("#5C4A3A");
    // Titolo
    private static final Color COL_TITLE = Color.web("#3B2006");
    // Frecce navigazione
    private static final Color COL_ARROW = Color.web("#3B2006");

    // --- Dipendenze ---
    private final InventoryController inventoryController;
    private final GameCatalog gameCatalog;
    private final SpriteManager spriteManager;
    private final GameStateManager gsm;
    private final GameState previousState;//---> da usare dopo modifiche tasti x/esc
    private final InputHandler input;

    // --- Font pixel art ---
    private final Font pixelFont;
    private final Font pixelFontSmall;

    // --- Stato ---
    private int currentPage = 0;
    private boolean rightWasPressed = false;
    private boolean leftWasPressed = false;
    private List<GameItem> allSlots;

    // --- Effetto grigio ---
    private final ColorAdjust grayscale = new ColorAdjust();

    /**
     * @param inventoryController the inventory controller
     * @param gameCatalog the full catalog of all possible items
     * @param spriteManager the sprite manager
     * @param gsm the game state manager
     * @param previousState the state to return to on close
     * @param input the input handler
     */
    public InventoryState(
        final InventoryController inventoryController,
        final GameCatalog gameCatalog,
        final SpriteManager spriteManager,
        final GameStateManager gsm,
        final GameState previousState,
        final InputHandler input
    ) {
        this.inventoryController = inventoryController;
        this.gameCatalog = gameCatalog;
        this.spriteManager = spriteManager;
        this.gsm = gsm;
        this.previousState = previousState;
        this.input = input;
        this.grayscale.setSaturation(-1.0);

        //TO-DO carica font pixel art
        final Font loaded = Font.loadFont(
            getClass().getResourceAsStream("/fonts/press_start_2p.ttf"), 10
        );
        this.pixelFont = loaded != null ? loaded : Font.font("Monospace", 10);
        final Font loadedSmall = Font.loadFont(
            getClass().getResourceAsStream("/fonts/press_start_2p.ttf"), 8
        );
        this.pixelFontSmall = loadedSmall != null ? loadedSmall : Font.font("Monospace", 8);
    }

    @Override
    public void init() {
        currentPage = 0;
        buildSlots();
    }

    /**
     * Builds the full slot list from GameCatalog.
     * Creates copies so catalog originals are never modified.
     * Fetches real quantity from InventoryController.
     */
    private void buildSlots() {
        allSlots = new ArrayList<>();
        for (final Ingredient ing : gameCatalog.getAllIngredients()) {
            final int qty = inventoryController.getIngredientQuantity(ing.getName());
            allSlots.add(new IngredientImpl(
                ing.getName(), ing.getImagePath(), qty, ing.getPrice(), ing.getType()));
        }
        for (final Potion pot : gameCatalog.getAllPotions()) {
            final int qty = inventoryController.getPotionQuantity(pot.getName());
            allSlots.add(new PotionImpl(
                pot.getName(), pot.getImagePath(), qty, pot.getPrice(),
                pot.getDescription(), pot.getEffect(), pot.isBlack()));
        }
    }

    @Override
    public void update() {
        // TODO: aggiungere input.isEsc() e input.isInventory() a InputHandler
        // if (input.isEsc() || input.isInventory()) gsm.setState(previousState);

        final boolean rightNow = input.isRight();
        if (rightNow && !rightWasPressed && canGoNext()) {
            currentPage++;
        }
        rightWasPressed = rightNow;

        final boolean leftNow = input.isLeft();
        if (leftNow && !leftWasPressed && currentPage > 0) {
            currentPage--;
        }
        leftWasPressed = leftNow;

        // TODO: click mouse sulla X --> gsm.setState(previousState)
    }

    @Override
    public void render(final GraphicsContext gc) {
        final double screenW = gc.getCanvas().getWidth();
        final double screenH = gc.getCanvas().getHeight();

        // -- PANNELLO ESTERNO --
        final double panelX = SCREEN_MARGIN;
        final double panelY = SCREEN_MARGIN;
        final double panelW = screenW - SCREEN_MARGIN * 2;
        final double panelH = screenH - SCREEN_MARGIN * 2;

        // Ombra hard pixel art - rettangolo scuro spostato in basso a destra
        gc.setFill(COL_SHADOW);
        gc.fillRect(panelX + SHADOW, panelY + SHADOW, panelW, panelH);

        // Bordo esterno scuro
        gc.setFill(COL_BORDER);
        gc.fillRect(panelX, panelY, panelW, panelH);

        // Sfondo interno marrone — rientrato di BORDER su ogni lato
        gc.setFill(COL_BG);
        gc.fillRect(
            panelX + BORDER, panelY + BORDER,
            panelW - BORDER * 2, panelH - BORDER * 2);

        // ── TITOLO ────────────────────────────────────────────────────
        gc.setFill(COL_TITLE);
        gc.setFont(pixelFont);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText("INVENTORY", screenW / 2, panelY + BORDER + 20);

        // ── GRIGLIA SLOT ──────────────────────────────────────────────
        // L'area della griglia è dentro il pannello, sotto il titolo.
        // Calcola dimensione slot in base allo spazio disponibile.
        final double gridX = panelX + BORDER + SLOT_PADDING;
        final double gridY = panelY + BORDER + 36;
        final double gridW = panelW - BORDER * 2 - SLOT_PADDING * 2;
        final double gridH = panelH - BORDER * 2 - 80;

        // Ogni slot occupa gridW/COLUMNS e gridH/ROWS
        // SLOT_PADDING divide gli slot tra loro
        final double slotW = (gridW - SLOT_PADDING * (COLUMNS - 1)) / COLUMNS;
        final double slotH = (gridH - SLOT_PADDING * (ROWS - 1)) / ROWS;

        final int startIndex = currentPage * ITEMS_PER_PAGE;
        final int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, allSlots.size());

        for (int i = startIndex; i < endIndex; i++) {
            final int displayIdx = i - startIndex;
            final int col = displayIdx % COLUMNS;
            final int row = displayIdx / COLUMNS;

            // Posizione top-left dello slot
            final double sx = gridX + col * (slotW + SLOT_PADDING);
            final double sy = gridY + row * (slotH + SLOT_PADDING);

            final GameItem item = allSlots.get(i);
            final boolean isEmpty = item.getQuantity() == 0;

            // ── DISEGNA SLOT ─────────────────────────────────────────
            // Ombra hard pixel art dello slot
            gc.setFill(COL_SHADOW);
            gc.fillRect(sx + 2, sy + 2, slotW, slotH);

            // Bordo slot
            gc.setFill(COL_SLOT_BORDER);
            gc.fillRect(sx, sy, slotW, slotH);

            // Sfondo slot — grigio se vuoto, chiaro se ha item
            gc.setFill(isEmpty ? COL_SLOT_EMPTY : COL_SLOT_FULL);
            gc.fillRect(sx + 2, sy + 2, slotW - 4, slotH - 4);

            // ── SPRITE ITEM ──────────────────────────────────────────
            // Se vuoto applica effetto grigio.
            final Image sprite = spriteManager.getStaticSprite(item.getImagePath());
            final double spriteX = sx + ITEM_PADDING;
            final double spriteY = sy + ITEM_PADDING;
            final double spriteW = slotW - ITEM_PADDING * 2;
            // Lascia spazio in basso per il numero quantità
            final double spriteH = slotH - ITEM_PADDING * 2 - 14;

            if (sprite != null) {
                if (isEmpty) {
                    gc.setEffect(grayscale);
                }
                gc.drawImage(sprite, spriteX, spriteY, spriteW, spriteH);
                gc.setEffect(null); // rimuovere dopo ogni volta il seteffect
            } else {
                // Placeholder se non c'è lo sprite
                gc.setFill(isEmpty ? Color.DARKGRAY : Color.web("#C8A96E"));
                gc.fillRect(spriteX, spriteY, spriteW, spriteH);
            }

            // ── QUANTITÀ in basso a destra ───────────────────────────
            gc.setFill(isEmpty ? COL_QTY_EMPTY : COL_QTY);
            gc.setFont(pixelFontSmall);
            gc.setTextAlign(TextAlignment.RIGHT);
            gc.fillText(
                String.valueOf(item.getQuantity()),
                sx + slotW - 4,
                sy + slotH - 3);
        }

        // Slot vuoti per completare la pagina se items < ITEMS_PER_PAGE
        // Disegna slot grigi per riempire la griglia visivamente
        for (int i = endIndex - startIndex; i < ITEMS_PER_PAGE; i++) {
            final int col = i % COLUMNS;
            final int row = i / COLUMNS;
            final double sx = gridX + col * (slotW + SLOT_PADDING);
            final double sy = gridY + row * (slotH + SLOT_PADDING);

            gc.setFill(COL_SHADOW);
            gc.fillRect(sx + 2, sy + 2, slotW, slotH);
            gc.setFill(COL_SLOT_BORDER);
            gc.fillRect(sx, sy, slotW, slotH);
            gc.setFill(COL_SLOT_EMPTY);
            gc.fillRect(sx + 2, sy + 2, slotW - 4, slotH - 4);
        }

        // ── BARRA NAVIGAZIONE IN BASSO ────────────────────────────────
        final double navY = panelY + panelH - BORDER - 4;
        final int totalPages = (int) Math.ceil((double) allSlots.size() / ITEMS_PER_PAGE);


        // Freccia sinistra PREV — disegnata solo se currentPage > 0
        final double arrowSize = 24.0;
        final double arrowY = navY - arrowSize;

        // Freccia sinistra — triangolo pixel art
        if (currentPage > 0) {
            gc.setFill(COL_ARROW);
        } else {
            gc.setFill(COL_SLOT_EMPTY); // grigia se non cliccabile
        }
        // Triangolo che punta a sinistra fatto con fillPolygon
        gc.fillPolygon(
            new double[]{panelX + BORDER + arrowSize, panelX + BORDER + arrowSize, panelX + BORDER + 4},
            new double[]{arrowY, arrowY + arrowSize, arrowY + arrowSize / 2},
            3
        );

        // Freccia destra NEXT — triangolo pixel art
        if (canGoNext()) {
            gc.setFill(COL_ARROW);
        } else {
            gc.setFill(COL_SLOT_EMPTY); // grigia se non cliccabile
        }
        // Triangolo che punta a destra
        gc.fillPolygon(
            new double[]{panelX + panelW - BORDER - arrowSize, panelX + panelW - BORDER - arrowSize, panelX + panelW - BORDER - 4},
            new double[]{arrowY, arrowY + arrowSize, arrowY + arrowSize / 2},
            3
        );

        // Numero pagina centrato — più in basso e visibile
        gc.setFill(COL_TITLE);
        gc.setFont(pixelFontSmall);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText(
            (currentPage + 1) + " / " + totalPages,
            screenW / 2,
            navY + arrowSize / 2 + 4
        );

        // ── PULSANTE X CHIUSURA ───────────────────────────────────────
        // Quadrato pixel art in alto a destra dentro il pannello
        final double closeSz = 20.0;
        final double closeX = panelX + panelW - BORDER - closeSz - 4;
        final double closeY = panelY + BORDER + 4;

        gc.setFill(COL_SHADOW);
        gc.fillRect(closeX + 2, closeY + 2, closeSz, closeSz);
        gc.setFill(COL_BORDER);
        gc.fillRect(closeX, closeY, closeSz, closeSz);
        gc.setFill(Color.web("#D04030"));
        gc.fillRect(closeX + 2, closeY + 2, closeSz - 4, closeSz - 4);
        gc.setFill(Color.WHITE);
        gc.setFont(pixelFontSmall);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText("X", closeX + closeSz / 2, closeY + closeSz - 4);
    }

    /**
     * @return true if there is a next page
     */
    private boolean canGoNext() {
        return (currentPage + 1) * ITEMS_PER_PAGE < allSlots.size();
    }
}