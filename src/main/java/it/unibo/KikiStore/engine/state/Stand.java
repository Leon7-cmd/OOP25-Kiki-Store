package it.unibo.KikiStore.engine.state;

import java.util.List;

import it.unibo.KikiStore.controller.api.InputHandler;
import it.unibo.KikiStore.controller.api.InventoryController;
import it.unibo.KikiStore.controller.api.OrderController;
import it.unibo.KikiStore.controller.api.RecipeBookController;
import it.unibo.KikiStore.controller.api.ShopTradingController;
import it.unibo.KikiStore.controller.impl.IngredientTradingControllerImpl;
import it.unibo.KikiStore.controller.impl.PotionTradingControllerImpl;
import it.unibo.KikiStore.engine.api.GameState;
import it.unibo.KikiStore.engine.api.GameStateManager;
import it.unibo.KikiStore.engine.api.GameStateTransition;
import it.unibo.KikiStore.engine.impl.BookState;
import it.unibo.KikiStore.model.economy.api.PotionPriceCalculator;
import it.unibo.KikiStore.model.inventory.api.GameCatalog;
import it.unibo.KikiStore.model.inventory.api.Ingredient;
import it.unibo.KikiStore.model.inventory.api.Potion;
import it.unibo.KikiStore.model.map.api.GameTile;
import it.unibo.KikiStore.model.map.impl.CollisionHandler;
import it.unibo.KikiStore.model.map.impl.MapLoader;
import it.unibo.KikiStore.model.map.impl.TileMapImpl;
import it.unibo.KikiStore.model.player.impl.PlayerImpl;
import it.unibo.KikiStore.view.entity.api.EntityRenderData;
import it.unibo.KikiStore.view.entity.impl.EntityRenderer;
import it.unibo.KikiStore.view.environment.api.MapRenderData;
import it.unibo.KikiStore.view.environment.impl.MapRenderer;
import it.unibo.KikiStore.view.hud.impl.HUDRenderer;
import it.unibo.KikiStore.view.utility.Camera;
import it.unibo.KikiStore.view.utility.SpriteManager;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;


/**
 * Stato della bottega: ospita entrambi gli stand (pozioni e ingredienti)
 * sulla stessa mappa. Ogni tile interattivo apre uno ShopTradingState
 * generico, specializzato a compile-time sul tipo corretto (Potion o
 * Ingredient) tramite il relativo controller concreto — nessuna
 * mescolanza o controllo di tipo a runtime tra le due categorie.
 */
public final class Stand implements GameState {
    private static final int PLAYER_X = 870;
    private static final int PLAYER_Y = 920;
    private static final int TILE_EXIT = 2;
    private static final int TILE_BOOK = 3;
    // Tile dinamico dello shop (sarà 4 in uno, 5 nell'altro)
    private final int tileShop;

    private final InputHandler input;
    private final PlayerImpl kiki;

    private final GameStateTransition transitionController;
    private final CollisionHandler collisionHandler;
    private final SpriteManager spriteManager;
    private final EntityRenderer entityRenderer;
    private final MapRenderer environmentRenderer;

    private final HUDRenderer hudRenderer; // Aggiunto per l'HUD

    private final Camera cam = new Camera();
    private int frameCount;
    private final int[][] groundGrid;
    private final int[][] decorationGrid;
    private final int[][] maskGrid;

    private final GameCatalog catalog;
    private final InventoryController inventoryController;
    private final RecipeBookController recipeBookController;
    private final OrderController orderController;
    private final PotionPriceCalculator priceCalculator;

    /**
     * @param transitionController usato per cambiare stato
     * @param input gestisce ogni input del giocatore
     * @param gameSession contiene i sistemi condivisi tra i vari stati
     * @param tileId l'ID del tile interattivo che apre la bottega(pozioni o ingredienti)
     */
    public Stand(final GameStateTransition transitionController, final InputHandler input, final GameSession gameSession,int tileId) {
        this.transitionController = transitionController;
        this.input = input;
        this.catalog = gameSession.getCatalog();
        this.inventoryController = gameSession.getInventoryController();
        this.recipeBookController = gameSession.getRecipeBookController();
        this.orderController = gameSession.getOrderController();
        this.priceCalculator = gameSession.getPriceCalculator();
        
        

        // --- 1. RESOURCE LOADING ---
        if (tileId == 6){
        this.groundGrid = MapLoader.loadMap("maps/standPotion/standPotionGround.txt");
        this.decorationGrid = MapLoader.loadMap("maps/standPotion/standPotionDecor.txt");
        this.maskGrid = MapLoader.loadMap("maps/standPotion/col/col.txt");
        this.tileShop=4;//potions
        }
        else{
        this.groundGrid = MapLoader.loadMap("maps/standIngredients/standingredientsGround.txt");
        this.decorationGrid = MapLoader.loadMap("maps/standIngredients/standIngredientsDecor.txt");
        this.maskGrid = MapLoader.loadMap("maps/standIngredients/col/col.txt");   
        this.tileShop=5;//ingredients
        }
        // --- 2. MODEL INITIALIZATION ---
        final GameTile collisionMap = new TileMapImpl(maskGrid, 32);
        collisionHandler = new CollisionHandler(collisionMap);

        this.kiki = gameSession.getPlayer();
        this.kiki.setCollisionHandler(collisionHandler);
        this.kiki.setX(PLAYER_X);
        this.kiki.setY(PLAYER_Y);

        // --- 3. VIEW INITIALIZATION ---
        this.spriteManager = new SpriteManager();
        this.entityRenderer = new EntityRenderer(spriteManager);
        this.environmentRenderer = new MapRenderer(spriteManager);
        this.hudRenderer = new HUDRenderer(spriteManager);
    }

    @Override
    public void init() { }

    @Override
    public void update() {
        kiki.update(input);
        frameCount++;

        final int tileId = collisionHandler.getInteractableTileId(kiki.getX() + 16, kiki.getY() + 32, 32, 32);

        if (tileId == TILE_EXIT && input.isAction()) { //TILE_EXIT
            transitionController.popState();
        }
        if (tileId == TILE_BOOK && input.isAction()) {
            openBookState();
        }
        if (tileId == this.tileShop && input.isAction()) {
            if (this.tileShop==4)
                openPotionTradingState();
            else
                openIngredientTradingState();
        }
    }

    private void openBookState() {
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

    private void openPotionTradingState() {
        final ShopTradingController<Potion> potionController =
                new PotionTradingControllerImpl(this.catalog, this.inventoryController,this.priceCalculator,this.recipeBookController, this.kiki);
        final ShopTradingState<Potion> shopTradingState = new ShopTradingState<>(
                potionController,
                this.spriteManager,
                this.transitionController,
                this,
                this.input
        );
        transitionController.pushState(shopTradingState);
    }

    private void openIngredientTradingState() {
        final ShopTradingController<Ingredient> ingredientController =
                new IngredientTradingControllerImpl(this.catalog, this.inventoryController, this.kiki);
        final ShopTradingState<Ingredient> shopTradingState = new ShopTradingState<>(
                ingredientController,
                this.spriteManager,
                this.transitionController,
                this,
                this.input
        );
        transitionController.pushState(shopTradingState);
    }

    @Override
    public void render(final GraphicsContext gc) {
        final double screenWidth = gc.getCanvas().getWidth();
        final double screenHeight = gc.getCanvas().getHeight();

        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, screenWidth, screenHeight);

        gc.save();
        cam.update(kiki.getX(), kiki.getY(), screenWidth, screenHeight);
        gc.translate(-cam.getX(), -cam.getY());

        environmentRenderer.render(gc, new MapRenderData(groundGrid, 32));
        environmentRenderer.render(gc, new MapRenderData(decorationGrid, 32));
        final EntityRenderData kikiData = new EntityRenderData(
                kiki.getX(), kiki.getY(), 64, 64, "sprites/player/kiki", kiki.getState(), kiki.getDirection()
        );
        entityRenderer.render(gc, List.of(kikiData), frameCount);

        gc.restore();
    }
}