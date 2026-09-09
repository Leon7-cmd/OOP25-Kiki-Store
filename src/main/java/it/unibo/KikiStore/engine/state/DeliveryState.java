package it.unibo.KikiStore.engine.state;

import it.unibo.KikiStore.controller.api.InputHandler;
import it.unibo.KikiStore.engine.api.GameState;
import it.unibo.KikiStore.engine.api.GameStateTransition;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public final class DeliveryState implements GameState {

    private final GameStateTransition transitionController;
    private final InputHandler input;
    private final GameSession gameSession;

    public DeliveryState(final GameStateTransition transitionController, final InputHandler input, final GameSession gameSession) {
        this.transitionController = transitionController;
        this.input = input;
        this.gameSession = gameSession;
    }

    @Override
    public void init() { }

    @Override
    public void update() {
        if (input.isCancel()) {
            transitionController.popState();
        }
    }

    @Override
    public void render(final GraphicsContext gc) {
        final double w = gc.getCanvas().getWidth();
        final double h = gc.getCanvas().getHeight();

        gc.setFill(Color.rgb(0, 0, 0, 0.7));
        gc.fillRect(0, 0, w, h);

        gc.setFill(Color.rgb(248, 238, 222));
        gc.fillRoundRect(w * 0.15, h * 0.2, w * 0.7, h * 0.5, 12, 12);

        gc.setFill(Color.rgb(60, 35, 15));
        gc.setFont(Font.font("Monospaced", FontWeight.BOLD, 16));

        final var recipient = gameSession.getDeliveryController().getCurrentRecipient();
        if (recipient.isEmpty()) {
            gc.fillText("Nessun pacco al momento", w * 0.2, h * 0.35);
        } else {
            final String name = recipient.get();
            final String hint = gameSession.getHouseBook()
                    .getLocationHintByOwnerName(name)
                    .orElse("Posizione sconosciuta");

            gc.fillText("Pacco per: " + name, w * 0.2, h * 0.32);
            gc.setFont(Font.font("System", 13));
            gc.fillText(hint, w * 0.2, h * 0.4);
            gc.setFont(Font.font("System", 11));
            gc.fillText("Premi ESC per tornare", w * 0.2, h * 0.6);
        }
    }
}
