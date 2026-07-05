package it.unibo.KikiStore.engine.state;

import java.util.List;

import it.unibo.KikiStore.controller.api.InputHandler;
import it.unibo.KikiStore.engine.api.GameState;
import it.unibo.KikiStore.engine.api.GameStateTransition;
import it.unibo.KikiStore.model.map.api.GameTile;
import it.unibo.KikiStore.model.map.impl.CollisionHandler;
import it.unibo.KikiStore.model.map.impl.MapLoader;
import it.unibo.KikiStore.model.map.impl.TileMapImpl;
import it.unibo.KikiStore.model.player.impl.PlayerImpl;
import it.unibo.KikiStore.view.entity.api.EntityRenderData;
import it.unibo.KikiStore.view.entity.impl.EntityRenderer;
import it.unibo.KikiStore.view.environment.api.MapRenderData;
import it.unibo.KikiStore.view.environment.impl.MapRenderer;
import it.unibo.KikiStore.view.hud.api.HUDRenderData;
import it.unibo.KikiStore.view.hud.impl.HUDRenderer;
import it.unibo.KikiStore.view.utility.Camera;
import it.unibo.KikiStore.view.utility.SpriteManager;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * GameState implementation for the fly minigame.
 * Handles side scrolling mechanics, camera progression, and win/loss conditions.
 */
public final class MinigameFly implements GameState {
    private static final int PLAYER_X = 200;
    private static final int PLAYER_Y = 180;
    private static final double GAME_SPEED = 2;
    private static final int TILE_SIZE = 32;
    private static final int PLAYER_SIZE = 64;
    private static final double ALPHA_OVERLAY = 0.75;
    private static final int INTERACTABLE_END_TILE_ID = 2;
    private static final int REWARD = 30;

    private double cameraStart;
    private boolean gameStart;
    private boolean gameEnd;

    private final InputHandler input;
    private final PlayerImpl kiki; 

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
     * Constructor for the MinigameFly state.
     * 
     * @param transitionController handles state popping and switching
     * @param input manages player controls
     */
    public MinigameFly(final GameStateTransition transitionController, final InputHandler input) {
        this.transitionController = transitionController;
        this.input = input;

        // --- 1. RESOURCE LOADING ---
        this.groundGrid = MapLoader.loadMap("maps/minigameFly/ground.txt");
        this.decorationGrid = MapLoader.loadMap("maps/minigameFly/decor.txt");
        this.maskGrid = MapLoader.loadMap("maps/minigameFly/col/col.txt");

        // --- 2. MODEL INITIALIZATION ---
        final GameTile collisionMap = new TileMapImpl(maskGrid, TILE_SIZE);
        collisionHandler = new CollisionHandler(collisionMap);

        // Initialize the player and inject the collision logic
        this.kiki = new PlayerImpl(PLAYER_X, PLAYER_Y);
        this.kiki.setCollisionHandler(collisionHandler); 

        // --- 3. VIEW INITIALIZATION ---
        this.spriteManager = new SpriteManager();
        this.entityRenderer = new EntityRenderer(this.spriteManager);
        this.environmentRenderer = new MapRenderer(this.spriteManager);
        this.hudRenderer = new HUDRenderer(this.spriteManager);
    }

    @Override
    public void init() { }

    @Override
    public void update() {
        final int tileId = collisionHandler.getInteractableTileId(
            kiki.getX() + (TILE_SIZE / 2), kiki.getY() + TILE_SIZE, TILE_SIZE, TILE_SIZE
        );
        frameCount++;

        // GAME END and START CHECK
        if (gameStart && !gameEnd) {
            kiki.update(input);
        }
        if (input.isAction() && !gameEnd) {
            gameStart = true;
        }

        // WIN CONDITION CHECK
        if (tileId == INTERACTABLE_END_TILE_ID && !gameEnd) {
            gameEnd = true;
            gameStart = false;
            kiki.setMoney(kiki.getMoney() + REWARD);
        }

        // OUT OF BOUNDS CHECK
        if (kiki.getX() > cam.getX() + cam.getW() - TILE_SIZE) {
            kiki.setX(cam.getX() + cam.getW() - TILE_SIZE);
        }
        if (kiki.getX() < cam.getX() - PLAYER_SIZE || gameEnd && input.isAction()) {
            transitionController.popState();
        }
    }

    @Override
    public void render(final GraphicsContext gc) {
        final double screenWidth = gc.getCanvas().getWidth(); 
        final double screenHeight = gc.getCanvas().getHeight();
        if (cameraStart == 0) {
            cameraStart = screenWidth * 0.50;
        } else if (gameStart) {
            cameraStart += GAME_SPEED;
        }
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, screenWidth, screenHeight);

        // --- CAMERA LOGIC ---
        gc.save(); 
        cam.update(cameraStart, screenHeight * 0.50, screenWidth, screenHeight);
        gc.translate(-cam.getX(), -cam.getY()); 

        // --- WORLD RENDERING ---
        environmentRenderer.render(gc, new MapRenderData(groundGrid, TILE_SIZE));
        environmentRenderer.render(gc, new MapRenderData(decorationGrid, TILE_SIZE));
        final EntityRenderData kikiData = new EntityRenderData(
            kiki.getX(), kiki.getY(), PLAYER_SIZE, PLAYER_SIZE, "sprites/player/kiki", kiki.getState(), kiki.getDirection()
        );
        entityRenderer.render(gc, List.of(kikiData), frameCount);

        if (!gameStart && !gameEnd) {
            gc.setGlobalAlpha(ALPHA_OVERLAY);
            gc.setFill(Color.BLACK);
            gc.fillRect(TILE_SIZE, TILE_SIZE, screenWidth, screenHeight);
            gc.setGlobalAlpha(1);
            gc.setFill(Color.GREEN);
            gc.setFont(Font.font("Verdana"));
            gc.fillText("Premi \"E\"", screenWidth * 0.50, screenHeight * 0.50);
        }
        gc.restore(); 

        // --- HUD ---
        final HUDRenderData hudData = new HUDRenderData(kiki.getEnergy(), 5, kiki.getMoney());
        hudRenderer.render(gc, hudData);
    }
}
