package it.unibo.KikiStore.controller.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import it.unibo.KikiStore.model.inventory.api.GameCatalog;
import it.unibo.KikiStore.model.inventory.impl.GameCatalogImpl;
import it.unibo.KikiStore.model.memory.api.CardState;
import it.unibo.KikiStore.model.memory.api.MemoryCard;
import it.unibo.KikiStore.model.player.api.Player;
import it.unibo.KikiStore.model.player.impl.PlayerImpl;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link MemoryControllerImpl}.
 */
class MemoryControllerImplTest {

    private static final int PREVIEW_DURATION_TICKS = 60;

    private MemoryControllerImpl controller;

    /**
     * Builds a fresh memory controller and starts a new game before each test.
     */
    @BeforeEach
    void setUp() {
        final GameCatalog catalog = new GameCatalogImpl("textFiles/ingredients.json", "textFiles/potions.json");
        final InventoryControllerImpl inventoryController = new InventoryControllerImpl();
        final Player player = new PlayerImpl(0, 0);
        controller = new MemoryControllerImpl(catalog, inventoryController, player);

        controller.startNewGame();
    }

    /**
     * Advances the controller past the initial preview phase, so the
     * board is interactive.
     */
    private void skipPreview() {
        for (int i = 0; i < PREVIEW_DURATION_TICKS; i++) {
            controller.update();
        }
    }

    /**
     * Verifies that a new game creates a board with 20 cards (10 pairs)
     * and resets time and moves to zero.
     */
    @Test
    void startNewGameCreatesFullBoard() {
        assertEquals(20, controller.getBoard().getCards().size());
        assertEquals(0, controller.getMoveCount());
        assertFalse(controller.isGameComplete());
    }

    /**
     * Verifies that flipping two cards with the same pair id matches them.
     */
    @Test
    void flippingMatchingCardsMarksThemAsMatched() {
        skipPreview();
        final int[] pair = findMatchingPair();

        controller.flipCard(pair[0]);
        controller.flipCard(pair[1]);

        final List<MemoryCard> cards = controller.getBoard().getCards();
        assertEquals(CardState.MATCHED, cards.get(pair[0]).getState());
        assertEquals(CardState.MATCHED, cards.get(pair[1]).getState());
        assertEquals(1, controller.getMoveCount());
    }

    /**
     * Verifies that flipping two non-matching cards hides them again
     * after the mismatch delay has passed.
     */
    @Test
    void flippingMismatchedCardsHidesThemAfterDelay() {
        skipPreview();
        final int[] mismatch = findMismatchedPair();

        controller.flipCard(mismatch[0]);
        controller.flipCard(mismatch[1]);

        for (int i = 0; i < PREVIEW_DURATION_TICKS; i++) {
            controller.update();
        }

        final List<MemoryCard> cards = controller.getBoard().getCards();
        assertEquals(CardState.HIDDEN, cards.get(mismatch[0]).getState());
        assertEquals(CardState.HIDDEN, cards.get(mismatch[1]).getState());
    }

    /**
     * Finds the indices of two cards sharing the same pair id.
     *
     * @return an array with the two matching indices
     */
    private int[] findMatchingPair() {
        final List<MemoryCard> cards = controller.getBoard().getCards();
        for (int i = 0; i < cards.size(); i++) {
            for (int j = i + 1; j < cards.size(); j++) {
                if (cards.get(i).getPairId() == cards.get(j).getPairId()) {
                    return new int[] { i, j };
                }
            }
        }
        throw new IllegalStateException("No matching pair found");
    }

    /**
     * Finds the indices of two cards with different pair ids.
     *
     * @return an array with the two mismatched indices
     */
    private int[] findMismatchedPair() {
        final List<MemoryCard> cards = controller.getBoard().getCards();
        for (int i = 0; i < cards.size(); i++) {
            for (int j = i + 1; j < cards.size(); j++) {
                if (cards.get(i).getPairId() != cards.get(j).getPairId()) {
                    return new int[] { i, j };
                }
            }
        }
        throw new IllegalStateException("No mismatched pair found");
    }
}
