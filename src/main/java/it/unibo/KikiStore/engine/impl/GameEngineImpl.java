package it.unibo.KikiStore.engine.impl;

import it.unibo.KikiStore.controller.api.OrderSpawner;
import it.unibo.KikiStore.engine.api.GameEngine;
import it.unibo.KikiStore.engine.api.GameStateManager;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;

/**
 * Implementation of the Game Engine.
 * Manages the game's lifecycle using JavaFX's AnimationTimer,
 * allowing for logic updates and continuous frame rendering.
 */
public class GameEngineImpl implements GameEngine {

    private final GameStateManager gsm;
    private final GraphicsContext gc;
    private AnimationTimer loop;
    private final OrderSpawner orderSpawner;

    private final double width;
    private final double height;

    /**
     * Constructs the game engine.
     *
     * @param gsm    The state manager.
     * @param gc     The GraphicsContext of the Canvas to draw frames on.
     * @param width  The dynamic width of the game screen.
     * @param height The dynamic height of the game screen.
     * @param orderSpawner The order spawner.
     */
    public GameEngineImpl(final GameStateManager gsm, final GraphicsContext gc, final double width,
            final double height, final OrderSpawner orderSpawner) {
        this.gsm = gsm;
        this.gc = gc;
        this.width = width;
        this.height = height;
        this.orderSpawner = orderSpawner;
        initLoop();
    }

    /**
     * Initialize the main Game Loop.
     * Follow the standard pattern: Update -> Clear -> Render.
     */
    private void initLoop() {
        this.loop = new AnimationTimer() {
            @Override
            public void handle(final long now) {
                orderSpawner.update();// Update the order spawner logic wherever you are (for now)
                gsm.update();
                gc.clearRect(0, 0, width, height);
                gsm.render(gc);
            }
        };
    }

    @Override
    public void start() {
        if (loop != null) {
            loop.start();
        }
    }

    @Override
    public void stop() {
        if (loop != null) {
            loop.stop();
        }
    }
}
