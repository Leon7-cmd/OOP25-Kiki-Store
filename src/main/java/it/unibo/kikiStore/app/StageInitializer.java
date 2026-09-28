package it.unibo.kikistore.app;

import it.unibo.kikistore.controller.api.InputHandler;
import it.unibo.kikistore.controller.impl.InputHandlerImpl;
import it.unibo.kikistore.engine.api.GameEngine;
import it.unibo.kikistore.engine.api.GameStateTransition;
import it.unibo.kikistore.engine.impl.GameEngineImpl;
import it.unibo.kikistore.engine.impl.GameStateManagerImpl;
import it.unibo.kikistore.engine.state.MenuState;
import it.unibo.kikistore.model.inventory.impl.GameCatalogImpl;
import it.unibo.kikistore.view.utility.SpriteManager;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * Manages the initialization of the main JavaFX window.
 */
public final class StageInitializer {

    private static final String WINDOW_TITLE = "Kiki's Store";
    private static final String INGREDIENTS_PATH = "textFiles/ingredients.json";
    private static final String POTIONS_PATH = "textFiles/potions.json";

    private static final double VIRTUAL_WIDTH = 1920.0;
    private static final double VIRTUAL_HEIGHT = 1080.0;

    /**
     * Configures and displays the game's graphical interface.
     * 
     * @param stage the primary window provided by JavaFX upon startup.
     */
    public void init(final Stage stage) {
        // 1. Canvas and Scene setup
        final Canvas canvas = new Canvas(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        final Pane root = new Pane(canvas);
        root.setStyle("-fx-background-color: black;");

        final Scene scene = new Scene(root, VIRTUAL_WIDTH, VIRTUAL_HEIGHT, Color.BLACK);

        root.widthProperty().addListener((obs, oldVal, newVal) -> updateLayout(root, canvas));
        root.heightProperty().addListener((obs, oldVal, newVal) -> updateLayout(root, canvas));

        final InputHandler inputHandler = new InputHandlerImpl(scene, canvas);
        final SpriteManager spriteManager = new SpriteManager();
        final GameCatalogImpl catalog = new GameCatalogImpl(INGREDIENTS_PATH, POTIONS_PATH);

        // 2. Initialization of the logical architecture
        final GameStateManagerImpl gsm = new GameStateManagerImpl();
        gsm.setState(new MenuState((GameStateTransition) gsm, inputHandler, spriteManager, catalog));

        // 3. GameEngine creation
        final GameEngine engine = new GameEngineImpl(gsm, canvas.getGraphicsContext2D(), VIRTUAL_WIDTH, VIRTUAL_HEIGHT);

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

    private void updateLayout(final Pane root, final Canvas canvas) {
        final double windowW = root.getWidth();
        final double windowH = root.getHeight();
        if (windowW <= 0.0 || windowH <= 0.0) {
            return;
        }

        final double scaleFactor = Math.min(windowW / VIRTUAL_WIDTH, windowH / VIRTUAL_HEIGHT);
        canvas.setScaleX(scaleFactor);
        canvas.setScaleY(scaleFactor);

        canvas.setLayoutX((windowW - VIRTUAL_WIDTH) / 2.0);
        canvas.setLayoutY((windowH - VIRTUAL_HEIGHT) / 2.0);
    }
}
