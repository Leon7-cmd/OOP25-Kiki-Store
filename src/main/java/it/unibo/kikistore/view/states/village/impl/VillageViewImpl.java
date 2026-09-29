package it.unibo.kikistore.view.states.village.impl;

import it.unibo.kikistore.model.item.api.GroundItem;
import it.unibo.kikistore.view.entity.api.EntityRenderData;
import it.unibo.kikistore.view.entity.impl.EntityRenderer;
import it.unibo.kikistore.view.environment.api.MapRenderData;
import it.unibo.kikistore.view.environment.impl.MapRenderer;
import it.unibo.kikistore.view.hud.api.HUDRenderData;
import it.unibo.kikistore.view.hud.impl.HUDRenderer;
import it.unibo.kikistore.view.item.api.ItemRenderData;
import it.unibo.kikistore.view.item.impl.ItemRenderer;
import it.unibo.kikistore.view.states.village.api.VillageView;
import it.unibo.kikistore.view.utility.Camera;
import it.unibo.kikistore.view.utility.GridUtils;
import it.unibo.kikistore.view.utility.SpriteManager;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles the rendering logic and camera for the village scene.
 */
public final class VillageViewImpl implements VillageView {

    private static final int TILE_SIZE = 32;

    private final EntityRenderer entityRenderer;
    private final MapRenderer environmentRenderer;
    private final ItemRenderer itemRenderer;
    private final HUDRenderer hudRenderer;
    private final Camera camera;

    private final int[][] groundGrid;
    private final int[][] decorationGrid;
    private final int[][] upperGrid;

    /**
     * Constructs a new VillageViewImpl.
     *
     * @param spriteManager  the sprite manager for graphical assets
     * @param groundGrid     ground layer matrix
     * @param decorationGrid decoration layer matrix
     * @param upperGrid      foreground layer matrix
     */
    public VillageViewImpl(
        final SpriteManager spriteManager,
        final int[][] groundGrid,
        final int[][] decorationGrid,
        final int[][] upperGrid
    ) {
        this.groundGrid = GridUtils.deepCopyGrid(groundGrid);
        this.decorationGrid = GridUtils.deepCopyGrid(decorationGrid);
        this.upperGrid = GridUtils.deepCopyGrid(upperGrid);

        this.camera = new Camera();
        this.entityRenderer = new EntityRenderer(spriteManager);
        this.environmentRenderer = new MapRenderer(spriteManager);
        this.itemRenderer = new ItemRenderer(spriteManager);
        this.hudRenderer = new HUDRenderer(spriteManager);
    }

    @Override
    public void render(
        final GraphicsContext gc,
        final EntityRenderData kikiData,
        final List<GroundItem> groundItems,
        final HUDRenderData hudData,
        final int frameCount
    ) {
        final double screenWidth = gc.getCanvas().getWidth();
        final double screenHeight = gc.getCanvas().getHeight();

        gc.save();
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, screenWidth, screenHeight);

        // --- CAMERA LOGIC ---
        camera.update(kikiData.x(), kikiData.y(), screenWidth, screenHeight);
        gc.setImageSmoothing(false);
        gc.scale(camera.getZoom(), camera.getZoom());
        gc.translate(-camera.getX(), -camera.getY());

        // --- WORLD RENDERING ---
        environmentRenderer.render(gc, new MapRenderData(groundGrid, TILE_SIZE));
        environmentRenderer.render(gc, new MapRenderData(decorationGrid, TILE_SIZE));

        // Ground Items Layer
        final List<ItemRenderData> itemDataList = new ArrayList<>();
        for (final GroundItem item : groundItems) {
            itemDataList.add(new ItemRenderData(
                item.getX(),
                item.getY(),
                item.getWidth(),
                item.getHeight(),
                item.getId(),
                item.isAnimated()
            ));
        }
        itemRenderer.render(gc, itemDataList, frameCount);

        // Player Layer
        entityRenderer.render(gc, List.of(kikiData), frameCount);

        // Foreground Layer
        environmentRenderer.render(gc, new MapRenderData(upperGrid, TILE_SIZE));
        gc.restore();

        // --- HUD ---
        hudRenderer.render(gc, hudData);
    }
}
