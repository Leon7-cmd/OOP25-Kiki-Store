package it.unibo.KikiStore.view.book;

import it.unibo.KikiStore.view.utility.SpriteManager;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 * Handles frame-by-frame playback of a book animation spritesheet
 * (open, close, turn left, turn right). Reusable for any grid-based
 * animation sequence.
 */
public class BookAnimator {
    private final Image sheet;
    private final int frameW;
    private final int frameH;
    private final int totalFrames;
    private final int cols;
    private int currentFrame;
    private int tickCounter;
    private boolean playing;

    /**
     * @param spriteManager sprite manager used to load the sheet
     * @param spriteId      path to the spritesheet (no extension)
     * @param cols          number of columns in the frame grid
     * @param rows          number of rows in the frame grid
     */
    public BookAnimator(final SpriteManager spriteManager, final String spriteId,
            final int cols, final int rows) {
        this.sheet = spriteManager.getStaticSprite(spriteId);
        this.cols = cols;
        if (sheet != null) {
            this.frameW = (int) (sheet.getWidth() / cols);
            this.frameH = (int) (sheet.getHeight() / rows);
        } else {
            this.frameW = 0;
            this.frameH = 0;
        }
        this.totalFrames = cols * rows;
    }

    /** Starts playback from frame 0. */
    public void play() {
        currentFrame = 0;
        tickCounter = 0;
        playing = true;
    }

    /** Jumps directly to the last frame (skip animation). */
    public void skipToEnd() {
        currentFrame = totalFrames - 1;
        playing = false;
    }

    /** Jumps directly to the first frame. */
    public void skipToStart() {
        currentFrame = 0;
        playing = false;
    }

    /**
     * Checks whether the animation has finished playing.
     *
     * @return true if playback finished (reached last frame)
     */
    public boolean isFinished() {
        return !playing;
    }

    /** Advances the animation by one game tick. Call every frame from update(). */
    public void update() {
        if (!playing) {
            return;
        }
        tickCounter++;
        if (tickCounter % 4 == 0) {
            currentFrame++;
            if (currentFrame >= totalFrames - 1) {
                currentFrame = totalFrames - 1;
                playing = false;
            }
        }
    }

    /**
     * Draws the current frame at the given screen position.
     *
     * @param gc graphics context
     * @param x  destination x
     * @param y  destination y
     * @param w  destination width
     * @param h  destination height
     */
    public void render(final GraphicsContext gc, final double x, final double y,
            final double w, final double h) {
        if (sheet == null) {
            return; // fallback
        }
        final int col = currentFrame % cols;
        final int row = currentFrame / cols;
        gc.drawImage(sheet, col * frameW, row * frameH, frameW, frameH, x, y, w, h);
    }
}
