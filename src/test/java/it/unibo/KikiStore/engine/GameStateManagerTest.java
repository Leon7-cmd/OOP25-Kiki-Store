package it.unibo.KikiStore.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.KikiStore.engine.api.GameState;
import it.unibo.KikiStore.engine.impl.GameStateManagerImpl;
import javafx.scene.canvas.GraphicsContext;

/**
 * Unit tests for {@link GameStateManagerImpl}.
 * Verifies state stack management, state lifecycles, and transition progression.
 */
class GameStateManagerTest {

    private GameStateManagerImpl gsm;

    /**
     * Fake implementation of GameState to track lifecycle calls.
     */
    private static final class DummyState implements GameState {
        private boolean initialized;
        private int updateCount;

        @Override
        public void init() {
            this.initialized = true;
        }

        @Override
        public void update() {
            this.updateCount++;
        }

        @Override
        public void render(final GraphicsContext gc) {
            // Not tested in headless unit tests
        }

        public boolean isInitialized() {
            return initialized;
        }

        public int getUpdateCount() {
            return updateCount;
        }
    }

    @BeforeEach
    void setUp() {
        gsm = new GameStateManagerImpl();
    }

    @Test
    void testInitialPushStateWhenEmpty() {
        final DummyState state = new DummyState();
        gsm.pushState(state);

        assertSame(state, gsm.getCurrentState());
        assertTrue(state.isInitialized());
    }

    @Test
    void testUpdatePropagatesToCurrentState() {
        final DummyState state = new DummyState();
        gsm.pushState(state);

        gsm.update();
        gsm.update();

        assertEquals(2, state.getUpdateCount());
    }

    @Test
    void testPushStateWithTransitionSequence() {
        final DummyState firstState = new DummyState();
        final DummyState secondState = new DummyState();

        gsm.pushState(firstState);

        // Request transition to second state
        gsm.pushState(secondState);
        assertSame(firstState, gsm.getCurrentState());

        // Advance updates to complete fade-in and trigger the state switch
        // (FADE_SPEED = 0.05, reaches 1.0 in more or less 20 updates)
        for (int i = 0; i < 25; i++) {
            gsm.update();
        }

        assertSame(secondState, gsm.getCurrentState());
        assertTrue(secondState.isInitialized());
    }

    @Test
    void testPopStateTransitionsBackToPreviousState() {
        final DummyState baseState = new DummyState();
        final DummyState overlayState = new DummyState();

        gsm.pushState(baseState);

        // Transition to overlay state
        gsm.pushState(overlayState);
        for (int i = 0; i < 45; i++) {
            gsm.update();
        }
        assertSame(overlayState, gsm.getCurrentState());

        // Trigger pop
        gsm.popState();

        // Advance updates to complete pop transition
        for (int i = 0; i < 45; i++) {
            gsm.update();
        }

        assertSame(baseState, gsm.getCurrentState());
    }
}