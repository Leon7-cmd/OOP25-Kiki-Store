package it.unibo.kikistore.engine.state;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import it.unibo.kikistore.controller.api.InputHandler;
import it.unibo.kikistore.engine.api.GameState;
import it.unibo.kikistore.engine.api.GameStateTransition;
import it.unibo.kikistore.model.inventory.api.GameCatalog;
import it.unibo.kikistore.view.states.pause.api.PauseView;
import it.unibo.kikistore.view.states.pause.impl.PauseViewImpl;
import it.unibo.kikistore.view.utility.SpriteManager;
import javafx.scene.canvas.GraphicsContext;

/**
 * Controller state managing the pause overlay lifecycle and user actions.
 */
@SuppressFBWarnings(
    value = "EI_EXPOSE_REP2",
    justification = "Shared engine and state dependencies injected from outside"
)
public final class PauseState implements GameState {

    private final GameStateTransition transitionController;
    private final InputHandler input;
    private final SpriteManager spriteManager;
    private final GameCatalog catalog;
    private final PauseView pauseView;

    /**
     * Constructs a new PauseState.
     * 
     * @param transitionController the controller to manage state transitions
     * @param input                the input handler for user interactions
     * @param spriteManager        the manager for game sprites
     * @param catalog              the game catalog containing inventory and recipes
     */
    public PauseState(
        final GameStateTransition transitionController,
        final InputHandler input,
        final SpriteManager spriteManager,
        final GameCatalog catalog
    ) {
        this.transitionController = transitionController;
        this.input = input;
        this.spriteManager = spriteManager;
        this.catalog = catalog;
        this.pauseView = new PauseViewImpl();
    }

    @Override
    public void init() { }

    @Override
    public void update() {
        if (input.isEscapePressed()) {
            transitionController.popState(false);
            return;
        }

        if (input.isMouseClicked()) {
            final double mouseX = input.getMouseX();
            final double mouseY = input.getMouseY();

            if (pauseView.isResumeClicked(mouseX, mouseY)) {
                transitionController.popState(false);
            } else if (pauseView.isTitleClicked(mouseX, mouseY)) {
                transitionController.clearStates();
                transitionController.pushState(new MenuState(
                    transitionController,
                    input,
                    spriteManager,
                    catalog
                ), true);
            }
        }
    }

    @Override
    public void render(final GraphicsContext gc) {
        pauseView.render(gc);
    }

    @Override
    public void pause() { }

    @Override
    public void resume() { }
}
