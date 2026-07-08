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

/**
 * Concrete implementation of GameState used to test the integration of 
 * player movement, dual-layered maps (visual/collision), and camera scrolling.
 */
public final class ShopState implements GameState {
    private static final int PLAYER_X = 870;
    private static final int PLAYER_Y = 920;
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
    public ShopState(final GameStateTransition transitionController, final InputHandler input) {
        this.transitionController = transitionController;
        this.input = input;

        // --- 1. RESOURCE LOADING ---
        this.groundGrid = MapLoader.loadMap("maps/shop/shopGround.txt");
        this.decorationGrid = MapLoader.loadMap("maps/shop/shopDecor.txt");
        this.maskGrid = MapLoader.loadMap("maps/shop/col/col.txt");

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
        kiki.update(input);
        frameCount++;

        final int tileId = collisionHandler.getInteractableTileId(kiki.getX() + 16, kiki.getY() + 32, 32, 32);
        if (tileId == 2 && input.isAction()) {
            transitionController.popState();
        }
        if (tileId == 3 && input.isAction()) {
            System.out.println("Interacting with the book!");
        }
        if (tileId == 4 && input.isAction()) {
            System.out.println("Interacting with the shopkeeper!");
        }
    }

    @Override
    public void render(final GraphicsContext gc) {
        final double screenWidth = gc.getCanvas().getWidth(); 
        final double screenHeight = gc.getCanvas().getHeight();

        // Clear the screen with a solid background color
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, screenWidth, screenHeight);

        // --- CAMERA LOGIC ---
        gc.save(); 
        cam.update(kiki.getX(), kiki.getY(), screenWidth, screenHeight);
        gc.translate(-cam.getX(), -cam.getY()); 

        // --- WORLD RENDERING ---
        environmentRenderer.render(gc, new MapRenderData(groundGrid, 32));
        environmentRenderer.render(gc, new MapRenderData(decorationGrid, 32));
        final EntityRenderData kikiData = new EntityRenderData(
            kiki.getX(), kiki.getY(), 64, 64, "sprites/player/kiki", kiki.getState(), kiki.getDirection()
        );
        entityRenderer.render(gc, List.of(kikiData), frameCount);

        gc.restore(); 
    }
}
