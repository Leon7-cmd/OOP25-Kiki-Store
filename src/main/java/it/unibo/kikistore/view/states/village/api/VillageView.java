package it.unibo.kikistore.view.states.village.api;

import it.unibo.kikistore.model.item.api.GroundItem;
import it.unibo.kikistore.view.entity.api.EntityRenderData;
import it.unibo.kikistore.view.hud.api.HUDRenderData;
import javafx.scene.canvas.GraphicsContext;

import java.util.List;

/**
 * Interface representing the visual renderer for the village.
 */
@SuppressWarnings("PMD.ImplicitFunctionalInterface")
public interface VillageView {

    /**
     * Renders the village scene, entities, items, and HUD.
     *
     * @param gc          graphics context to draw onto
     * @param kikiData    data to render the player
     * @param groundItems collection of items currently spawned on the ground
     * @param hudData     data for rendering the HUD
     * @param frameCount  current frame counter for animations
     */
    void render(
        GraphicsContext gc,
        EntityRenderData kikiData,
        List<GroundItem> groundItems,
        HUDRenderData hudData,
        int frameCount
    );
}
