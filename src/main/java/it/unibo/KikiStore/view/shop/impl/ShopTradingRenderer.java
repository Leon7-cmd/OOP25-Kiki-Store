package it.unibo.KikiStore.view.shop.impl;

import java.util.List;

import it.unibo.KikiStore.controller.api.ShopTradingController;
import it.unibo.KikiStore.model.item.api.GameItem;
import it.unibo.KikiStore.view.utility.SpriteManager;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public final class ShopTradingRenderer {

    private static final int MAX_VISIBLE_ITEMS = 4;
    private final SpriteManager spriteManager;

    public ShopTradingRenderer(final SpriteManager spriteManager) {
        this.spriteManager = spriteManager;
    }

    /**
     * @param <T> il tipo di item scambiato (Potion o Ingredient)
     * @param dialogueText testo mostrato nel riquadro di dialogo (feedback transazione o saluto)
     */
    public <T extends GameItem> void render(
            final GraphicsContext gc,
            final ShopTradingController<T> controller,
            final boolean isBuyingTab,
            final int selectedIndex,
            final String dialogueText) {

        final double w = gc.getCanvas().getWidth();
        final double h = gc.getCanvas().getHeight();

        gc.setFill(Color.rgb(0, 0, 0, 0.55));
        gc.fillRect(0, 0, w, h);

        final double menuW = w * 0.44;
        final double topSectionH = h * 0.70;
        final double startX = 20;
        final double startY = 20;

        final List<T> items = isBuyingTab ? controller.getBuyableItems() : controller.getSellableItems();
        final T selectedItem = items.isEmpty() ? null : items.get(Math.min(selectedIndex, items.size() - 1));

        drawLeftPanel(gc, controller, items, startX, startY, menuW, topSectionH, isBuyingTab, selectedIndex);
        drawRightScene(gc, controller, selectedItem, isBuyingTab, startX + menuW + 20, startY, w - (menuW + startX + 40), topSectionH);
        drawDialogueBox(gc, dialogueText, startX, startY + topSectionH + 15, w - (startX * 2), h - (topSectionH + startY + 30));
    }

    private <T extends GameItem> void drawLeftPanel(
            final GraphicsContext gc,
            final ShopTradingController<T> controller,
            final List<T> items,
            final double x,
            final double y,
            final double w,
            final double h,
            final boolean isBuyingTab,
            final int selectedIndex) {

        // Banner intestazione
        gc.setFill(Color.rgb(245, 230, 200));
        gc.fillRoundRect(x + 15, y, w - 30, 26, 8, 8);
        gc.setStroke(Color.rgb(80, 50, 25));
        gc.strokeRoundRect(x + 15, y, w - 30, 26, 8, 8);
        gc.setFill(Color.rgb(60, 35, 15));
        gc.setFont(Font.font("Monospaced", FontWeight.BOLD, 12));
        gc.fillText("BOTTEGA", x + 30, y + 17);

        // Sfondo pergamena
        final double bodyY = y + 30;
        final double bodyH = h - 30;
        gc.setFill(Color.rgb(248, 238, 222));
        gc.fillRoundRect(x, bodyY, w, bodyH, 12, 12);
        gc.setStroke(Color.rgb(180, 140, 100));
        gc.setLineWidth(2);
        gc.strokeRoundRect(x, bodyY, w, bodyH, 12, 12);

        // Tab COMPRA / VENDI
        final double tabW = (w - 20) / 2;
        gc.setFill(isBuyingTab ? Color.rgb(225, 190, 145) : Color.rgb(195, 165, 125));
        gc.fillRoundRect(x + 6, bodyY + 6, tabW, 24, 6, 6);
        gc.setFill(Color.BLACK);
        gc.fillText("COMPRA", x + (tabW / 2) - 20, bodyY + 22);

        gc.setFill(!isBuyingTab ? Color.rgb(225, 190, 145) : Color.rgb(195, 165, 125));
        gc.fillRoundRect(x + 10 + tabW, bodyY + 6, tabW, 24, 6, 6);
        gc.setFill(Color.BLACK);
        gc.fillText("VENDI", x + tabW + (tabW / 2) - 15, bodyY + 22);

        // Lista item (scrollabile a finestra: mostra al massimo MAX_VISIBLE_ITEMS)
        final double listY = bodyY + 44;
        final double slotH = 36;
        final double slotGap = 4;

        if (items.isEmpty()) {
            gc.setFill(Color.rgb(90, 70, 50));
            gc.setFont(Font.font("System", 12));
            gc.fillText("Nessun oggetto disponibile", x + 20, listY + 20);
            return;
        }

        final int firstVisible = Math.max(0, Math.min(selectedIndex - MAX_VISIBLE_ITEMS + 1, items.size() - MAX_VISIBLE_ITEMS));
        final int lastVisible = Math.min(items.size(), Math.max(0, firstVisible) + MAX_VISIBLE_ITEMS);

        for (int i = Math.max(0, firstVisible); i < lastVisible; i++) {
            final T item = items.get(i);
            final double itemY = listY + (i - firstVisible) * (slotH + slotGap);
            final boolean isSelected = i == selectedIndex;

            gc.setFill(isSelected ? Color.rgb(255, 250, 235) : Color.WHITE);
            gc.fillRoundRect(x + 8, itemY, w - 16, slotH, 6, 6);
            gc.setStroke(isSelected ? Color.rgb(210, 140, 40) : Color.rgb(140, 90, 50));
            gc.setLineWidth(isSelected ? 2.5 : 1);
            gc.strokeRoundRect(x + 8, itemY, w - 16, slotH, 6, 6);

            gc.setFill(Color.rgb(30, 30, 30));
            gc.setFont(Font.font("System", FontWeight.BOLD, 12));
            gc.fillText(item.getName(), x + 20, itemY + 15);

            gc.setFont(Font.font("System", 10));
            gc.fillText(
                    (isBuyingTab ? "DISPONIBILE" : "POSSEDUTO: " + item.getQuantity()),
                    x + 20,
                    itemY + 28
            );

            final int price = isBuyingTab ? controller.getBuyPrice(item) : controller.getSellPrice(item);
            gc.setFont(Font.font("System", FontWeight.BOLD, 12));
            gc.fillText(price + "euro", x + w - 65, itemY + 22);
        }
    }

    private <T extends GameItem> void drawRightScene(
            final GraphicsContext gc,
            final ShopTradingController<T> controller,
            final T selectedItem,
            final boolean isBuyingTab,
            final double x,
            final double y,
            final double w,
            final double h) {

        gc.setFill(Color.rgb(75, 55, 40));
        gc.fillRoundRect(x, y, w, h, 12, 12);
        gc.setStroke(Color.rgb(120, 85, 60));
        gc.strokeRoundRect(x, y, w, h, 12, 12);

        if (selectedItem == null) {
            return;
        }

        // Sprite dell'item selezionato, centrato nella parte alta del pannello
        final Image sprite = spriteManager.getStaticSprite(selectedItem.getImagePath());
        final double spriteSize = Math.min(w * 0.5, h * 0.4);
        final double spriteX = x + (w - spriteSize) / 2;
        final double spriteY = y + 20;
        if (sprite != null) {
            gc.drawImage(sprite, spriteX, spriteY, spriteSize, spriteSize);
        }

        // Nome e prezzo sotto lo sprite
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("System", FontWeight.BOLD, 14));
        gc.fillText(selectedItem.getName(), x + 15, spriteY + spriteSize + 22);

        final int price = isBuyingTab ? controller.getBuyPrice(selectedItem) : controller.getSellPrice(selectedItem);
        gc.setFont(Font.font("System", 12));
        gc.fillText(price + " euro", x + 15, spriteY + spriteSize + 40);
    }

    private void drawDialogueBox(
            final GraphicsContext gc,
            final String dialogueText,
            final double x,
            final double y,
            final double w,
            final double h) {

        gc.setFill(Color.rgb(25, 20, 15, 0.95));
        gc.fillRoundRect(x, y, w, h, 8, 8);
        gc.setStroke(Color.rgb(200, 165, 110));
        gc.strokeRoundRect(x, y, w, h, 8, 8);

        gc.setFill(Color.GOLD);
        gc.setFont(Font.font("Monospaced", FontWeight.BOLD, 13));
        gc.fillText("Kiki:", x + 15, y + 24);

        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("System", 12));
        gc.fillText(dialogueText, x + 65, y + 24);
    }
}