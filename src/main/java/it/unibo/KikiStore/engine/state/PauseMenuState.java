package it.unibo.KikiStore.engine.state;

import it.unibo.KikiStore.engine.api.GameState;
import it.unibo.KikiStore.engine.api.GameStateManager;
import it.unibo.KikiStore.engine.api.GameStateTransition;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public final class PauseMenuState implements GameState {
    private final GameStateManager gsm;
    private final GameStateTransition transition;
    private final GameState previousState;

    private Rectangle2D resumeBounds;
    private Rectangle2D exitBounds;

    public PauseMenuState(final GameStateManager gsm, final GameStateTransition transition, final GameState previousState) {
        this.gsm = gsm;
        this.transition = transition;
        this.previousState = previousState;
    }

    @Override
    public void init() { }

    @Override
    public void update() {
        // no gameplay update
    }

    @Override
    public void render(final GraphicsContext gc) {
        gc.setFill(Color.rgb(0, 0, 0, 0.7));
        gc.fillRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());

        gc.setFill(Color.WHITE);
        gc.setFont(Font.font(20));
        gc.fillText("PAUSED", 50, 80);
        gc.fillText("Resume", 50, 130);
        resumeBounds = new Rectangle2D(50, 115, 100, 25);
        
        gc.fillText("Exit to Menu", 50, 170);
        exitBounds = new Rectangle2D(50, 155, 100, 25);
    }
    
    public void handleMouseClick(final double x, final double y) {
        if (resumeBounds != null && resumeBounds.contains(x, y)) {
            gsm.setState(previousState);
        } else if (exitBounds != null && exitBounds.contains(x, y)) {
            // Torna al menu principale
            // transition.changeState(...);
            // Per ora, semplicemente chiudiamo l'applicazione
            System.exit(0);
        }
    }
}
