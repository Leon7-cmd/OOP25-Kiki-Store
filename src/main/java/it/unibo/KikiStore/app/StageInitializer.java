package it.unibo.KikiStore.app;

import it.unibo.KikiStore.controller.impl.InputHandlerImpl;
import it.unibo.KikiStore.engine.api.GameEngine;
import it.unibo.KikiStore.engine.api.GameStateManager;
import it.unibo.KikiStore.engine.impl.GameEngineImpl;
import it.unibo.KikiStore.engine.impl.GameStateManagerImpl;
import it.unibo.KikiStore.engine.state.CraftingTestState;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Manages the initialization of the main JavaFX window (Stage).
 * Assembles the basic graphical components (Canvas) and starts the GameEngine.
 */

public class StageInitializer {

    /**
     * Configures and displays the game's graphical interface.
     * 
     * @param stage The primary window provided by JavaFX upon startup.
     */
    public void init(final Stage stage) {
        final javafx.geometry.Rectangle2D screenBounds = javafx.stage.Screen.getPrimary().getVisualBounds();
        // double screenWidth = screenBounds.getWidth() * 0.50;
        // double screenHeight = screenBounds.getHeight() * 0.60;
        final double screenWidth = screenBounds.getWidth();
        final double screenHeight = screenBounds.getHeight();
        // 1. Canvas setup
        final StackPane root = new StackPane();
        final Canvas canvas = new Canvas(screenWidth, screenHeight);
        root.getChildren().add(canvas);
        final Scene scene = new Scene(root);
        final InputHandlerImpl inputHandler = new InputHandlerImpl(scene);

        // 2. Initialization of the logical architecture
        final GameStateManager gsm = new GameStateManagerImpl();
        // gsm.setState(new TestState(inputHandler));
        // gsm.setState(new BookTestState(inputHandler, gsm));
        gsm.setState(new CraftingTestState(inputHandler, gsm));
        // 3. GameEngine creation
        final GameEngine engine = new GameEngineImpl(gsm, canvas.getGraphicsContext2D(), screenWidth, screenHeight);

        // 4. Final configuration of the OS window
        stage.setTitle("Kiki's Store");
        stage.setScene(scene);
        // stage.setResizable(false);
        stage.setFullScreenExitHint("");
        stage.setFullScreenExitKeyCombination(javafx.scene.input.KeyCombination.NO_MATCH);

        stage.show();

        // 5. GameLoop startup
        engine.start();
    }
}
