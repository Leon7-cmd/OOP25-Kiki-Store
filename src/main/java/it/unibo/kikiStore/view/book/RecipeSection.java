package it.unibo.kikiStore.view.book;

import it.unibo.kikiStore.controller.api.RecipeBookController;
import it.unibo.kikiStore.model.inventory.api.Ingredient;
import it.unibo.kikiStore.model.inventory.api.Recipe;
import it.unibo.kikiStore.view.utility.SpriteManager;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import java.util.List;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javafx.scene.text.Text;

/**
 * Recipe book section — shows two recipes at once, one per open page
 * (left page + right page), like a real book spread.
 * Page turning is handled externally by BookState via goNext()/goPrev(),
 * synchronized with the page-turn animation.
 */
public final class RecipeSection implements BookSection {

    private static final double IMAGE_SIZE_RATIO = 0.5;
    private static final double TEXT_LINE_HEIGHT = 13.0;
    private static final double TEXT_SIDE_PADDING = 15.0;
    private static final double TEXT_MARGIN = 35.0;

    private static final Color COL_TITLE = Color.web("#3B2006");
    private static final Color COL_TEXT = Color.web("#3B2006");
    private static final Color COL_EMPTY_MSG = Color.web("#5C4A3A");
    private static final Color COL_IMG_FALLBACK = Color.web("#C8A96E");

    private static final double PAGE_TOP_FRAC = 105.0 / 272.0;
    //private static final double PAGE_BOTTOM_FRAC = 244.0 / 272.0;
    private static final double PAGE_LEFT_FRAC = 26.0 / 272.0;
    private static final double PAGE_CENTER_FRAC = 135.5 / 272.0;
    private static final double PAGE_RIGHT_FRAC = 245.0 / 272.0;

    private static final double EFFECT_TOP_OFFSET = 18.0;
    private static final double INGREDIENT_TOP_MARGIN = 10.0;

    private final RecipeBookController recipeBookController;
    private final SpriteManager spriteManager;
    private final Font pixelFont;
    private final Font pixelFontSmall;

    private List<Recipe> unlockedRecipes;
    private int leftIndex; // indice della ricetta sulla pagina sinistra; destra = leftIndex + 1

    /**
     * @param recipeBookController the recipe book controller
     * @param spriteManager        the sprite manager
     * @param pixelFont            pixel font for the potion name
     * @param pixelFontSmall       pixel font for the description
     */
    @SuppressFBWarnings(
        value = "EI_EXPOSE_REP2",
        justification = "Controllers and rendering resources are shared with the book on purpose"
    )
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

        final double rightPageX = x + PAGE_CENTER_FRAC * bookSize;
        final double rightPageY = leftPageY;
        final double rightPageW = (PAGE_RIGHT_FRAC - PAGE_CENTER_FRAC) * bookSize;

        // Pagina sinistra — sempre presente se ci sono ricette
        renderRecipePage(gc, unlockedRecipes.get(leftIndex), leftPageX, leftPageY, leftPageW);

        // Pagina destra — solo se esiste una ricetta successiva nella lista
        final int rightIndex = leftIndex + 1;
        if (rightIndex < unlockedRecipes.size()) {
            renderRecipePage(gc, unlockedRecipes.get(rightIndex), rightPageX, rightPageY, rightPageW);
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
     */
    private void renderRecipePage(final GraphicsContext gc, final Recipe recipe,
            final double x, final double y,
            final double w) {
        final double imgSize = w * IMAGE_SIZE_RATIO;
        final double imgX = x + (w - imgSize) / 2;
        final double imgY = y + 10;

        final Image potionImg = spriteManager.getStaticSprite(recipe.getPotion().getImagePath());
        if (potionImg != null) {
            gc.drawImage(potionImg, imgX, imgY, imgSize, imgSize);
        } else {
            gc.setFill(COL_IMG_FALLBACK);
            gc.fillRect(imgX, imgY, imgSize, imgSize);
        }

        gc.setFill(COL_TITLE);
        gc.setFont(pixelFont);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText(recipe.getPotion().getName(), x + w / 2, imgY + imgSize + TEXT_MARGIN);

        gc.setFill(COL_TEXT);
        gc.setFont(pixelFontSmall);
        gc.setTextAlign(TextAlignment.LEFT);
        final double effectY = imgY + imgSize + TEXT_MARGIN + EFFECT_TOP_OFFSET;
        gc.fillText("EFFECT: " + recipe.getPotion().getEffect(), x + TEXT_SIDE_PADDING, effectY);

        final double descY = effectY + TEXT_MARGIN;
        final double textEndY = drawWrappedText(gc, recipe.getPotion().getDescription(),
                x + TEXT_SIDE_PADDING, descY, w - TEXT_MARGIN);

        final String ingredientsText = "Ingredients: " + joinIngredientNames(recipe.getIngredients());
        drawWrappedText(gc, ingredientsText, x + TEXT_SIDE_PADDING,
                textEndY + INGREDIENT_TOP_MARGIN, w - TEXT_MARGIN);
    }

    /**
     * Joins the ingredient names into a single comma-separated string.
     *
     * @param ingredients the recipe's required ingredients
     * @return the joined names, e.g. "Basil, Dandelion, Sage"
     */
    private String joinIngredientNames(final List<Ingredient> ingredients) {
        final StringBuilder result = new StringBuilder();
        for (int i = 0; i < ingredients.size(); i++) {
            if (i > 0) {
                result.append(", ");
            }
            result.append(ingredients.get(i).getName());
        }
        return result.toString();
    }

    /**
     * Draws text wrapped to fit within maxWidth, breaking on word boundaries.
     *
     * @param gc       graphics context
     * @param text     the text to draw
     * @param x        left x position
     * @param y        starting y position (top of first line)
     * @param maxWidth maximum width before wrapping
     * @return the y position just below the last drawn line, where the next
     *         content can start
     */
    private double drawWrappedText(final GraphicsContext gc, final String text,
            final double x, final double y, final double maxWidth) {
        final String[] words = text.split(" ");
        final StringBuilder line = new StringBuilder();
        double currentY = y;

        for (final String word : words) {
            final String candidate = line.length() == 0 ? word : line + " " + word;
            if (line.length() > 0 && measureTextWidth(candidate) > maxWidth) {
                gc.fillText(line.toString(), x, currentY);
                currentY += TEXT_LINE_HEIGHT;
                line.setLength(0);
                line.append(word);
            } else {
                line.setLength(0);
                line.append(candidate);
            }
        }
        if (line.length() > 0) {
            gc.fillText(line.toString(), x, currentY);
            currentY += TEXT_LINE_HEIGHT;
        }
        return currentY;
    }

    private double measureTextWidth(final String text) {
        final Text measurer = new Text(text);
        measurer.setFont(pixelFontSmall);
        return measurer.getLayoutBounds().getWidth();
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
