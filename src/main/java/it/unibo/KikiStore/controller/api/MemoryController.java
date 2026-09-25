package it.unibo.KikiStore.controller.api;

import it.unibo.KikiStore.model.memory.api.MemoryBoard;

/**
 * Controls functioning of a emory minigame session - turning cards, tracking
 * time
 * and the number of moves, and granting a reward once the board is complete.
 */
public interface MemoryController {
    /**
     * Starts a new game: builds a new shuffled board, resets the
     * timer and move counter, and previews all cards for a a short time.
     */
    void startNewGame();

    /**
     * Advances the game by one tick - handles the preview delay, the
     * timer, and the mismatch delay before flipping cards back.
     */
    void update();

    /**
     * Flips the card at the given index, if the game is currently
     * accepting input and the card is face down.
     *
     * @param index the index of the card to flip
     */
    void flipCard(int index);

    /**
     * Returns the current board, for the view to render.
     *
     * @return the memory board
     */
    MemoryBoard getBoard();

    /**
     * Returns how many seconds have passed since gameplay started
     * (not counting the initial preview).
     *
     * @return the passed time in seconds, the elapsed time
     */
    int getElapsedSeconds();

    /**
     * Returns how many moves (pairs of cards flipped) the player has made.
     *
     * @return the move count
     */
    int getMoveCount();

    /**
     * Checks whether the current game has been completed.
     *
     * @return true if the board is fully matched
     */
    boolean isGameComplete();

    /**
     * Returns the best score achieved so far this session.
     *
     * @return the best score, or -1 if no game has been completed yet
     */
    int getBestScore();

    /**
     * Returns the score of the most recently completed game.
     *
     * @return the last game's score, or -1 if no game has been completed yet
     */
    int getLastScore();

    /**
     * Checks whether the most recently completed game earned the big
     * reward (energy + random potion) rather than coins.
     *
     * @return true if the big reward was granted
     */
    boolean wasLastRewardBig();
}
