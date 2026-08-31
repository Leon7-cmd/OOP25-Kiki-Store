package it.unibo.KikiStore.engine.state;

import java.util.List;

import it.unibo.KikiStore.controller.api.CraftingController;
import it.unibo.KikiStore.controller.api.InputHandler;
import it.unibo.KikiStore.controller.api.InventoryController;
import it.unibo.KikiStore.controller.api.OrderController;
import it.unibo.KikiStore.controller.api.RecipeBookController;
import it.unibo.KikiStore.controller.impl.CraftingControllerImpl;
import it.unibo.KikiStore.engine.api.GameState;
import it.unibo.KikiStore.engine.api.GameStateManager;
import it.unibo.KikiStore.engine.api.GameStateTransition;
import it.unibo.KikiStore.engine.impl.BookState;
import it.unibo.KikiStore.engine.impl.CraftingState;
import it.unibo.KikiStore.model.inventory.api.GameCatalog;
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

   /*  private final OrderSpawner orderSpawner;*/
    private final GameCatalog catalog;
    private final InventoryController inventoryController;
    private final RecipeBookController recipeBookController;
    private final OrderController orderController;
 
    /*ora questi sono condivisi 
    public void seedStarterInventory(final InventoryController inventoryController, final GameCatalog catalog) {
        inventoryController.addIngredient("Chamomile", "sprites/ingredients/chamomile", 3, "flower", 3);
        inventoryController.addIngredient("Clover", "sprites/ingredients/clover", 2, "plant", 12);
        inventoryController.addIngredient("Blue Berries", "sprites/ingredients/blue_berries", 4, "berry", 7);
        inventoryController.addIngredient("Basil", "sprites/ingredients/basil", 2, "plant", 2);
        inventoryController.addPotion("Shieldberry Potion", "sprites/potions/shieldberry", 1,
                "Smells of dark berries and white blossoms, keeps colds and coughs at bay",
                "immunity", false);
    }

    public void unlockStarterRecipes(final RecipeBookController recipeBookController) {
        final List<Recipe> allRecipes = recipeBookController.getAllRecipes();
        for (int i = 0; i < Math.min(3, allRecipes.size()); i++) {
            recipeBookController.unlockRecipe(allRecipes.get(i));
        }
    }*/
    // accessible for update method 
    /**
     * Constructs a ShopState with the required controller systems and loads map resources.
     * 
     * @param transitionController is used to switch from one state to another
     * @param input  controls every input from the player
     */
    public ShopState(final GameStateTransition transitionController, final InputHandler input, final GameSession gameSession) {
        this.transitionController = transitionController;
        this.input = input;
        //catalog+inventoryController+recipeBookController+orderController are passed from GameSession to maintain state across game states
        this.catalog = gameSession.getCatalog();
        this.inventoryController = gameSession.getInventoryController();
        this.recipeBookController = gameSession.getRecipeBookController();
        this.orderController = gameSession.getOrderController();
       /*  this.orderSpawner = gameSession.getOrderSpawner();*/ 


       /*  final CustomerBook customerBook = new CustomerBookImpl("customers.json", this.catalog);
        final NeedBook needBook = new NeedBookImpl("needs.json");
        final NeedGenerator needGenerator = new NeedGeneratorImpl(needBook);
        final OrderGenerator orderGenerator = new OrderGeneratorImpl(customerBook, needGenerator);
        ora in gamesession causa condivisione e orderspawn condiviso*/
        /*this.orderSpawner = new OrderSpawnerImpl(
            orderGenerator,
            this.orderBook,
            300, // spawn interval in frames
            5,   // max pending orders
            1000 // reset threshold in frames
        );*/

        // --- 1. RESOURCE LOADING ---
        this.groundGrid = MapLoader.loadMap("maps/shop/shopGround.txt");
        this.decorationGrid = MapLoader.loadMap("maps/shop/shopDecor.txt");
        this.maskGrid = MapLoader.loadMap("maps/shop/col/col.txt");

        // --- 2. MODEL INITIALIZATION ---
        final GameTile collisionMap = new TileMapImpl(maskGrid, 32);
        collisionHandler = new CollisionHandler(collisionMap);

        // Initialize the player and inject the collision logic
        this.kiki = gameSession.getPlayer();
        this.kiki.setCollisionHandler(collisionHandler);
        this.kiki.setX(PLAYER_X);
        this.kiki.setY(PLAYER_Y);

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
            final GameStateManager gsm = (GameStateManager) this.transitionController;
            final BookState bookState = new BookState(
                this.inventoryController,
                this.recipeBookController,
                this.orderController,
                this.catalog,
                this.spriteManager,
                gsm,
                this,
                this.input
            );
            transitionController.pushState(bookState);
        }
        if (tileId == 4 && input.isAction()) {
            final GameStateManager gsm = (GameStateManager) this.transitionController;
            final CraftingController craftingController = new CraftingControllerImpl(
                    this.inventoryController,
                    this.recipeBookController
            );
            final GameState craftingState = new CraftingState(
                    this.inventoryController,
                    craftingController,
                    this.recipeBookController,
                    this.catalog,
                    this.spriteManager,
                    gsm,
                    this,
                    this.input
            );
            transitionController.pushState(craftingState);
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
