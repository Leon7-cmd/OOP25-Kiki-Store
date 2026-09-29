package it.unibo.kikistore.view.states.minigamefly.api;

import it.unibo.kikistore.view.entity.api.EntityRenderData;
import it.unibo.kikistore.view.hud.api.HUDRenderData;
import javafx.scene.canvas.GraphicsContext;

/**
 * View interface for the fly minigame.
 */
public interface MinigameFlyView {

    /**
     * Renders the scrolling background, player entity, overlays, and HUD.
     *
     * @param gc         graphics context to draw onto
     * @param kikiData   rendering data for the player entity
     * @param hudData    rendering data for the HUD overlay
     * @param cameraLeft the current left-bound position of the moving camera
     * @param showPrompt true if the initial instructions overlay should be drawn
     * @param frameCount animation frame counter
     */
    void render(
        GraphicsContext gc,
        EntityRenderData kikiData,
        HUDRenderData hudData,
        double cameraLeft,
        boolean showPrompt,
        int frameCount
    );

    /**
     * Calculates the visible world width for the given screen width.
     *
     * @param screenWidth screen width in pixels
     * @return world width visible.
     */
    double getVisibleWorldWidth(double screenWidth);
}
