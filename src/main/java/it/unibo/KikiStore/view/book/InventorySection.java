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
    private static final int SLOTS_PER_SIDE = COLUMNS * ROWS;

    private static final double SLOT_PADDING = 6.0;
    private static final double ITEM_PADDING = 6.0;

    // Percentuali della pagina bianca rispetto al frame 272x272 di Open_book
    private static final double PAGE_TOP_FRAC = 100.0 / 272.0;
    private static final double PAGE_BOTTOM_FRAC = 244.0 / 272.0;
    private static final double PAGE_LEFT_FRAC = 25.0 / 272.0;
    private static final double PAGE_CENTER_FRAC = 135.5 / 272.0;
    private static final double PAGE_RIGHT_FRAC = 250.0 / 272.0;

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

        // x,y,w,h qui rappresentano l'intero quadrato del libro (w == h)
        final double bookSize = w;

        // Pagina sinistra — area calcolata dalle percentuali misurate
        final double leftPageX = x + PAGE_LEFT_FRAC * bookSize;
        final double leftPageY = y + PAGE_TOP_FRAC * bookSize;
        final double leftPageW = (PAGE_CENTER_FRAC - PAGE_LEFT_FRAC) * bookSize;
        final double leftPageH = (PAGE_BOTTOM_FRAC - PAGE_TOP_FRAC) * bookSize;

        // Pagina destra — stessa altezza, parte da dove finisce la sinistra
        final double rightPageX = x + PAGE_CENTER_FRAC * bookSize;
        final double rightPageY = leftPageY;
        final double rightPageW = (PAGE_RIGHT_FRAC - PAGE_CENTER_FRAC) * bookSize;
        final double rightPageH = leftPageH;

        final Image cubeSprite = spriteManager.getStaticSprite("sprites/ui_book/sells_full");
        if (cubeSprite == null) {
            return;
        }

        // Mantiene il rapporto originale dello sprite (96:144) dentro l'area della pagina
        final double spriteAspect = cubeSprite.getWidth() / cubeSprite.getHeight();

        renderPageBackground(gc, cubeSprite, spriteAspect, ingredientSlots,
            leftPageX, leftPageY, leftPageW, leftPageH);
        renderPageBackground(gc, cubeSprite, spriteAspect, potionSlots,
            rightPageX, rightPageY, rightPageW, rightPageH);
    }

    /**
     * Draws the cube background sprite fitted inside
     * the page area, centered, then draws the items on top.
     *
     * @param gc graphics context
     * @param cubeSprite the cube background sprite
     * @param spriteAspect width/height ratio of cubeSprite
     * @param items items to draw on this page
     * @param pageX exact page area x
     * @param pageY exact page area y
     * @param pageW exact page area width
     * @param pageH exact page area height
     */
    private void renderPageBackground(final GraphicsContext gc, final Image cubeSprite,
                                    final double spriteAspect, final List<GameItem> items,
                                    final double pageX, final double pageY,
                                    final double pageW, final double pageH) {
        double drawW = pageW;
        double drawH = drawW / spriteAspect;

        if (drawH > pageH) {
            drawH = pageH;
            drawW = drawH * spriteAspect;
        }

        final double drawX = pageX + (pageW - drawW) / 2;
        final double drawY = pageY + (pageH - drawH) / 2;

        gc.drawImage(cubeSprite, drawX, drawY, drawW, drawH);
        renderItemsOnly(gc, items, drawX, drawY, drawW, drawH);
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
                final double spriteY = sy + ITEM_PADDING + 6;
                final double spriteW = slotW - ITEM_PADDING * 2;
                final double spriteH = slotH - ITEM_PADDING * 2 - 12;

                if (sprite != null) {
                    // Mantiene il rapporto originale dello sprite invece di stirarlo
                    //dentro lo spazio disponibile dello slot
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

                gc.setFill(isEmpty ? COL_QTY_EMPTY : COL_QTY);
                gc.setFont(pixelFontSmall);
                gc.setTextAlign(TextAlignment.RIGHT);
                gc.fillText(String.valueOf(item.getQuantity()), sx + slotW - 3, sy + slotH - 2);
            }
        }
    }
}