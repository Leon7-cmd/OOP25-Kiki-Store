package it.unibo.KikiStore.view.book;

import javafx.scene.canvas.GraphicsContext;

/**
 * A content section displayed inside the open book
 * (Inventory, Recipes, Orders).
 */
public interface BookSection {
    /**
     * Draws this section's content inside the given area.
     *
     * @param gc graphics context
     * @param x top-left x of the content area
     * @param y top-left y of the content area
     * @param w width of the content area
     * @param h height of the content area
     */
    void render(GraphicsContext gc, double x, double y, double w, double h);

    /** Handles input for page turning etc. Called every tick when this section is active. */
    void update();
}
