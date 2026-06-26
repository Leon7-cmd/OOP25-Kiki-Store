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
import it.unibo.KikiStore.view.utility.Camera;
import it.unibo.KikiStore.view.utility.SpriteManager;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public final class MinigameFly implements GameState{
    private static final int PLAYER_X = 300;
    private static final int PLAYER_Y = 60;
    private static final double CAMERA_SPEED = 3;
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

    private final Camera cam = new Camera();
    private int frameCount;
    private final int[][] groundGrid;
    private final int[][] decorationGrid;
    private final int[][] maskGrid;

    /**
     * Constructs a TestState with the required controller systems and loads map resources.
     * 
     * @param transitionController is used to switch from one state to another
     * @param input  controls every input from the player
     */
    public MinigameFly(final GameStateTransition transitionController, final InputHandler input) {
        this.transitionController = transitionController;
        this.input = input;

        // --- 1. RESOURCE LOADING ---
        this.groundGrid = MapLoader.loadMap("maps/minigameFly/ground.txt");
        this.decorationGrid = MapLoader.loadMap("maps/minigameFly/decor.txt");
        this.maskGrid = MapLoader.loadMap("maps/minigameFly/col/col.txt");

        // --- 2. MODEL INITIALIZATION ---
        final GameTile collisionMap = new TileMapImpl(maskGrid, 32);
        collisionHandler = new CollisionHandler(collisionMap);

        // Initialize the player and inject the collision logic
        this.kiki = new PlayerImpl(PLAYER_X, PLAYER_Y);
        this.kiki.setCollisionHandler(collisionHandler); 

        // --- 3. VIEW INITIALIZATION ---
        this.spriteManager = new SpriteManager();
        this.entityRenderer = new EntityRenderer(spriteManager);
        this.environmentRenderer = new MapRenderer(spriteManager);
    }

    @Override
    public void init() { }

    @Override
    public void update() {
        final int tileId = collisionHandler.getInteractableTileId(kiki.getX() + 16, kiki.getY() + 32, 32, 32);
        if (gameStart && !gameEnd) {
            kiki.update(input);
        }
        frameCount++;
        if (tileId == 2) {
            gameEnd = true;
            gameStart = false;
        }
        if (kiki.getX() < (cam.getX()) || (gameEnd && input.isAction())) {
            transitionController.popState();
        }
        if (input.isAction() && !gameEnd) {
            gameStart = true;
        }
    }

    @Override
    public void render(final GraphicsContext gc) {
        final double screenWidth = gc.getCanvas().getWidth(); 
        final double screenHeight = gc.getCanvas().getHeight();
        if (cameraStart == 0 ) {
            cameraStart = screenWidth*0.50;
        } else if (gameStart) {
            cameraStart += CAMERA_SPEED;
        }

        // Clear the screen with a solid background color
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, screenWidth, screenHeight);

        // --- CAMERA LOGIC ---
        gc.save(); 
        cam.update(cameraStart, screenHeight*0.50, screenWidth, screenHeight);
        gc.translate(-cam.getX(), -cam.getY()); 

        // --- WORLD RENDERING ---
        environmentRenderer.render(gc, new MapRenderData(groundGrid, 32));
        environmentRenderer.render(gc, new MapRenderData(decorationGrid, 32));
        final EntityRenderData kikiData = new EntityRenderData(
            kiki.getX(), kiki.getY(), 64, 64, "sprites/player/kiki", kiki.getState(), kiki.getDirection()
        );
        entityRenderer.render(gc, List.of(kikiData), frameCount);

        if (!gameStart && !gameEnd) {
            gc.setGlobalAlpha(0.75);
            gc.setFill(Color.BLACK);
            gc.fillRect(32, 32, screenWidth, screenHeight);
            gc.setGlobalAlpha(1);
            gc.setFill(Color.GREEN);
            gc.setFont(Font.font("Verdana"));
            gc.fillText("Premi \"E\"", screenWidth*0.50, screenHeight*0.50);
        }

        gc.restore(); 
    }
}
