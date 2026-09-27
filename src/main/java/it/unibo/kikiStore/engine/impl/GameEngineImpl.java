package it.unibo.kikiStore.engine.impl;

import it.unibo.kikiStore.engine.api.GameEngine;
import it.unibo.kikiStore.engine.api.GameStateManager;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;

/**
 * Implementation of the GameEngine.
 */
public final class GameEngineImpl implements GameEngine {

    private static final long ONE_SECOND_NS = 1_000_000_000L;
    private static final int TARGET_FPS = 60;
    private static final long TIME_PER_TICK = ONE_SECOND_NS / TARGET_FPS; // ~16.66 ms in nanosecondi

    private final GameStateManager gsm;
    private final GraphicsContext gc;
    private AnimationTimer loop;

    private final double width;
    private final double height;
    private long lastTime;
    private double delta;

    /**
     * Constructs a new GameEngineImpl.
     *
     * @param gsm the game state manager
     * @param gc  the graphics context for rendering
     * @param width the width of the game canvas
     * @param height the height of the game canvas
     */
    public GameEngineImpl(final GameStateManager gsm, final GraphicsContext gc, final double width, final double height) {
        this.gsm = gsm;
        this.gc = gc;
        this.width = width;
        this.height = height;
        initLoop();
    }

    private void initLoop() {
        this.loop = new AnimationTimer() {
            @Override
            public void handle(final long now) {
                if (lastTime == 0) {
                    lastTime = now;
                    return;
                }

                // Fixed 60 FPS update rate logic
                final long elapsed = now - lastTime;
                lastTime = now;
                delta += (double) elapsed / TIME_PER_TICK;

                while (delta >= 1.0) {
                    gsm.update();
                    delta -= 1.0;
                }

                gc.clearRect(0, 0, width, height);
                gsm.render(gc);
            }
        };
    }

    @Override
    public void start() {
        if (loop != null) {
            this.lastTime = 0;
            this.delta = 0.0;
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
