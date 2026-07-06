package it.unibo.KikiStore.app;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Starting class.
 */
public final class Main extends Application {

    @Override public void start(final Stage stage) {
        final StageInitializer initializer = new StageInitializer();
        initializer.initMenu(stage);
    }

    /**
     * Starting method for the project.
     * 
     * @param args arguments
     */
    public static void main(final String[] args) {
        launch(args);
    }
}
