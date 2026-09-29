package it.unibo.kikistore.view.states.menu.impl;

import it.unibo.kikistore.view.states.menu.api.MenuView;
import it.unibo.kikistore.view.utility.SpriteManager;
import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

/**
 * Concrete implementation of MenuView.
 */
public final class MenuViewImpl implements MenuView {

    private static final String BACKGROUND_ID = "sprites/menu/background";
    private static final String FONT_FAMILY = "Helvetica";

    // Layout Constants
    private static final double BUTTON_WIDTH = 220.0;
    private static final double BUTTON_HEIGHT = 50.0;
    private static final double BUTTON_GAP = 15.0;
    private static final double MARGIN_LEFT = 60.0;
    private static final double MARGIN_BOTTOM = 60.0;

    // Button Constants
    private static final double BUTTON_OPACITY = 0.95;
    private static final double BUTTON_CORNER_RADIUS = 12.0;
    private static final double BUTTON_BORDER_WIDTH = 2.5;
    private static final double BUTTON_FONT_SIZE = 22.0;

    // Color Palette
    private static final Color PLAY_BASE_COLOR = Color.web("#3d5e2a", BUTTON_OPACITY);
    private static final Color PLAY_BORDER_COLOR = Color.web("#5b8f36");
    private static final Color QUIT_BASE_COLOR = Color.web("#6e2233", BUTTON_OPACITY);
    private static final Color QUIT_BORDER_COLOR = Color.web("#9c334c");

    private final SpriteManager spriteManager;
    private Rectangle2D startButtonBounds;
    private Rectangle2D quitButtonBounds;

    /**
     * Constructs a new MenuViewImpl.
     *
     * @param spriteManager the sprite cache manager
     */
    public MenuViewImpl(final SpriteManager spriteManager) {
        this.spriteManager = spriteManager;
    }

    @Override
    public void render(final GraphicsContext gc) {
        final double screenWidth = gc.getCanvas().getWidth();
        final double screenHeight = gc.getCanvas().getHeight();

        gc.save();
        gc.setImageSmoothing(false);

        final Image background = spriteManager.getStaticSprite(BACKGROUND_ID);
        if (background != null) {
            gc.drawImage(background, 0, 0, screenWidth, screenHeight);
        } else {
            gc.setFill(Color.BLACK);
            gc.fillRect(0, 0, screenWidth, screenHeight);
        }

        // Button positions
        final double btnX = MARGIN_LEFT;
        final double quitY = screenHeight - MARGIN_BOTTOM - BUTTON_HEIGHT;
        final double startY = quitY - BUTTON_HEIGHT - BUTTON_GAP;

        this.startButtonBounds = new Rectangle2D(btnX, startY, BUTTON_WIDTH, BUTTON_HEIGHT);
        this.quitButtonBounds = new Rectangle2D(btnX, quitY, BUTTON_WIDTH, BUTTON_HEIGHT);

        // Draw buttons
        drawButton(gc, startButtonBounds, "GIOCA", PLAY_BASE_COLOR, PLAY_BORDER_COLOR);
        drawButton(gc, quitButtonBounds, "ESCI", QUIT_BASE_COLOR, QUIT_BORDER_COLOR);

        gc.restore();
    }

    @Override
    public boolean isStartClicked(final double x, final double y) {
        return startButtonBounds != null && startButtonBounds.contains(x, y);
    }

    @Override
    public boolean isQuitClicked(final double x, final double y) {
        return quitButtonBounds != null && quitButtonBounds.contains(x, y);
    }

    private void drawButton(
        final GraphicsContext gc,
        final Rectangle2D bounds,
        final String text,
        final Color baseColor,
        final Color borderColor
    ) {
        gc.setFill(baseColor);
        gc.fillRoundRect(
            bounds.getMinX(),
            bounds.getMinY(),
            bounds.getWidth(),
            bounds.getHeight(),
            BUTTON_CORNER_RADIUS,
            BUTTON_CORNER_RADIUS
        );

        gc.setStroke(borderColor);
        gc.setLineWidth(BUTTON_BORDER_WIDTH);
        gc.strokeRoundRect(
            bounds.getMinX(),
            bounds.getMinY(),
            bounds.getWidth(),
            bounds.getHeight(),
            BUTTON_CORNER_RADIUS,
            BUTTON_CORNER_RADIUS
        );

        gc.setFill(Color.WHITE);
        gc.setFont(Font.font(FONT_FAMILY, FontWeight.BOLD, BUTTON_FONT_SIZE));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);

        final double textCenterX = bounds.getMinX() + (bounds.getWidth() / 2.0);
        final double textCenterY = bounds.getMinY() + (bounds.getHeight() / 2.0);
        gc.fillText(text, textCenterX, textCenterY);
    }
}
