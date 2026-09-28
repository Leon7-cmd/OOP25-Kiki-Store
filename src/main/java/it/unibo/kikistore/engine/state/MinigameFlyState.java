package it.unibo.kikistore.engine.state;

import java.util.List;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import it.unibo.kikistore.controller.api.InputHandler;
import it.unibo.kikistore.controller.api.PlayerController;
import it.unibo.kikistore.engine.api.GameState;
import it.unibo.kikistore.engine.api.GameStateTransition;
import it.unibo.kikistore.model.map.api.GameTile;
import it.unibo.kikistore.model.map.impl.CollisionHandler;
import it.unibo.kikistore.model.map.impl.MapLoader;
import it.unibo.kikistore.model.map.impl.TileMapImpl;
import it.unibo.kikistore.view.entity.api.EntityRenderData;
import it.unibo.kikistore.view.entity.impl.EntityRenderer;
import it.unibo.kikistore.view.environment.api.MapRenderData;
import it.unibo.kikistore.view.environment.impl.MapRenderer;
import it.unibo.kikistore.view.hud.api.HUDRenderData;
import it.unibo.kikistore.view.hud.impl.HUDRenderer;
import it.unibo.kikistore.view.utility.Camera;
import it.unibo.kikistore.view.utility.SpriteManager;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

/**
 * GameState implementation for the fly minigame.
 * Handles side scrolling mechanics, camera progression, and win/loss conditions.
 */
@SuppressFBWarnings(
    value = "EI_EXPOSE_REP2",
    justification = "GameStateTransition and PlayerController are shared components for world and player management"
)
public final class MinigameFlyState implements GameState {

    // Gameplay Constants
    private static final int PLAYER_X = 200;
    private static final int PLAYER_Y = 180;
    private static final double GAME_SPEED = 1.0;
    private static final int TILE_SIZE = 32;
    private static final int PLAYER_SIZE = 64;
    private static final int INTERACTABLE_END_TILE_ID = 2;
    private static final int REWARD = 30;
    private static final int ENERGY_RESTORED = 2;

    // Rendering & Camera Constants
    private static final double MINIGAME_ZOOM = 2.5;
    private static final double CAMERA_INITIAL_OFFSET_X = 150.0;
    private static final double ALPHA_OVERLAY = 0.75;

    // Font Constants
    private static final String FONT_FAMILY = "Verdana";
    private static final double PROMPT_FONT_SIZE = 22.0;

    private double cameraLeft;
    private boolean initializedCamera;
    private boolean gameStart;
    private boolean gameEnd;

    private final InputHandler input;
    private final PlayerController kiki; 

    private final GameStateTransition transitionController;
    private final CollisionHandler collisionHandler;
    private final SpriteManager spriteManager;
    private final EntityRenderer entityRenderer;
    private final MapRenderer environmentRenderer;
    private final HUDRenderer hudRenderer;

    private final Camera cam = new Camera();
    private int frameCount;
    private final int[][] groundGrid;
    private final int[][] decorationGrid;
    private final int[][] maskGrid;

    /**
     * Constructs a new MinigameFlyState.
     *
     * @param stateController the GameStateTransition controller for managing state transitions
     * @param input           the InputHandler for user input
     * @param kiki            the PlayerController for controlling the player character
     */
    public MinigameFlyState(
        final GameStateTransition stateController,
        final InputHandler input,
        final PlayerController kiki
    ) {
        this.transitionController = stateController;
        this.input = input;

        // --- 1. RESOURCE LOADING ---
        this.groundGrid = MapLoader.loadMap("maps/minigameFly/ground.txt");
        this.decorationGrid = MapLoader.loadMap("maps/minigameFly/decor.txt");
        this.maskGrid = MapLoader.loadMap("maps/minigameFly/col/col.txt");

        // --- 2. MODEL INITIALIZATION ---
        final GameTile collisionMap = new TileMapImpl(maskGrid, TILE_SIZE);
        collisionHandler = new CollisionHandler(collisionMap);
        this.kiki = kiki;

        // --- 3. VIEW INITIALIZATION ---
        this.spriteManager = new SpriteManager();
        this.entityRenderer = new EntityRenderer(this.spriteManager);
        this.environmentRenderer = new MapRenderer(this.spriteManager);
        this.hudRenderer = new HUDRenderer(this.spriteManager);

        this.cam.setZoom(MINIGAME_ZOOM);
    }

    @Override
    public void init() {
        this.kiki.setPosition(PLAYER_X, PLAYER_Y);
        this.kiki.setCollisionHandler(this.collisionHandler);
        this.cam.setZoom(MINIGAME_ZOOM);
    }

    @Override
    public void update() {
        frameCount++;

        // 1. VICTORY
        if (gameEnd) {
            if (input.isAction()) {
                transitionController.popState();
            }
            return;
        }

        // 2. READY
        if (!gameStart) {
            if (input.isAction()) {
                gameStart = true;
            }
            return;
        }

        // 3. RUNNING
        kiki.update();
        cameraLeft += GAME_SPEED;

        // Check for victory condition
        final int tileId = collisionHandler.getInteractableTileId(
            kiki.getX() + (TILE_SIZE / 2.0), kiki.getY() + TILE_SIZE, TILE_SIZE, TILE_SIZE
        );
        if (tileId == INTERACTABLE_END_TILE_ID) {
            gameEnd = true;
            kiki.addMoney(REWARD);
            kiki.restoreEnergy(ENERGY_RESTORED);
            return;
        }

        // Keep the player within the boundary
        if (kiki.getX() > cam.getX() + cam.getW() - PLAYER_SIZE) {
            kiki.setPosition(cam.getX() + cam.getW() - PLAYER_SIZE, kiki.getY());
        }

        // Lose condition
        if (kiki.getX() < cam.getX() - PLAYER_SIZE) {
            transitionController.popState();
        }
    }

    @Override
    public void render(final GraphicsContext gc) {
        final double screenWidth = gc.getCanvas().getWidth(); 
        final double screenHeight = gc.getCanvas().getHeight();
        final double mapCenterY = groundGrid.length * TILE_SIZE / 2.0;

        final double viewW = screenWidth / cam.getZoom();

        // --- CAMERA LOGIC --- 
        if (!initializedCamera) {
            this.cameraLeft = Math.max(0.0, kiki.getX() - CAMERA_INITIAL_OFFSET_X);
            this.initializedCamera = true;
        }

        final double cameraCenterX = cameraLeft + (viewW / 2.0);
        cam.update(cameraCenterX, mapCenterY, screenWidth, screenHeight);

        gc.save();

        gc.setFill(Color.BLACK);
        gc.fillRect(0.0, 0.0, screenWidth, screenHeight);

        gc.setImageSmoothing(false);
        gc.scale(cam.getZoom(), cam.getZoom());
        gc.translate(-cam.getX(), -cam.getY());

        // --- WORLD RENDERING ---
        environmentRenderer.render(gc, new MapRenderData(groundGrid, TILE_SIZE));
        environmentRenderer.render(gc, new MapRenderData(decorationGrid, TILE_SIZE));
        final EntityRenderData kikiData = new EntityRenderData(
            kiki.getX(),
            kiki.getY(),
            PLAYER_SIZE,
            PLAYER_SIZE,
            "sprites/player/kiki",
            kiki.getState(),
            kiki.getDirection()
        );
        entityRenderer.render(gc, List.of(kikiData), frameCount);

        if (!gameStart && !gameEnd) {
            gc.setGlobalAlpha(ALPHA_OVERLAY);
            gc.setFill(Color.BLACK);
            gc.fillRect(cam.getX(), cam.getY(), cam.getW(), cam.getH());
            gc.setGlobalAlpha(1.0);
            gc.setFill(Color.GREEN);
            gc.setFont(Font.font(FONT_FAMILY, PROMPT_FONT_SIZE));
            gc.setTextAlign(TextAlignment.CENTER);
            gc.setTextBaseline(VPos.CENTER);
            gc.fillText("Premi \"E\"", cam.getX() + (cam.getW() / 2.0), cam.getY() + (cam.getH() / 2.0));
        }

        gc.restore(); 

        // --- HUD ---
        final HUDRenderData hudData = new HUDRenderData(kiki.getEnergy(), kiki.maxEnergy(), kiki.getMoney());
        hudRenderer.render(gc, hudData);
    }

    @Override
    public void pause() { }

    @Override
    public void resume() { }
}
