package it.unibo.KikiStore.engine.state;

import it.unibo.KikiStore.controller.api.InputHandler;
import it.unibo.KikiStore.engine.api.GameState;
import it.unibo.KikiStore.engine.api.GameStateTransition;
import it.unibo.KikiStore.model.map.impl.CollisionHandler;
import it.unibo.KikiStore.model.map.impl.MapLoader;
import it.unibo.KikiStore.model.map.impl.TileMapImpl;
import it.unibo.KikiStore.model.map.api.GameTile;
import it.unibo.KikiStore.model.player.impl.PlayerImpl;
import it.unibo.KikiStore.view.entity.api.EntityRenderData;
import it.unibo.KikiStore.view.entity.impl.EntityRenderer;
import it.unibo.KikiStore.view.environment.api.MapRenderData;
import it.unibo.KikiStore.view.environment.impl.MapRenderer;
import it.unibo.KikiStore.view.utility.Camera;
import it.unibo.KikiStore.view.utility.SpriteManager;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.List;

public class TestTwoState implements GameState{
    private final InputHandler input;
    private final PlayerImpl kiki; 
    
    private final GameStateTransition transitionController;
    private final CollisionHandler collisionHandler;
    private final SpriteManager spriteManager;
    private final EntityRenderer entityRenderer;
    private final MapRenderer environmentRenderer;
    
    private Camera cam = new Camera();
    private int frameCount = 0;
    private final int[][] groundGrid;
    private final int[][] decorationGrid;
    private final int[][] upperGrid;
    private final int[][] maskGrid;

    public TestTwoState(GameStateTransition transitionController ,InputHandler input) {
        this.transitionController = transitionController;
        this.input = input;
        
        // --- 1. RESOURCE LOADING ---
        this.groundGrid = MapLoader.loadMap("maps/map1/testGround.txt");
        this.decorationGrid = MapLoader.loadMap("maps/map1/testDecor.txt");
        this.upperGrid = MapLoader.loadMap("maps/map1/testUpper.txt");
        this.maskGrid = MapLoader.loadMap("maps/map1/col/testCol.txt");
        
        // --- 2. MODEL INITIALIZATION ---
        GameTile collisionMap = new TileMapImpl(maskGrid, 32);
        collisionHandler = new CollisionHandler(collisionMap);
        
        // Initialize the player and inject the collision logic
        this.kiki = new PlayerImpl(220, 220);
        this.kiki.setCollisionHandler(collisionHandler); 
        
        // --- 3. VIEW INITIALIZATION ---
        this.spriteManager = new SpriteManager();
        this.entityRenderer = new EntityRenderer(spriteManager);
        this.environmentRenderer = new MapRenderer(spriteManager);
    }

    @Override
    public void init() {}

    @Override
    public void update() {
        kiki.update(input);
        frameCount++;

        int tileId = collisionHandler.getInteractableTileId(kiki.getX() + 16, kiki.getY() + 32, 32, 32);
        if (tileId == 2 && input.isAction()) {
            GameState newState = new TestState(transitionController, input);
            transitionController.pushState(newState);
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        double screenWidth = gc.getCanvas().getWidth(); 
        double screenHeight = gc.getCanvas().getHeight();
        
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
        EntityRenderData kikiData = new EntityRenderData(
            kiki.getX(), kiki.getY(), 64, 64, "sprites/player/kiki", kiki.getState(), kiki.getDirection()
        );
        entityRenderer.render(gc, List.of(kikiData), frameCount);
        environmentRenderer.render(gc, new MapRenderData(upperGrid, 32));

        gc.restore(); 
    }
}
