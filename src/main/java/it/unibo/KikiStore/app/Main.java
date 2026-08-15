package it.unibo.KikiStore.app;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(final Stage stage) {
        final StageInitializer initializer = new StageInitializer();
        initializer.init(stage);
    }

    public static void main(final String[] args) {
        launch(args);
    }
}
