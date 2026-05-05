package it.unibo.KikiStore.app;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        StageInitializer initializer = new StageInitializer();
        initializer.init(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
