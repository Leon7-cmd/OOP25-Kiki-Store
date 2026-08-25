package it.unibo.KikiStore.engine.impl;

import it.unibo.KikiStore.controller.api.InputHandler;
import it.unibo.KikiStore.controller.api.MemoryController;
import it.unibo.KikiStore.engine.api.GameState;
import it.unibo.KikiStore.engine.api.GameStateManager;
import it.unibo.KikiStore.model.memory.api.CardState;
import it.unibo.KikiStore.model.memory.api.MemoryCard;
import it.unibo.KikiStore.view.utility.SpriteManager;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.util.List;

/**
 * Memory minigame screen - a 5x4 grid of cards flipped with the mouse.
 * States: PLAYING (grid interactive, time/moves shown), RESULT
 * (final panel with score, reward, and a play-again prompt).
 */
public final class MemoryState implements GameState {

    private enum Phase {
        PLAYING, RESULT
    }

    private static final int COLS = 5;
    private static final int ROWS = 4;
    private static final int SECONDS_PER_MINUTE = 60;

    private static final double MARGIN = 60.0;
    private static final double TOP_MARGIN = 90.0;
    private static final double CARD_GAP = 10.0;
    private static final double CARD_PADDING = 8.0;

    private static final double TITLE_FONT_SIZE = 22.0;
    private static final double SMALL_FONT_SIZE = 14.0;

    private static final double PANEL_WIDTH = 400.0;
    private static final double PANEL_HEIGHT = 240.0;
    private static final double LINE_HEIGHT = 28.0;
    private static final double PROMPT_OFFSET_X = 50.0;
    private static final double BUTTON_WIDTH = 60.0;
    private static final double BUTTON_HEIGHT = 30.0;

    private static final Color COL_BG = Color.web("#C68642");
    private static final Color COL_CARD_BACK = Color.web("#5C3A1E");
    private static final Color COL_TEXT = Color.web("#3B2006");
    private static final Color COL_TEXT_DIM = Color.web("#5C4A3A");
    private static final Color COL_SELECTED_BORDER = Color.web("#1D9E75");
    private static final Color COL_PANEL_BG = Color.web("#F5E6C8");

    private final MemoryController memoryController;
    private final SpriteManager spriteManager;
    private final GameStateManager gsm;
    private final GameState previousState;
    private final InputHandler input;

    private final Font pixelFont;
    private final Font pixelFontSmall;

    private Phase phase = Phase.PLAYING;
    private boolean playAgainYesSelected = true;

    private boolean leftWasPressed;
    private boolean rightWasPressed;
    private boolean actionWasPressed;

    private double screenW = 1;
    private double screenH = 1;

    /**
     * @param memoryController the memory game controller
     * @param spriteManager    the sprite manager
     * @param gsm              the game state manager
     * @param previousState    state to return to on close
     * @param input            the input handler
     */
    public MemoryState(final MemoryController memoryController, final SpriteManager spriteManager,
            final GameStateManager gsm, final GameState previousState, final InputHandler input) {
        this.memoryController = memoryController;
        this.spriteManager = spriteManager;
        this.gsm = gsm;
        this.previousState = previousState;
        this.input = input;

        final Font loadedTitle = Font.loadFont(
                getClass().getResourceAsStream("/fonts/press_start_2p.ttf"), TITLE_FONT_SIZE);
        this.pixelFont = loadedTitle != null ? loadedTitle : Font.font("Monospace", TITLE_FONT_SIZE);
        final Font loadedSmall = Font.loadFont(
                getClass().getResourceAsStream("/fonts/press_start_2p.ttf"), SMALL_FONT_SIZE);
        this.pixelFontSmall = loadedSmall != null ? loadedSmall : Font.font("Monospace", SMALL_FONT_SIZE);
    }

    @Override
    public void init() {
        memoryController.startNewGame();
        phase = Phase.PLAYING;
        playAgainYesSelected = true;
    }

    @Override
    public void update() {
        memoryController.update();

        if (memoryController.isGameComplete()) {
            phase = Phase.RESULT;
        }

        switch (phase) {
            case PLAYING:
                updatePlaying();
                break;
            case RESULT:
                updateResult();
                break;
            default:
                break;
        }
    }

    private void updatePlaying() {
        if (!input.isMouseClicked()) {
            return;
        }
        final int index = cardIndexAt(input.getMouseX(), input.getMouseY());
        if (index >= 0) {
            memoryController.flipCard(index);
        }
    }

    private void updateResult() {
        final boolean leftNow = input.isLeft();
        final boolean rightNow = input.isRight();
        if ((leftNow && !leftWasPressed) || (rightNow && !rightWasPressed)) {
            playAgainYesSelected = !playAgainYesSelected;
        }
        leftWasPressed = leftNow;
        rightWasPressed = rightNow;

        final boolean actionNow = input.isAction();
        if (actionNow && !actionWasPressed) {
            confirmPlayAgain(playAgainYesSelected);
        }
        actionWasPressed = actionNow;

        if (input.isMouseClicked()) {
            final double mx = input.getMouseX();
            final double my = input.getMouseY();
            final double centerX = screenW / 2;
            final double promptY = screenH / 2 + PANEL_HEIGHT / 2 - LINE_HEIGHT;

            if (inButton(mx, my, centerX - PROMPT_OFFSET_X, promptY)) {
                confirmPlayAgain(true);
            } else if (inButton(mx, my, centerX + PROMPT_OFFSET_X, promptY)) {
                confirmPlayAgain(false);
            }
        }
    }

    /**
     * @param yes true if the player chose to play again
     */
    private void confirmPlayAgain(final boolean yes) {
        if (yes) {
            memoryController.startNewGame();
            phase = Phase.PLAYING;
        } else {
            gsm.setState(previousState);
        }
    }

    /**
     * @param px      point x
     * @param py      point y
     * @param centerX button center x
     * @param centerY button center y
     * @return true if the point is inside the button area
     */
    private boolean inButton(final double px, final double py, final double centerX, final double centerY) {
        return Math.abs(px - centerX) <= BUTTON_WIDTH / 2 && Math.abs(py - centerY) <= BUTTON_HEIGHT / 2;
    }

    /**
     * @param mx mouse x
     * @param my mouse y
     * @return the clicked card index, or -1 if outside the grid
     */
    private int cardIndexAt(final double mx, final double my) {
        final double gridW = screenW - MARGIN * 2;
        final double gridH = screenH - TOP_MARGIN - MARGIN;
        final double cardW = (gridW - CARD_GAP * (COLS - 1)) / COLS;
        final double cardH = (gridH - CARD_GAP * (ROWS - 1)) / ROWS;

        final double relX = mx - MARGIN;
        final double relY = my - TOP_MARGIN;
        if (relX < 0 || relY < 0) {
            return -1;
        }

        final int col = (int) (relX / (cardW + CARD_GAP));
        final int row = (int) (relY / (cardH + CARD_GAP));
        if (col >= COLS || row >= ROWS) {
            return -1;
        }
        if (relX - col * (cardW + CARD_GAP) > cardW || relY - row * (cardH + CARD_GAP) > cardH) {
            return -1;
        }
        return row * COLS + col;
    }

    @Override
    public void render(final GraphicsContext gc) {
        screenW = gc.getCanvas().getWidth();
        screenH = gc.getCanvas().getHeight();
        gc.setImageSmoothing(false);

        gc.setFill(COL_BG);
        gc.fillRect(0, 0, screenW, screenH);

        renderTopBar(gc);
        renderGrid(gc);

        switch (phase) {
            case RESULT:
                renderResult(gc);
                break;
            case PLAYING:
            default:
                break;
        }
    }

    private void renderTopBar(final GraphicsContext gc) {
        final List<MemoryCard> cards = memoryController.getBoard().getCards();
        int matched = 0;
        for (final MemoryCard card : cards) {
            if (card.getState() == CardState.MATCHED) {
                matched++;
            }
        }

        final String text = String.format("Time: %s   Moves: %d   Pairs: %d/%d",
                formatTime(memoryController.getElapsedSeconds()), memoryController.getMoveCount(),
                matched / 2, cards.size() / 2);

        gc.setFill(COL_TEXT);
        gc.setFont(pixelFontSmall);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText(text, screenW / 2, MARGIN / 2);
    }

    /**
     * @param totalSeconds the total elapsed seconds
     * @return the formatted MM:SS time string
     */
    private String formatTime(final int totalSeconds) {
        return String.format("%02d:%02d", totalSeconds / SECONDS_PER_MINUTE, totalSeconds % SECONDS_PER_MINUTE);
    }

    private void renderGrid(final GraphicsContext gc) {
        final double gridW = screenW - MARGIN * 2;
        final double gridH = screenH - TOP_MARGIN - MARGIN;
        final double cardW = (gridW - CARD_GAP * (COLS - 1)) / COLS;
        final double cardH = (gridH - CARD_GAP * (ROWS - 1)) / ROWS;

        final List<MemoryCard> cards = memoryController.getBoard().getCards();
        for (int i = 0; i < cards.size(); i++) {
            final double x = MARGIN + (i % COLS) * (cardW + CARD_GAP);
            final double y = TOP_MARGIN + (i / COLS) * (cardH + CARD_GAP);
            renderCard(gc, cards.get(i), x, y, cardW, cardH);
        }
    }

    /**
     * @param gc   graphics context
     * @param card the card to draw
     * @param x    card x
     * @param y    card y
     * @param w    card width
     * @param h    card height
     */
    private void renderCard(final GraphicsContext gc, final MemoryCard card,
            final double x, final double y, final double w, final double h) {
        if (card.getState() == CardState.HIDDEN) {
            gc.setFill(COL_CARD_BACK);
            gc.fillRect(x, y, w, h);
            return;
        }

        gc.setFill(COL_PANEL_BG);
        gc.fillRect(x, y, w, h);

        final Image sprite = spriteManager.getStaticSprite(card.getImagePath());
        if (sprite != null) {
            final double aspect = sprite.getWidth() / sprite.getHeight();
            double itemW = w - CARD_PADDING * 2;
            double itemH = itemW / aspect;
            if (itemH > h - CARD_PADDING * 2) {
                itemH = h - CARD_PADDING * 2;
                itemW = itemH * aspect;
            }
            gc.drawImage(sprite, x + (w - itemW) / 2, y + (h - itemH) / 2, itemW, itemH);
        }

        if (card.getState() == CardState.MATCHED) {
            gc.setStroke(COL_SELECTED_BORDER);
            gc.setLineWidth(3);
            gc.strokeRect(x + 1, y + 1, w - 2, h - 2);
        }
    }

    /**
     * @param gc graphics context
     */
    private void renderResult(final GraphicsContext gc) {
        gc.setFill(Color.rgb(0, 0, 0, 0.6));
        gc.fillRect(0, 0, screenW, screenH);

        final double centerX = screenW / 2;
        final double panelX = centerX - PANEL_WIDTH / 2;
        final double panelY = screenH / 2 - PANEL_HEIGHT / 2;

        gc.setFill(COL_PANEL_BG);
        gc.fillRect(panelX, panelY, PANEL_WIDTH, PANEL_HEIGHT);

        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFill(COL_TEXT);
        gc.setFont(pixelFont);
        double y = panelY + LINE_HEIGHT;
        gc.fillText("Board complete!", centerX, y);

        gc.setFont(pixelFontSmall);
        y += LINE_HEIGHT;
        gc.fillText("Time: " + formatTime(memoryController.getElapsedSeconds())
                + "   Moves: " + memoryController.getMoveCount(), centerX, y);
        y += LINE_HEIGHT;
        gc.fillText("Score: " + memoryController.getLastScore(), centerX, y);
        y += LINE_HEIGHT;

        gc.setFill(COL_TEXT_DIM);
        gc.fillText(memoryController.wasLastRewardBig()
                ? "Reward: energy boost + a random potion!"
                : "Reward: coins", centerX, y);
        y += LINE_HEIGHT;

        if (memoryController.getLastScore() == memoryController.getBestScore()) {
            gc.setFill(COL_SELECTED_BORDER);
            gc.fillText("New record!", centerX, y);
        }

        renderYesNoPrompt(gc, centerX, panelY + PANEL_HEIGHT - LINE_HEIGHT, "Play again?");
    }

    /**
     * Draws a "question / Yes No" prompt with the current selection highlighted.
     * Same layout used across the game's other minigame screens.
     *
     * @param gc       graphics context
     * @param centerX  horizontal center for the prompt
     * @param y        y position of the question line
     * @param question the question text
     */
    private void renderYesNoPrompt(final GraphicsContext gc, final double centerX, final double y,
            final String question) {
        gc.setFill(COL_TEXT);
        gc.setFont(pixelFontSmall);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText(question, centerX, y - LINE_HEIGHT / 2);

        gc.setFont(pixelFont);
        gc.setFill(playAgainYesSelected ? COL_SELECTED_BORDER : COL_TEXT_DIM);
        gc.fillText("Yes", centerX - PROMPT_OFFSET_X, y);
        gc.setFill(!playAgainYesSelected ? COL_SELECTED_BORDER : COL_TEXT_DIM);
        gc.fillText("No", centerX + PROMPT_OFFSET_X, y);
    }
}
