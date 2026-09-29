package it.unibo.kikistore.view.states.menu.api;

import javafx.scene.canvas.GraphicsContext;

/**
 * View interface for the title and main menu screen.
 */
public interface MenuView {

    /**
     * Renders the background and menu buttons.
     *
     * @param gc the GraphicsContext to draw onto
     */
    void render(GraphicsContext gc);

    /**
     * Checks if the start/play button was clicked at the given coordinates.
     *
     * @param x mouse X coordinate
     * @param y mouse Y coordinate
     * @return true if the click is inside the start button bounds
     */
    boolean isStartClicked(double x, double y);

    /**
     * Checks if the quit button was clicked at the given coordinates.
     *
     * @param x mouse X coordinate
     * @param y mouse Y coordinate
     * @return true if the click is inside the quit button bounds
     */
    boolean isQuitClicked(double x, double y);
}
