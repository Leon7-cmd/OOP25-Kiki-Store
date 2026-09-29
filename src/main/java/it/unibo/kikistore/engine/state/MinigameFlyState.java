package it.unibo.kikistore.engine.state;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import it.unibo.kikistore.controller.api.InputHandler;
import it.unibo.kikistore.controller.api.PlayerController;
import it.unibo.kikistore.engine.api.GameState;
import it.unibo.kikistore.engine.api.GameStateTransition;
import it.unibo.kikistore.model.map.api.GameTile;
import it.unibo.kikistore.model.map.impl.CollisionHandler;
import it.unibo.kikistore.model.map.impl.MapLoader;
import it.unibo.kikistore.model.map.impl.TileMapImpl;
import it.unibo.kikistore.model.utility.BoundingBox;
import it.unibo.kikistore.view.hud.api.HUDRenderData;
import it.unibo.kikistore.view.states.minigamefly.api.MinigameFlyView;
import it.unibo.kikistore.view.states.minigamefly.impl.MinigameFlyViewImpl;
import it.unibo.kikistore.view.utility.SpriteManager;
import javafx.scene.canvas.GraphicsContext;

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
    private static final int INTERACTABLE_END_TILE_ID = 2;
    private static final int REWARD = 30;
    private static final int ENERGY_RESTORED = 2;

    // Rendering & Camera Constants
    private static final double CAMERA_INITIAL_OFFSET_X = 150.0;
    private static final double DEFAULT_VIEWPORT_WIDTH = 320.0;

    private double cameraLeft;
    private double currentViewportWidth = DEFAULT_VIEWPORT_WIDTH;
    private boolean initializedCamera;
    private boolean gameStart;
    private boolean gameEnd;

    private final InputHandler input;
    private final PlayerController kiki; 

    private final GameStateTransition transitionController;
    private final CollisionHandler collisionHandler;
    private final MinigameFlyView minigameFlyView;

    private int frameCount;

    /**
     * Constructs a new MinigameFlyState.
     *
     * @param stateController the GameStateTransition controller for managing state transitions
     * @param input           the InputHandler for user input
     * @param kiki            the PlayerController for controlling the player character
     * @param spriteManager   the shared SpriteManager for loading graphics
     */
    public MinigameFlyState(
        final GameStateTransition stateController,
        final InputHandler input,
        final PlayerController kiki,
        final SpriteManager spriteManager
    ) {
        this.transitionController = stateController;
        this.input = input;
        this.kiki = kiki;

        // --- 1. RESOURCE LOADING ---
        final int[][] groundGrid = MapLoader.loadMap("maps/minigameFly/ground.txt");
        final int[][] decorationGrid = MapLoader.loadMap("maps/minigameFly/decor.txt");
        final int[][] maskGrid = MapLoader.loadMap("maps/minigameFly/col/col.txt");

        // --- 2. MODEL INITIALIZATION ---
        final GameTile collisionMap = new TileMapImpl(maskGrid, TILE_SIZE);
        this.collisionHandler = new CollisionHandler(collisionMap);

        // --- 3. VIEW INITIALIZATION ---
        this.minigameFlyView = new MinigameFlyViewImpl(spriteManager, groundGrid, decorationGrid);
    }

    @Override
    public void init() {
        this.kiki.setPosition(PLAYER_X, PLAYER_Y);
    }

    @Override
    public void update() {
        frameCount++;

        // 1. VICTORY
        if (gameEnd) {
            if (input.isAction()) {
                transitionController.popState(true);
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
        kiki.update(collisionHandler);
        cameraLeft += GAME_SPEED;

        // Check for victory condition
        final BoundingBox interactionArea = kiki.getHitbox();
        final int tileId = collisionHandler.getInteractableTileId(
            interactionArea.x(),
            interactionArea.y(),
            interactionArea.width(),
            interactionArea.height()
        );

        if (tileId == INTERACTABLE_END_TILE_ID) {
            gameEnd = true;
            kiki.addMoney(REWARD);
            kiki.restoreEnergy(ENERGY_RESTORED);
            return;
        }

        // Keep the player within the boundary
        final double rightBoundary = cameraLeft + currentViewportWidth;
        if (kiki.getX() > rightBoundary) {
            kiki.setPosition(rightBoundary, kiki.getY());
        }

        // Lose condition
        if (kiki.getX() < cameraLeft) {
            transitionController.popState(true);
        }
    }

    @Override
    public void render(final GraphicsContext gc) {
        // --- CAMERA LOGIC --- 
        if (!initializedCamera) {
            this.cameraLeft = Math.max(0.0, kiki.getX() - CAMERA_INITIAL_OFFSET_X);
            this.currentViewportWidth = minigameFlyView.getVisibleWorldWidth(gc.getCanvas().getWidth());
            this.initializedCamera = true;
        }

        // --- HUD ---
        final HUDRenderData hudData = new HUDRenderData(kiki.getEnergy(), kiki.maxEnergy(), kiki.getMoney());
        final boolean showPrompt = !gameStart && !gameEnd;

        minigameFlyView.render(gc, kiki.toRenderData(), hudData, cameraLeft, showPrompt, frameCount);
    }

    @Override
    public void pause() { }

    @Override
    public void resume() { }
}
