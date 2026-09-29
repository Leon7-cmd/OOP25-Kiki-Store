package it.unibo.kikistore.view.states.minigamefly.impl;

import it.unibo.kikistore.view.entity.api.EntityRenderData;
import it.unibo.kikistore.view.entity.impl.EntityRenderer;
import it.unibo.kikistore.view.environment.api.MapRenderData;
import it.unibo.kikistore.view.environment.impl.MapRenderer;
import it.unibo.kikistore.view.hud.api.HUDRenderData;
import it.unibo.kikistore.view.hud.impl.HUDRenderer;
import it.unibo.kikistore.view.states.minigamefly.api.MinigameFlyView;
import it.unibo.kikistore.view.utility.Camera;
import it.unibo.kikistore.view.utility.GridUtils;
import it.unibo.kikistore.view.utility.SpriteManager;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

import java.util.List;

/**
 * Concrete implementation of MinigameFlyView.
 */
public final class MinigameFlyViewImpl implements MinigameFlyView {

    private static final int TILE_SIZE = 32;
    private static final double MINIGAME_ZOOM = 2.5;
    private static final double ALPHA_OVERLAY = 0.75;
    private static final String FONT_FAMILY = "Verdana";
    private static final double INSTRUCTION_FONT_SIZE = 11.0;
    private static final double PROMPT_FONT_SIZE = 12.0;
    private static final double INSTRUCTION_OFFSET_Y = 28.0;
    private static final double ACTION_PROMPT_OFFSET_Y = 68.0;

    private static final double BOX_WIDTH = 250.0;
    private static final double BOX_HEIGHT = 90.0;
    private static final double BOX_RADIUS = 12.0;
    private static final double BOX_BORDER_WIDTH = 2.0;

    private static final Color CREAM_BG = Color.web("#f7eedb", 0.95);
    private static final Color CREAM_BORDER = Color.web("#8b5a2b");
    private static final Color TEXT_COLOR = Color.web("#3a2512");
    private static final Color KEY_COLOR = Color.web("#2b6e26");

    private final EntityRenderer entityRenderer;
    private final MapRenderer environmentRenderer;
    private final HUDRenderer hudRenderer;
    private final Camera camera;

    private final int[][] groundGrid;
    private final int[][] decorationGrid;

    /**
     * Constructs a new MinigameFlyViewImpl.
     *
     * @param spriteManager  shared asset manager
     * @param groundGrid     ground layer matrix
     * @param decorationGrid decoration layer matrix
     */
    public MinigameFlyViewImpl(
        final SpriteManager spriteManager,
        final int[][] groundGrid,
        final int[][] decorationGrid
    ) {
        this.groundGrid = GridUtils.deepCopyGrid(groundGrid);
        this.decorationGrid = GridUtils.deepCopyGrid(decorationGrid);

        this.camera = new Camera();
        this.camera.setZoom(MINIGAME_ZOOM);

        this.entityRenderer = new EntityRenderer(spriteManager);
        this.environmentRenderer = new MapRenderer(spriteManager);
        this.hudRenderer = new HUDRenderer(spriteManager);
    }

    @Override
    public double getVisibleWorldWidth(final double screenWidth) {
        return screenWidth / MINIGAME_ZOOM;
    }

    @Override
    public void render(
        final GraphicsContext gc,
        final EntityRenderData kikiData,
        final HUDRenderData hudData,
        final double cameraLeft,
        final boolean showPrompt,
        final int frameCount
    ) {
        final double screenWidth = gc.getCanvas().getWidth();
        final double screenHeight = gc.getCanvas().getHeight();
        final double mapCenterY = groundGrid.length * TILE_SIZE / 2.0;
        final double viewW = screenWidth / camera.getZoom();
        final double cameraCenterX = cameraLeft + (viewW / 2.0);

        camera.update(cameraCenterX, mapCenterY, screenWidth, screenHeight);

        gc.save();
        gc.setFill(Color.BLACK);
        gc.fillRect(0.0, 0.0, screenWidth, screenHeight);

        gc.setImageSmoothing(false);
        gc.scale(camera.getZoom(), camera.getZoom());
        gc.translate(-camera.getX(), -camera.getY());

        // --- MAP AND ENTITY RENDERING ---
        environmentRenderer.render(gc, new MapRenderData(groundGrid, TILE_SIZE));
        environmentRenderer.render(gc, new MapRenderData(decorationGrid, TILE_SIZE));
        entityRenderer.render(gc, List.of(kikiData), frameCount);

        // --- PROMPT OVERLAY ---
        if (showPrompt) {
            gc.setGlobalAlpha(ALPHA_OVERLAY);
            gc.setFill(Color.BLACK);
            gc.fillRect(camera.getX(), camera.getY(), camera.getW(), camera.getH());
            gc.setGlobalAlpha(1.0);

            final double boxX = camera.getX() + (camera.getW() - BOX_WIDTH) / 2.0;
            final double boxY = camera.getY() + (camera.getH() - BOX_HEIGHT) / 2.0;
            final double centerX = boxX + (BOX_WIDTH / 2.0);

            gc.setFill(CREAM_BG);
            gc.fillRoundRect(boxX, boxY, BOX_WIDTH, BOX_HEIGHT, BOX_RADIUS, BOX_RADIUS);

            gc.setStroke(CREAM_BORDER);
            gc.setLineWidth(BOX_BORDER_WIDTH);
            gc.strokeRoundRect(boxX, boxY, BOX_WIDTH, BOX_HEIGHT, BOX_RADIUS, BOX_RADIUS);

            gc.setFill(TEXT_COLOR);
            gc.setFont(Font.font(FONT_FAMILY, FontWeight.NORMAL, INSTRUCTION_FONT_SIZE));
            gc.setTextAlign(TextAlignment.CENTER);
            gc.setTextBaseline(VPos.CENTER);
            gc.fillText(
                "Non farti raggiungere dallo schermo\nEvita gli ostacoli e arriva alla fine",
                centerX,
                boxY + INSTRUCTION_OFFSET_Y
            );

            gc.setFill(KEY_COLOR);
            gc.setFont(Font.font(FONT_FAMILY, FontWeight.BOLD, PROMPT_FONT_SIZE));
            gc.fillText("Premi \"E\" per iniziare", centerX, boxY + ACTION_PROMPT_OFFSET_Y);
        }

        gc.restore();

        // --- HUD ---
        hudRenderer.render(gc, hudData);
    }
}
