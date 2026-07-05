package it.unibo.KikiStore.view.menu.impl;
import it.unibo.KikiStore.app.StageInitializer;
import it.unibo.KikiStore.view.menu.api.InitialScreenView;
import javafx.fxml.FXML;
import javafx.stage.Stage;

/**
 * Initial screen view implementation : menu (FXML).
 * Buttons: Continue, New Game, Settings, Exit.
 */
public class InitialScreenViewImpl implements InitialScreenView {

    private Stage primaryStage;

    /**
     * 
     * @param stage primary stage of the application that would be called from StageInitializer
     */
    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    @FXML
    public void handleContinueButton() {
        System.out.println("Continue");
        // TODO: continuing the game from the last saved state (optional)
    }

    @FXML
    public void handleNewGameButton() {
        System.out.println("New Game - Starting new game...");
        // starting a new game (resetting the game state)
        StageInitializer stageInitializer = new StageInitializer();
        stageInitializer.init(primaryStage);    
    }

    @FXML
    public void handleSettingsButton() {
        System.out.println("Settings - Opening settings...");
        // TODO:settings with audio options(maybe FXML)
    }

    @FXML
    public void handleExitButton() {
        System.out.println("Exit - Closing application...");
        primaryStage.close();
        System.exit(0);
    }
}
