package it.unibo.kikistore.view.states.pause.impl;

import it.unibo.kikistore.view.states.pause.api.PauseView;
import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

/**
 * Implementation of PauseView rendering the pause overlay.
 */
public final class PauseViewImpl implements PauseView {

    // Layout Constants
    private static final double TITLE_OFFSET_Y = 50.0;
    private static final double BUTTON_CORNER_RADIUS = 12.0;
    private static final double BUTTON_BORDER_WIDTH = 2.5;
    private static final double BUTTON_WIDTH = 280.0;
    private static final double BUTTON_HEIGHT = 55.0;
    private static final double BUTTON_GAP = 25.0;
    private static final double DARKEN_OPACITY = 0.55;
    private static final double BUTTON_OPACITY = 0.95;

    // Font Constants
    private static final String FONT_FAMILY = "Helvetica";
    private static final double TITLE_FONT_SIZE = 42.0;
    private static final double BUTTON_FONT_SIZE = 20.0;

    // Color Palette
    private static final Color RESUME_BASE_COLOR = Color.web("#3d5e2a", BUTTON_OPACITY);
    private static final Color RESUME_BORDER_COLOR = Color.web("#5b8f36");
    private static final Color TITLE_BASE_COLOR = Color.web("#6e2233", BUTTON_OPACITY);
    private static final Color TITLE_BORDER_COLOR = Color.web("#9c334c");

    private Rectangle2D resumeButtonBounds;
    private Rectangle2D titleButtonBounds;

    @Override
    public void render(final GraphicsContext gc) {
        final double screenWidth = gc.getCanvas().getWidth();
        final double screenHeight = gc.getCanvas().getHeight();

        gc.save();
        gc.setTransform(1, 0, 0, 1, 0, 0);

        // 1. Black overlay
        gc.setFill(Color.rgb(0, 0, 0, DARKEN_OPACITY));
        gc.fillRect(0, 0, screenWidth, screenHeight);

        // 2. Button coordinates
        final double centerX = (screenWidth - BUTTON_WIDTH) / 2.0;
        final double centerY = screenHeight / 2.0;
        final double resumeY = centerY - BUTTON_HEIGHT;
        final double titleY = resumeY + BUTTON_HEIGHT + BUTTON_GAP;

        this.resumeButtonBounds = new Rectangle2D(centerX, resumeY, BUTTON_WIDTH, BUTTON_HEIGHT);
        this.titleButtonBounds = new Rectangle2D(centerX, titleY, BUTTON_WIDTH, BUTTON_HEIGHT);

        // 3. Button Title
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font(FONT_FAMILY, FontWeight.BOLD, TITLE_FONT_SIZE));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.BOTTOM);
        gc.fillText("PAUSA", screenWidth / 2.0, resumeY - TITLE_OFFSET_Y);

        // 4. Button Graphic
        drawButton(gc, resumeButtonBounds, "CONTINUA", RESUME_BASE_COLOR, RESUME_BORDER_COLOR);
        drawButton(gc, titleButtonBounds, "MENU PRINCIPALE", TITLE_BASE_COLOR, TITLE_BORDER_COLOR);

        gc.restore();
    }

    @Override
    public boolean isResumeClicked(final double x, final double y) {
        return resumeButtonBounds != null && resumeButtonBounds.contains(x, y);
    }

    @Override
    public boolean isTitleClicked(final double x, final double y) {
        return titleButtonBounds != null && titleButtonBounds.contains(x, y);
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
