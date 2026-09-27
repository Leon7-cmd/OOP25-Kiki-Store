package it.unibo.kikiStore.app;

import it.unibo.kikiStore.controller.api.InputHandler;
import it.unibo.kikiStore.controller.impl.InputHandlerImpl;
import it.unibo.kikiStore.engine.api.GameEngine;
import it.unibo.kikiStore.engine.api.GameStateTransition;
import it.unibo.kikiStore.engine.impl.GameEngineImpl;
import it.unibo.kikiStore.engine.impl.GameStateManagerImpl;
import it.unibo.kikiStore.engine.state.MenuState;
import it.unibo.kikiStore.model.inventory.impl.GameCatalogImpl;
import it.unibo.kikiStore.view.utility.SpriteManager;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * Manages the initialization of the main JavaFX window.
 */
public final class StageInitializer {

    private static final String WINDOW_TITLE = "Kiki's Store";
    private static final String INGREDIENTS_PATH = "textFiles/ingredients.json";
    private static final String POTIONS_PATH = "textFiles/potions.json";

    /**
     * Configures and displays the game's graphical interface.
     * 
     * @param stage the primary window provided by JavaFX upon startup.
     */
    public void init(final Stage stage) {
        final Rectangle2D screenBounds = Screen.getPrimary().getBounds();
        final double screenWidth = screenBounds.getWidth();
        final double screenHeight = screenBounds.getHeight();

        // 1. Canvas and Scene setup
        final Pane root = new Pane();
        final Canvas canvas = new Canvas(screenWidth, screenHeight);
        root.getChildren().add(canvas);
        final Scene scene = new Scene(root, screenWidth, screenHeight, Color.BLACK);

        scene.widthProperty().addListener((obs, oldVal, newVal) -> canvas.setWidth(newVal.doubleValue()));
        scene.heightProperty().addListener((obs, oldVal, newVal) -> canvas.setHeight(newVal.doubleValue()));

        final InputHandler inputHandler = new InputHandlerImpl(scene);
        final SpriteManager spriteManager = new SpriteManager();
        final GameCatalogImpl catalog = new GameCatalogImpl(INGREDIENTS_PATH, POTIONS_PATH);

        // 2. Initialization of the logical architecture
        final GameStateManagerImpl gsm = new GameStateManagerImpl();
        gsm.setState(new MenuState((GameStateTransition) gsm, inputHandler, spriteManager, catalog));

        // 3. GameEngine creation
        final GameEngine engine = new GameEngineImpl(gsm, canvas.getGraphicsContext2D(), screenWidth, screenHeight);

        // 4. Final configuration of the OS window
        stage.setTitle(WINDOW_TITLE);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setOnCloseRequest(event -> engine.stop());
        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        stage.setFullScreenExitHint("");
        stage.setFullScreen(true);
        stage.show();

        // 5. GameLoop startup
        engine.start();
    }
}
