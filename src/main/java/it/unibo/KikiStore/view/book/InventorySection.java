package it.unibo.KikiStore.view.book;

import java.util.ArrayList;
import java.util.List;

import it.unibo.KikiStore.controller.api.InventoryController;
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
 * Inventory grid section — shown as two fixed facing pages inside the book:
 * left page = all 18 ingredients, right page = the 10 potions
 * (remaining slots on the right page stay empty and gray).
 */
public class InventorySection implements BookSection {

    private static final int COLUMNS = 3;
    private static final int ROWS = 6;
    private static final int SLOTS_PER_SIDE = COLUMNS * ROWS; // 18

    private static final double SLOT_PADDING = 6.0;
    private static final double ITEM_PADDING = 6.0;
    private static final double PAGE_GAP = 20.0;

    private static final Color COL_EMPTY_FALLBACK = Color.web("#8B7355");
    private static final Color COL_FULL_FALLBACK = Color.web("#C68642");
    private static final Color COL_QTY = Color.web("#3B2006");
    private static final Color COL_QTY_EMPTY = Color.web("#5C4A3A");

    private final InventoryController inventoryController;
    private final GameCatalog gameCatalog;
    private final SpriteManager spriteManager;
    private final Font pixelFontSmall;

    private final ColorAdjust grayscale = new ColorAdjust();
    private List<GameItem> ingredientSlots;
    private List<GameItem> potionSlots;

    /**
     * @param inventoryController the inventory controller
     * @param gameCatalog the full item catalog — 18 ingredients, 10 potions
     * @param spriteManager the sprite manager
     * @param pixelFontSmall small pixel font for quantity text
     */
    public InventorySection(final InventoryController inventoryController,
                             final GameCatalog gameCatalog,
                             final SpriteManager spriteManager,
                             final Font pixelFontSmall) {
        this.inventoryController = inventoryController;
        this.gameCatalog = gameCatalog;
        this.spriteManager = spriteManager;
        this.pixelFontSmall = pixelFontSmall;
        this.grayscale.setSaturation(-1.0);
        buildSlots();
    }

    /** Refreshes both pages with current inventory quantities. Call when reopening the book. */
    public void refresh() {
        buildSlots();
    }

    /**
     * Builds the two fixed lists — ingredients for the left page,
     * potions for the right page.
     */
    private void buildSlots() {
        ingredientSlots = new ArrayList<>();
        for (final Ingredient ing : gameCatalog.getAllIngredients()) {
            final int qty = inventoryController.getIngredientQuantity(ing.getName());
            ingredientSlots.add(new IngredientImpl(ing.getName(), ing.getImagePath(), qty, ing.getType(),ing.getPrice()));
        }

        potionSlots = new ArrayList<>();
        for (final Potion pot : gameCatalog.getAllPotions()) {
            final int qty = inventoryController.getPotionQuantity(pot.getName());
            potionSlots.add(new PotionImpl(pot.getName(), pot.getImagePath(), qty,
                pot.getDescription(), pot.getEffect(), pot.isBlack()));
        }
    }

    @Override
    public void update() {
        // Niente da aggiornare — entrambe le pagine sono sempre visibili insieme,
        // non c'è navigazione interna a questa sezione - solo in recipes
    }

    @Override
    public void render(final GraphicsContext gc, final double x, final double y,
                        final double w, final double h) {

        gc.setImageSmoothing(false); // Lo smoothing annulla l'effetto pixellato
        final double sideW = (w - PAGE_GAP) / 2;
        final Image cubeSprite = spriteManager.getStaticSprite("sprites/ui_book/sells_full");

        if (cubeSprite == null) {
            return;
        }

        // Calcola dimensione e offset dello sfondo mantenendo il rapporto originale - DA MODIFICARE
        final double spriteAspect = cubeSprite.getWidth() / cubeSprite.getHeight();
        double drawW = sideW;
        double drawH = drawW / spriteAspect;

        if (drawH > h) {
            drawH = h;
            drawW = drawH * spriteAspect;
        }

        final double offsetX = (sideW - drawW) / 2;
        final double offsetY = (h - drawH) / 2;

        // Pagina sinistra
        final double leftPageX = x + offsetX;
        final double leftPageY = y + offsetY;
        gc.drawImage(cubeSprite, leftPageX, leftPageY, drawW, drawH);
        renderItemsOnly(gc, ingredientSlots, leftPageX, leftPageY, drawW, drawH);

        // Pagina destra
        final double rightPageX = x + sideW + PAGE_GAP + offsetX;
        final double rightPageY = y + offsetY;
        gc.drawImage(cubeSprite, rightPageX, rightPageY, drawW, drawH);
        renderItemsOnly(gc, potionSlots, rightPageX, rightPageY, drawW, drawH);
    }

    private void renderItemsOnly(final GraphicsContext gc, final List<GameItem> items,
                                final double x, final double y, final double w, final double h) {
        final double slotW = (w - SLOT_PADDING * (COLUMNS - 1)) / COLUMNS;
        final double slotH = (h - SLOT_PADDING * (ROWS - 1)) / ROWS;

        for (int i = 0; i < SLOTS_PER_SIDE; i++) {
            final int col = i % COLUMNS;
            final int row = i / COLUMNS;
            final double sx = x + col * (slotW + SLOT_PADDING);
            final double sy = y + row * (slotH + SLOT_PADDING);

            if (i < items.size()) {
                final GameItem item = items.get(i);
                final boolean isEmpty = item.getQuantity() == 0;

                final Image sprite = spriteManager.getStaticSprite(item.getImagePath());
                final double spriteX = sx + ITEM_PADDING;
                final double spriteY = sy + ITEM_PADDING;
                final double spriteW = slotW - ITEM_PADDING * 2;
                final double spriteH = slotH - ITEM_PADDING * 2 - 12;

                if (sprite != null) {
                    if (isEmpty) {
                        gc.setEffect(grayscale);
                    }
                    gc.drawImage(sprite, spriteX, spriteY, spriteW, spriteH);
                    gc.setEffect(null);
                }

                gc.setFill(isEmpty ? COL_QTY_EMPTY : COL_QTY);
                gc.setFont(pixelFontSmall);
                gc.setTextAlign(TextAlignment.RIGHT);
                gc.fillText(String.valueOf(item.getQuantity()), sx + slotW - 3, sy + slotH - 2);
            }
            // Se i >= items.size(), lo slot resta vuoto — il cubo grigio è già nello sfondo
        }
    }
}