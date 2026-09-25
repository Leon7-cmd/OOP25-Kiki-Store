package it.unibo.KikiStore.app;

import it.unibo.KikiStore.controller.api.InputHandler;
import it.unibo.KikiStore.controller.impl.InputHandlerImpl;
import it.unibo.KikiStore.engine.api.GameEngine;
import it.unibo.KikiStore.engine.api.GameStateTransition;
import it.unibo.KikiStore.engine.impl.GameEngineImpl;
import it.unibo.KikiStore.engine.impl.GameStateManagerImpl;
import it.unibo.KikiStore.engine.state.TestState;
import it.unibo.KikiStore.model.player.api.Player;
import it.unibo.KikiStore.model.player.impl.PlayerImpl;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * Manages the initialization of the main JavaFX window (Stage).
 * Assembles the primary graphical canvas, establishes input bindings, boots the GameEngine,
 * and sets up persistent game entities such as the Player.
 */
public final class StageInitializer {
    private static final int PLAYER_X = 1850;
    private static final int PLAYER_Y = 2950;

    private static final double SCREEN_WIDTH_RATIO = 0.50;
    private static final double SCREEN_HEIGHT_RATIO = 0.60;
    private static final String WINDOW_TITLE = "Kiki's Store";

    /**
     * Configures and displays the game's graphical interface.
     * 
     * @param stage the primary window provided by JavaFX upon startup.
     */
    public void init(final Stage stage) {
        final Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        final double screenWidth = screenBounds.getWidth() * SCREEN_WIDTH_RATIO;
        final double screenHeight = screenBounds.getHeight() * SCREEN_HEIGHT_RATIO;

        // 1. Canvas and Scene setup
        final StackPane root = new StackPane();
        final Canvas canvas = new Canvas(screenWidth, screenHeight);
        root.getChildren().add(canvas);
        final Scene scene = new Scene(root);
        final InputHandler inputHandler = new InputHandlerImpl(scene);

        // 2. Persistent Model setup
        final Player player = new PlayerImpl(PLAYER_X, PLAYER_Y);

        // 3. Initialization of the logical architecture
        final GameStateManagerImpl gsm = new GameStateManagerImpl();
        gsm.setState(new TestState((GameStateTransition) gsm, inputHandler, player));

        // 4. GameEngine creation
        final GameEngine engine = new GameEngineImpl(gsm, canvas.getGraphicsContext2D(), screenWidth, screenHeight);

        // 5. Final configuration of the OS window
        stage.setTitle(WINDOW_TITLE);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setOnCloseRequest(event -> engine.stop());
        stage.show();

        // 6. GameLoop startup
        engine.start();
    }
}
