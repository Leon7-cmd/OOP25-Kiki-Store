package it.unibo.KikiStore.view.book;

import it.unibo.KikiStore.controller.api.RecipeBookController;
import it.unibo.KikiStore.model.inventory.api.Recipe;
import it.unibo.KikiStore.view.utility.SpriteManager;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import java.util.List;

/**
 * Recipe book section — shows two recipes at once, one per open page
 * (left page + right page), like a real book spread.
 * Page turning is handled externally by BookState via goNext()/goPrev(),
 * synchronized with the page-turn animation.
 */
public final class RecipeSection implements BookSection {

    private static final double IMAGE_SIZE_RATIO = 0.5;
    private static final double TEXT_LINE_HEIGHT = 13.0;
    private static final double CHAR_WIDTH_ESTIMATE = 6.5; // TO-DO calibra con Press Start 2P
    private static final double TEXT_SIDE_PADDING = 10.0;
    private static final double TEXT_MARGIN = 20.0;

    private static final Color COL_TITLE = Color.web("#3B2006");
    private static final Color COL_TEXT = Color.web("#3B2006");
    private static final Color COL_EMPTY_MSG = Color.web("#5C4A3A");
    private static final Color COL_IMG_FALLBACK = Color.web("#C8A96E");

    private static final double PAGE_TOP_FRAC = 105.0 / 272.0;
    private static final double PAGE_BOTTOM_FRAC = 244.0 / 272.0;
    private static final double PAGE_LEFT_FRAC = 26.0 / 272.0;
    private static final double PAGE_CENTER_FRAC = 135.5 / 272.0;
    private static final double PAGE_RIGHT_FRAC = 270.0 / 272.0;

    private final RecipeBookController recipeBookController;
    private final SpriteManager spriteManager;
    private final Font pixelFont;
    private final Font pixelFontSmall;

    private static final double INGREDIENT_ICON_SIZE = 16.0;
    private static final Color COL_SECTION_HEADER = Color.web("#6B3E11");

    private List<Recipe> unlockedRecipes;
    private int leftIndex; // indice della ricetta sulla pagina sinistra; destra = leftIndex + 1

    /**
     * @param recipeBookController the recipe book controller
     * @param spriteManager        the sprite manager
     * @param pixelFont            pixel font for the potion name
     * @param pixelFontSmall       pixel font for the description
     */
    public RecipeSection(final RecipeBookController recipeBookController,
            final SpriteManager spriteManager,
            final Font pixelFont,
            final Font pixelFontSmall) {
        this.recipeBookController = recipeBookController;
        this.spriteManager = spriteManager;
        this.pixelFont = pixelFont;
        this.pixelFontSmall = pixelFontSmall;
        refresh();
    }

    /**
     * Reloads unlocked recipes and resets to the first spread. Call when reopening
     * the book.
     */
    public void refresh() {
        leftIndex = 0;
        unlockedRecipes = recipeBookController.getUnlockedRecipes();
    }

    @Override
    public void update() {
        // Vuoto — BookState gestisce l'input per coordinare l'animazione di sfoglio
        // pagina
    }

    @Override
    public void render(final GraphicsContext gc, final double x, final double y,
            final double w, final double h) {
        gc.setImageSmoothing(false);
        if (unlockedRecipes.isEmpty()) {
            gc.setFill(COL_EMPTY_MSG);
            gc.setFont(pixelFontSmall);
            gc.setTextAlign(TextAlignment.CENTER);
            gc.fillText("No recipes discovered yet", x + w / 2, y + h / 2);
            return;
        }

        // x,y,w,h qui sono l'intero quadrato del libro (w == h)
        final double bookSize = w;

        final double leftPageX = x + PAGE_LEFT_FRAC * bookSize;
        final double leftPageY = y + PAGE_TOP_FRAC * bookSize;
        final double leftPageW = (PAGE_CENTER_FRAC - PAGE_LEFT_FRAC) * bookSize;
        final double leftPageH = (PAGE_BOTTOM_FRAC - PAGE_TOP_FRAC) * bookSize;

        final double rightPageX = x + PAGE_CENTER_FRAC * bookSize;
        final double rightPageY = leftPageY;
        final double rightPageW = (PAGE_RIGHT_FRAC - PAGE_CENTER_FRAC) * bookSize;
        final double rightPageH = leftPageH;

        // Pagina sinistra — sempre presente se ci sono ricette
        renderRecipePage(gc, unlockedRecipes.get(leftIndex), leftPageX, leftPageY, leftPageW, leftPageH);

        // Pagina destra — solo se esiste una ricetta successiva nella lista
        final int rightIndex = leftIndex + 1;
        if (rightIndex < unlockedRecipes.size()) {
            renderRecipePage(gc, unlockedRecipes.get(rightIndex), rightPageX, rightPageY, rightPageW, rightPageH);
        }
    }

    /**
     * Draws a single recipe on one page — potion image, name, and description.
     *
     * @param gc     graphics context
     * @param recipe the recipe to draw
     * @param x      page area x
     * @param y      page area y
     * @param w      page area width
     * @param h      page area height
     */
    

    /**
     * Draws a single recipe on one page — potion image, name, required ingredients, and description.
     */
    private void renderRecipePage(final GraphicsContext gc, final Recipe recipe,
            final double x, final double y,
            final double w, final double h) {

        // 1. Immagine della pozione (più raccolta per lasciare spazio agli ingredienti)
        final double imgSize = w * 0.32;
        final double imgX = x + (w - imgSize) / 2;
        final double imgY = y + 6;

        final Image potionImg = spriteManager.getStaticSprite(recipe.getPotion().getImagePath());
        if (potionImg != null) {
            gc.drawImage(potionImg, imgX, imgY, imgSize, imgSize);
        } else {
            gc.setFill(COL_IMG_FALLBACK);
            gc.fillRect(imgX, imgY, imgSize, imgSize);
        }

        // 2. Nome della pozione
        gc.setFill(COL_TITLE);
        gc.setFont(pixelFont);
        gc.setTextAlign(TextAlignment.CENTER);
        final double titleY = imgY + imgSize + 14;
        gc.fillText(recipe.getPotion().getName(), x + w / 2, titleY);

        // 3. Sezione Ingredienti Richiesti
        double currentY = titleY + 16;
        gc.setFill(COL_SECTION_HEADER);
        gc.setFont(pixelFontSmall);
        gc.setTextAlign(TextAlignment.LEFT);
        gc.fillText("Ingredients:", x + TEXT_SIDE_PADDING, currentY);

        currentY += 12;
        final List<it.unibo.KikiStore.model.inventory.api.Ingredient> ingredients = recipe.getIngredients();
        if (ingredients != null && !ingredients.isEmpty()) {
            for (final var ing : ingredients) {
                // Disegna sprite o fallback per ciascun ingrediente
                final Image ingImg = spriteManager.getStaticSprite(ing.getImagePath());
                final double iconX = x + TEXT_SIDE_PADDING;
                if (ingImg != null) {
                    gc.drawImage(ingImg, iconX, currentY - 10, INGREDIENT_ICON_SIZE, INGREDIENT_ICON_SIZE);
                }

                // Nome e Quantità (se quantity <= 0 mostriamo 1x)
                final int qty = ing.getQuantity() > 0 ? ing.getQuantity() : 1;
                final String text = qty + "x " + ing.getName();

                gc.setFill(COL_TEXT);
                gc.fillText(text, iconX + INGREDIENT_ICON_SIZE + 4, currentY + 2);
                currentY += 16;
            }
        }

        // 4. Descrizione / Lore in fondo
        currentY += 6;
        gc.setFill(COL_EMPTY_MSG);
        drawWrappedText(gc, recipe.getPotion().getDescription(), x + TEXT_SIDE_PADDING, currentY, w - TEXT_MARGIN);
    }
    /**
     * Draws text wrapped to fit within maxWidth, breaking on word boundaries.
     *
     * @param gc       graphics context
     * @param text     the text to draw
     * @param x        left x position
     * @param y        starting y position (top of first line)
     * @param maxWidth maximum width before wrapping
     */
    private void drawWrappedText(final GraphicsContext gc, final String text,
            final double x, final double y, final double maxWidth) {
        final int maxCharsPerLine = Math.max(1, (int) (maxWidth / CHAR_WIDTH_ESTIMATE));

        final String[] words = text.split(" ");
        final StringBuilder line = new StringBuilder();
        double currentY = y;

        for (final String word : words) {
            if (line.length() + word.length() + 1 > maxCharsPerLine) {
                gc.fillText(line.toString(), x, currentY);
                line.setLength(0);
                currentY += TEXT_LINE_HEIGHT;
            }
            if (line.length() > 0) {
                line.append(" ");
            }
            line.append(word);
        }
        if (line.length() > 0) {
            gc.fillText(line.toString(), x, currentY);
        }
    }

    /**
     * Checks whether there is a next spread of 2 more recipes ahead.
     *
     * @return true if there is a next spread
     */
    public boolean canGoNext() {
        return leftIndex + 2 < unlockedRecipes.size();
    }

    /**
     * Checks whether there is a previous spread of 2 recipes.
     *
     * @return true if there is a previous spread
     */
    public boolean canGoPrev() {
        return leftIndex - 2 >= 0;
    }

    /**
     * Advances to the next pair of recipes. Called by BookState after the
     * turn-right animation finishes.
     */
    public void goNext() {
        if (canGoNext()) {
            leftIndex += 2;
        }
    }

    /**
     * Goes back to the previous pair of recipes. Called by BookState after the
     * turn-left animation finishes.
     */
    public void goPrev() {
        if (canGoPrev()) {
            leftIndex -= 2;
        }
    }
}
