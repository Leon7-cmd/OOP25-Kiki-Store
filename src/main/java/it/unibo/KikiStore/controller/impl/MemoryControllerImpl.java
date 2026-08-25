package it.unibo.KikiStore.controller.impl;

import it.unibo.KikiStore.controller.api.InventoryController;
import it.unibo.KikiStore.controller.api.MemoryController;
import it.unibo.KikiStore.model.inventory.api.GameCatalog;
import it.unibo.KikiStore.model.inventory.api.Potion;
import it.unibo.KikiStore.model.memory.api.CardState;
import it.unibo.KikiStore.model.memory.api.MemoryBoard;
import it.unibo.KikiStore.model.memory.api.MemoryCard;
import it.unibo.KikiStore.model.memory.impl.MemoryBoardImpl;
import it.unibo.KikiStore.model.player.impl.PlayerImpl;
import it.unibo.KikiStore.model.player.api.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Implementation of the memory game controller. Manages a
 * preview phase, gameplay with a mismatch delay, and reward granting
 * based on time and moves once the board is complete.
 */
public final class MemoryControllerImpl implements MemoryController {

    private enum Phase {
        PREVIEW, PLAYING, CHECKING_MISMATCH, COMPLETE
    }

    private static final int TICKS_PER_SECOND = 60;
    private static final int PREVIEW_DURATION_TICKS = 60;
    private static final int MISMATCH_DELAY_TICKS = 45;

    private static final int SCORE_BASE = 1000;
    private static final int TIME_THRESHOLD_SECONDS = 30;
    private static final int MOVES_THRESHOLD = 12;
    private static final int PENALTY_PER_EXTRA_SECOND = 8;
    private static final int PENALTY_PER_EXTRA_MOVE = 20;

    private static final int ENERGY_BOOST_AMOUNT = 20;
    private static final int COIN_DIVISOR = 10;
    private static final int MIN_COINS = 1;

    private final GameCatalog gameCatalog;
    private final InventoryController inventoryController;
    private final Player player;
    private final Random random = new Random();

    private MemoryBoard board;
    private Phase phase = Phase.COMPLETE;

    private int previewTicksRemaining;
    private int mismatchTicksRemaining;
    private int elapsedTicks;
    private int moveCount;
    private int bestScore = -1;
    private int lastScore = -1;
    private boolean lastRewardWasBig;

    private Integer firstFlippedIndex;
    private Integer secondFlippedIndex;

    /**
     * @param gameCatalog         the full item catalog, used to pick the potion
     *                            sprites for the pairs
     * @param inventoryController the inventory controller, used to grant the random
     *                            potion reward
     * @param player the player, used to grant energy and money rewards
     */
    public MemoryControllerImpl(final GameCatalog gameCatalog,
            final InventoryController inventoryController,
            final Player player) {
        this.gameCatalog = gameCatalog;
        this.inventoryController = inventoryController;
        this.player = player;
    }

    @Override
    public void startNewGame() {
        final List<String> pairImagePaths = new ArrayList<>();
        for (final Potion potion : gameCatalog.getAllPotions()) {
            pairImagePaths.add(potion.getImagePath());
        }

        board = new MemoryBoardImpl(pairImagePaths);
        phase = Phase.PREVIEW;
        previewTicksRemaining = PREVIEW_DURATION_TICKS;
        mismatchTicksRemaining = 0;
        elapsedTicks = 0;
        moveCount = 0;
        firstFlippedIndex = null;
        secondFlippedIndex = null;

        for (final MemoryCard card : board.getCards()) {
            card.setState(CardState.REVEALED);
        }
    }

    @Override
    public void update() {
        switch (phase) {
            case PREVIEW:
                previewTicksRemaining--;
                if (previewTicksRemaining <= 0) {
                    hideAllRevealedCards();
                    phase = Phase.PLAYING;
                }
                break;
            case PLAYING:
                elapsedTicks++;
                break;
            case CHECKING_MISMATCH:
                elapsedTicks++;
                mismatchTicksRemaining--;
                if (mismatchTicksRemaining <= 0) {
                    resolveMismatch();
                }
                break;
            case COMPLETE:
                break;
            default:
                break;
        }
    }

    /**
     * Flips every currently revealed (but not matched) card back to hidden.
     * Used to end the preview phase.
     */
    private void hideAllRevealedCards() {
        for (final MemoryCard card : board.getCards()) {
            if (card.getState() == CardState.REVEALED) {
                card.setState(CardState.HIDDEN);
            }
        }
    }

    @Override
    public void flipCard(final int index) {
        if (phase != Phase.PLAYING) {
            return;
        }
        final MemoryCard card = board.getCards().get(index);
        if (card.getState() != CardState.HIDDEN) {
            return;
        }

        card.setState(CardState.REVEALED);

        if (firstFlippedIndex == null) {
            firstFlippedIndex = index;
            return;
        }

        secondFlippedIndex = index;
        moveCount++;

        final MemoryCard first = board.getCards().get(firstFlippedIndex);
        final MemoryCard second = board.getCards().get(secondFlippedIndex);

        if (first.getPairId() == second.getPairId()) {
            first.setState(CardState.MATCHED);
            second.setState(CardState.MATCHED);
            firstFlippedIndex = null;
            secondFlippedIndex = null;
            if (board.isComplete()) {
                completeGame();
            }
        } else {
            phase = Phase.CHECKING_MISMATCH;
            mismatchTicksRemaining = MISMATCH_DELAY_TICKS;
        }
    }

    /**
     * Flips the two mismatched cards back to hidden and resumes gameplay.
     */
    private void resolveMismatch() {
        board.getCards().get(firstFlippedIndex).setState(CardState.HIDDEN);
        board.getCards().get(secondFlippedIndex).setState(CardState.HIDDEN);
        firstFlippedIndex = null;
        secondFlippedIndex = null;
        phase = Phase.PLAYING;
    }

    /**
     * Marks the game as complete, updates the session best score, and
     * grants the reward based on the final time and move count.
     */
    private void completeGame() {
        phase = Phase.COMPLETE;
        final int score = calculateScore();
        lastScore = score;
        if (score > bestScore) {
            bestScore = score;
        }
        grantReward(score);
    }

    /**
     * Calculates the final score, penalizing time and moves spent
     * beyond their respective thresholds.
     *
     * @return the calculated score, never negative
     */
    private int calculateScore() {
        final int extraSeconds = Math.max(0, getElapsedSeconds() - TIME_THRESHOLD_SECONDS);
        final int extraMoves = Math.max(0, moveCount - MOVES_THRESHOLD);
        final int score = SCORE_BASE
                - extraSeconds * PENALTY_PER_EXTRA_SECOND
                - extraMoves * PENALTY_PER_EXTRA_MOVE;
        return Math.max(0, score);
    }

    /**
     * Grants the reward for the completed game: an energy boost plus a
     * random potion if the player finished within both thresholds,
     * otherwise coins proportional to the final score.
     *
     * @param score the final score for this game
     */
    private void grantReward(final int score) {
        final boolean fastAndFewMoves = getElapsedSeconds() <= TIME_THRESHOLD_SECONDS
                && moveCount <= MOVES_THRESHOLD;
        lastRewardWasBig = fastAndFewMoves;

        if (fastAndFewMoves) {
            player.setEnergy(player.getEnergy() + ENERGY_BOOST_AMOUNT);
            addRandomPotionToInventory();
        } else {
            final int coins = Math.max(MIN_COINS, score / COIN_DIVISOR);
            player.setMoney(player.getMoney() + coins);
        }
    }

    /**
     * Adds one random potion from the catalog to the player's inventory.
     */
    private void addRandomPotionToInventory() {
        final List<Potion> allPotions = gameCatalog.getAllPotions();
        final Potion chosen = allPotions.get(random.nextInt(allPotions.size()));
        inventoryController.addPotion(chosen.getName(), chosen.getImagePath(), 1,
                chosen.getDescription(), chosen.getEffect(), false);
    }

    @Override
    public MemoryBoard getBoard() {
        return board;
    }

    @Override
    public int getElapsedSeconds() {
        return elapsedTicks / TICKS_PER_SECOND;
    }

    @Override
    public int getMoveCount() {
        return moveCount;
    }

    @Override
    public boolean isGameComplete() {
        return phase == Phase.COMPLETE;
    }

    @Override
    public int getBestScore() {
        return bestScore;
    }

    @Override
    public int getLastScore() {
        return lastScore;
    }

    @Override
    public boolean wasLastRewardBig() {
        return lastRewardWasBig;
    }
}
