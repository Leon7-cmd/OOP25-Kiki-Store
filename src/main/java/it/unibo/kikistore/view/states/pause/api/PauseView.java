package it.unibo.kikistore.view.states.pause.api;

import javafx.scene.canvas.GraphicsContext;

/**
 * View interface for the pause overlay screen.
 */
public interface PauseView {

    /**
     * Renders the darkening overlay, title, and interactive buttons.
     *
     * @param gc the GraphicsContext to draw on
     */
    void render(GraphicsContext gc);

    /**
     * Checks if the resume button contains the given screen coordinates.
     *
     * @param x the mouse X position
     * @param y the mouse Y position
     * @return true if the coordinates fall inside the resume button, false otherwise
     */
    boolean isResumeClicked(double x, double y);

    /**
     * Checks if the return-to-title button contains the given screen coordinates.
     *
     * @param x the mouse X position
     * @param y the mouse Y position
     * @return true if the coordinates fall inside the title button, false otherwise
     */
    boolean isTitleClicked(double x, double y);
}
