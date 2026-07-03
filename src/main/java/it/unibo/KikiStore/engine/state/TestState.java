package it.unibo.KikiStore.engine.state;

import it.unibo.KikiStore.controller.api.InputHandler;
import it.unibo.KikiStore.engine.api.GameState;
import it.unibo.KikiStore.model.map.impl.CollisionHandler;
import it.unibo.KikiStore.model.map.impl.MapLoader;
import it.unibo.KikiStore.model.map.impl.TileMapImpl;
import it.unibo.KikiStore.model.item.api.Item;
import it.unibo.KikiStore.model.item.impl.AbstractItemImpl;
import it.unibo.KikiStore.model.map.api.GameTile;
import it.unibo.KikiStore.model.player.impl.PlayerImpl;
import it.unibo.KikiStore.view.entity.api.EntityRenderData;
import it.unibo.KikiStore.view.entity.impl.EntityRenderer;
import it.unibo.KikiStore.view.environment.api.MapRenderData;
import it.unibo.KikiStore.view.environment.impl.MapRenderer;
import it.unibo.KikiStore.view.item.api.ItemRenderData;
import it.unibo.KikiStore.view.item.impl.ItemRenderer;
import it.unibo.KikiStore.view.utility.Camera;
import it.unibo.KikiStore.view.utility.SpriteManager;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * Concrete implementation of GameState used to test the integration of 
 * player movement, dual-layered maps (visual/collision), and camera scrolling.
 */
public class TestState implements GameState {

    private final InputHandler input;
    private final PlayerImpl kiki; 
    
    private final SpriteManager spriteManager;
    private final EntityRenderer entityRenderer;
    private final MapRenderer environmentRenderer;
    
    private Camera cam = new Camera();
    private int frameCount = 0;
    private final int[][] visualGrid;

    public TestState(InputHandler input) {
        this.input = input;
        
        // --- 1. RESOURCE LOADING ---
        this.visualGrid = MapLoader.loadMap("maps/test.txt");
        int[][] maskGrid = MapLoader.loadMap("maps/col/testCol.txt");
        
        // --- 2. MODEL INITIALIZATION ---
        GameTile collisionMap = new TileMapImpl(maskGrid, 64);
        CollisionHandler collisionHandler = new CollisionHandler(collisionMap);
        
        // Initialize the player and inject the collision logic
        this.kiki = new PlayerImpl(100, 100);
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
        MapRenderData mapData = new MapRenderData(visualGrid, 64);
        environmentRenderer.render(gc, mapData);

        EntityRenderData kikiData = new EntityRenderData(
            kiki.getX(), kiki.getY(), 64, 64, "sprites/player/kiki", kiki.getState(), kiki.getDirection()
        );
        entityRenderer.render(gc, List.of(kikiData), frameCount);

        gc.restore(); 
    }
}