package it.unibo.KikiStore.engine.api;

/**
 * Represents the main game engine.
 */
public interface GameEngine {
    /**
     * Starts the main game loop.
     * From this moment forward, the engine will cyclically call
     * the update and render phases.
     */
    void start();

    /**
     * Stops or suspends the main game loop.
     * Stops the execution of physics calculations and the rendering of frames on the screen.
     */
    void stop();
}
