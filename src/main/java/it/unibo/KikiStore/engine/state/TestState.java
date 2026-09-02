package it.unibo.KikiStore.engine.state;

import it.unibo.KikiStore.controller.api.InputHandler;
import it.unibo.KikiStore.controller.api.PlayerController;
import it.unibo.KikiStore.controller.impl.PlayerControllerImpl;
import it.unibo.KikiStore.engine.api.GameState;
import it.unibo.KikiStore.engine.api.GameStateTransition;
import it.unibo.KikiStore.model.map.api.GameTile;
import it.unibo.KikiStore.model.map.impl.CollisionHandler;
import it.unibo.KikiStore.model.map.impl.MapLoader;
import it.unibo.KikiStore.model.map.impl.TileMapImpl;
import it.unibo.KikiStore.model.player.api.Player;
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

import java.util.List;
import java.util.Objects;

/**
 * Concrete implementation of GameState used to test the integration of 
 * player movement, dual-layered maps (visual/collision), and camera scrolling.
 */
public final class TestState implements GameState {

    private static final int TILE_SIZE = 32;
    private static final double DEFAULT_SPAWN_X = 1850.0;
    private static final double DEFAULT_SPAWN_Y = 2950.0;

    private static final int[] TELEPORT1 = {2550, 1520};
    private static final int[] TELEPORT2 = {1280, 2410};

    private final InputHandler input;
    private final Player kiki; 
    private final PlayerController kikiController;
    private double savedX;
    private double savedY;

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
    private final int[][] upperGrid;
    private final int[][] maskGrid;

    /**
     * Constructs a TestState with the required controller systems and loads map resources.
     * 
     * @param transitionController is used to switch from one state to another
     * @param input controls every input from the player
     * @param kiki the shared player model instance
     */
    public TestState(final GameStateTransition transitionController, final InputHandler input, final Player kiki) {
        this.transitionController = Objects.requireNonNull(transitionController, "Transition controller cannot be null");
        this.input = Objects.requireNonNull(input, "Input handler cannot be null");
        this.kiki = Objects.requireNonNull(kiki, "Player cannot be null");

        // --- 1. RESOURCE LOADING ---
        this.groundGrid = MapLoader.loadMap("maps/map0/testGround.txt");
        this.decorationGrid = MapLoader.loadMap("maps/map0/testDecor.txt");
        this.upperGrid = MapLoader.loadMap("maps/map0/testUpper.txt");
        this.maskGrid = MapLoader.loadMap("maps/map0/col/testCol.txt");

        // --- 2. MODEL INITIALIZATION ---
        final GameTile collisionMap = new TileMapImpl(maskGrid, TILE_SIZE);
        this.collisionHandler = new CollisionHandler(collisionMap);

        // Se Kiki non è ancora stata posizionata nel mondo, usa lo spawn di default
        if (this.kiki.getX() == 0.0 && this.kiki.getY() == 0.0) {
            this.kiki.setX(DEFAULT_SPAWN_X);
            this.kiki.setY(DEFAULT_SPAWN_Y);
        }

        // Salva le coordinate correnti e inietta le collisioni
        this.savedX = this.kiki.getX();
        this.savedY = this.kiki.getY();
        this.kiki.setCollisionHandler(this.collisionHandler); 
        this.kikiController = new PlayerControllerImpl(this.kiki, this.input);

        // --- 3. VIEW INITIALIZATION ---
        this.spriteManager = new SpriteManager();
        this.entityRenderer = new EntityRenderer(this.spriteManager);
        this.environmentRenderer = new MapRenderer(this.spriteManager);
        this.hudRenderer = new HUDRenderer(this.spriteManager);
    }

    @Override
    public void init() { }

    @Override
    public void pause() {
        this.savedX = this.kiki.getX();
        this.savedY = this.kiki.getY();
    }

    @Override
    public void resume() {
        this.kiki.setX(this.savedX);
        this.kiki.setY(this.savedY);
        this.kiki.setCollisionHandler(this.collisionHandler);
    }

    @Override
    public void update() {
        kikiController.update();
        frameCount++;
        final int tileId = collisionHandler.getInteractableTileId(kiki.getX() + 16, kiki.getY() + 32, TILE_SIZE, TILE_SIZE);
        if (tileId == 2 && input.isAction()) {
            kiki.setX(TELEPORT1[0]);
            kiki.setY(TELEPORT1[1]);
        }
        if (tileId == 3 && input.isAction()) {
            kiki.setX(TELEPORT2[0]);
            kiki.setY(TELEPORT2[1]);
        }
        if (tileId == 4 && input.isAction()) {
            input.resetAction();
            transitionController.pushState(new MinigameFly(transitionController, input, kiki));
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
        environmentRenderer.render(gc, new MapRenderData(groundGrid, TILE_SIZE));
        environmentRenderer.render(gc, new MapRenderData(decorationGrid, TILE_SIZE));
        final EntityRenderData kikiData = new EntityRenderData(
            kiki.getX(), kiki.getY(), 64, 64, "sprites/player/kiki", kiki.getState(), kiki.getDirection()
        );
        entityRenderer.render(gc, List.of(kikiData), frameCount);
        environmentRenderer.render(gc, new MapRenderData(upperGrid, TILE_SIZE));

        gc.restore(); 

        // --- HUD ---
        final HUDRenderData hudData = new HUDRenderData(kiki.getEnergy(), 5, kiki.getMoney());
        hudRenderer.render(gc, hudData);
    }
}
