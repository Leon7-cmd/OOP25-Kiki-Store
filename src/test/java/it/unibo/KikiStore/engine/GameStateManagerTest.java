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
 * Verifies state stack behavior, lifecycle hooks (init, pause, resume), and update propagation.
 */
class GameStateManagerTest {

    private GameStateManagerImpl gsm;

    /**
     * Fake implementation of GameState tracking lifecycle events and updates.
     */
    private static final class DummyState implements GameState {
        private boolean initialized;
        private boolean paused;
        private boolean resumed;
        private int updateCount;

        @Override
        public void init() {
            this.initialized = true;
        }

        @Override
        public void pause() {
            this.paused = true;
            this.resumed = false;
        }

        @Override
        public void resume() {
            this.resumed = true;
            this.paused = false;
        }

        @Override
        public void update() {
            this.updateCount++;
        }

        @Override
        public void render(final GraphicsContext gc) {
            // Not evaluated in headless unit tests
        }

        public boolean isInitialized() {
            return this.initialized;
        }

        public boolean isPaused() {
            return this.paused;
        }

        public boolean isResumed() {
            return this.resumed;
        }

        public int getUpdateCount() {
            return this.updateCount;
        }
    }

    /**
     * Prepares an empty GameStateManager instance before each test.
     */
    @BeforeEach
    void setUp() {
        gsm = new GameStateManagerImpl();
    }

    /**
     * Verifies that pushing onto an empty stack activates and initializes the state immediately.
     */
    @Test
    void testInitialPushStateWhenEmpty() {
        final DummyState state = new DummyState();
        gsm.pushState(state);

        assertSame(state, gsm.getCurrentState());
        assertTrue(state.isInitialized());
    }

    /**
     * Verifies that update cycles propagate exclusively to the currently active top-level state.
     */
    @Test
    void testUpdatePropagatesToCurrentState() {
        final DummyState state = new DummyState();
        gsm.pushState(state);

        gsm.update();
        gsm.update();

        assertEquals(2, state.getUpdateCount());
    }

    /**
     * Verifies that pushing a new state pauses the previous one and activates the new one.
     */
    @Test
    void testPushStatePausesPreviousAndActivatesNew() {
        final DummyState firstState = new DummyState();
        final DummyState secondState = new DummyState();

        gsm.pushState(firstState);
        gsm.pushState(secondState);

        // Previous state is paused; new state is initialized and set active
        assertTrue(firstState.isPaused());
        assertTrue(secondState.isInitialized());
        assertSame(secondState, gsm.getCurrentState());

        // Updates now only hit the top state
        gsm.update();
        assertEquals(0, firstState.getUpdateCount());
        assertEquals(1, secondState.getUpdateCount());
    }

    /**
     * Verifies that popping a state resumes the underlying state and restores it as active.
     */
    @Test
    void testPopStateResumesPreviousState() {
        final DummyState baseState = new DummyState();
        final DummyState overlayState = new DummyState();

        gsm.pushState(baseState);
        gsm.pushState(overlayState);
        assertSame(overlayState, gsm.getCurrentState());

        // Pop overlay state
        gsm.popState();

        // Underlying state is restored and resumed
        assertSame(baseState, gsm.getCurrentState());
        assertTrue(baseState.isResumed());

        // Subsequent updates hit the resumed base state
        gsm.update();
        assertEquals(1, baseState.getUpdateCount());
    }

    /**
     * Verifies that setState clears existing stack history and establishes a clean top-level state.
     */
    @Test
    void testSetStateClearsStack() {
        final DummyState firstState = new DummyState();
        final DummyState replacementState = new DummyState();

        gsm.pushState(firstState);
        gsm.setState(replacementState);

        assertSame(replacementState, gsm.getCurrentState());
        assertTrue(replacementState.isInitialized());

        // Popping should leave the stack empty
        gsm.popState();
        assertEquals(null, gsm.getCurrentState());
    }
}
