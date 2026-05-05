package it.unibo.KikiStore.engine.state;

import it.unibo.KikiStore.engine.api.GameState;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * TestState per verificare il corretto funzionamento del Game Engine.
 * Dimostra l'esecuzione in sequenza di init(), update() e render().
 */
public class TestState implements GameState {

    private double boxX = 100;
    private final double boxY = 200;
    private double speedX = 5.0;

    @Override
    public void init() {}

    /**
     * Dimostra che il Game Loop sta girando aggiornando la logica costantemente.
     */
    @Override
    public void update() {
        // Facciamo muovere il quadrato avanti e indietro
        boxX += speedX;

        if (boxX > 700 || boxX < 50) {
            speedX = -speedX; 
        }
    }

    /**
     * Dimostra che il GraphicsContext sta dipingendo correttamente sul Canvas.
     */
    @Override
    public void render(GraphicsContext gc) {
        double canvasWidth = gc.getCanvas().getWidth();
        double canvasHeight = gc.getCanvas().getHeight();

        gc.setFill(Color.DARKSLATEGRAY);
        gc.fillRect(0, 0, canvasWidth, canvasHeight);

        gc.setFill(Color.CRIMSON);
        gc.fillRect(boxX, boxY, 80, 80);
    }
}