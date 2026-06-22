package it.unibo.KikiStore.engine.api;

public interface GameStateTransition {

    void pushState(GameState newState);

    void popState();
}
