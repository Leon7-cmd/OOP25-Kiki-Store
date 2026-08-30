package it.unibo.KikiStore.app;

import it.unibo.KikiStore.controller.impl.InputHandlerImpl;
import it.unibo.KikiStore.engine.api.GameEngine;
import it.unibo.KikiStore.engine.api.GameStateManager;
import it.unibo.KikiStore.engine.api.GameStateTransition;
import it.unibo.KikiStore.engine.impl.GameEngineImpl;
import it.unibo.KikiStore.engine.impl.GameStateManagerImpl;
import it.unibo.KikiStore.engine.state.GameSession;
import it.unibo.KikiStore.engine.state.TestState;
import it.unibo.KikiStore.view.hud.impl.HUDRenderer;
import it.unibo.KikiStore.view.menu.impl.InitialScreenViewImpl;
import it.unibo.KikiStore.view.utility.SpriteManager;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * Manages the initialization of the main JavaFX window (Stage).
 * Assembles the basic graphical components (Canvas) and starts the GameEngine.
 */

public class StageInitializer {

    /**
     * Configures and displays menu (FXML).
     * Carica il file e lo mostra come prima schermata.
     * 
     * @param stage The primary window provided by JavaFX upon startup.
     */
    public void initMenu(Stage stage) {
        try {
            // Carica il file FXML (Initial screen)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/layout/InitialScreen.fxml"));
            AnchorPane menuRoot = loader.load();
            
            // Ottieni il controller e passigli lo stage
            InitialScreenViewImpl controller = loader.getController();
            controller.setPrimaryStage(stage);
            
            // Crea la scena con il menu
            Scene scene = new Scene(menuRoot, 600, 400);
            
            // Configura lo stage
            stage.setTitle("Kiki's Store - Menu");
            stage.setScene(scene);
            stage.setResizable(true);
            stage.show();
            
            System.out.println("Menu caricato correttamente.");
        } catch (java.io.IOException e) {
            System.err.println("Errore nel caricamento del menu FXML: " + e.getMessage());
        }
    }

    /**
     * Carica e mostra la schermata di gioco (Canvas + GameEngine).
     * Chiamato da InitialScreenViewImpl quando l'utente preme "New Game".
     * 
     * @param stage Lo stage principale
     */
    public void init(final Stage stage) {
        final Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        final double screenWidth = screenBounds.getWidth()*0.50;
        final double screenHeight = screenBounds.getHeight()*0.60;

        // 1. Canvas setup
        final StackPane root = new StackPane();
        final Canvas canvas = new Canvas(screenWidth, screenHeight);
        root.getChildren().add(canvas);
        final Scene scene = new Scene(root);
        

        // 2. Initialization of the logical architecture
        final GameStateManager gsm = new GameStateManagerImpl();//
        final SpriteManager spriteManager = new SpriteManager();//
        final HUDRenderer hudRenderer = new HUDRenderer(spriteManager);//
        final InputHandlerImpl inputHandler = new InputHandlerImpl(scene);
    

        final GameSession gameSession = GameSession.createStarterSession();
        gsm.setState(new TestState((GameStateTransition) gsm, inputHandler, gameSession));

        // Use TestState as initial game state. BookTestState was used for testing
        // and would replace the TestState causing the book to open on New Game.
        // 3. GameEngine creation
        final GameEngine engine = new GameEngineImpl(gsm, canvas.getGraphicsContext2D(), screenWidth, screenHeight);

        // 4. Final configuration of the OS window
        stage.setTitle("Kiki's Store - Game");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setFullScreenExitHint("");//
        stage.setFullScreenExitKeyCombination(javafx.scene.input.KeyCombination.NO_MATCH);//

        stage.show();

        // 5. GameLoop startup
        engine.start();
    }
}

