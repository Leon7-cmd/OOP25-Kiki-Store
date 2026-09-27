package it.unibo.kikiStore.view.hud.impl;

import it.unibo.kikiStore.view.hud.api.HUDRenderData;
import it.unibo.kikiStore.view.utility.SpriteManager;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Class used to display HUD for energy and money.
 */
public final class HUDRenderer {

    private static final double SCALE = 2.0;

    // Energy variables
    private static final double ENERGY_START_X = 180 * SCALE;
    private static final double ENERGY_START_Y = 20 * SCALE;
    private static final double TEXT_ENERGY_OFFSET_X = -45 * SCALE;
    private static final double TEXT_ENERGY_OFFSET_Y = 12 * SCALE;

    // Coin variables
    private static final double COIN_START_X = 20 * SCALE;
    private static final double COIN_START_Y = 18 * SCALE;
    private static final double TEXT_COIN_OFFSET_X = 24 * SCALE;
    private static final double TEXT_COIN_OFFSET_Y = 14 * SCALE;

    // Utility variables
    private static final double FONT_SIZE = 14 * SCALE;
    private static final double ICON_SIZE = 16 * SCALE;

    private final SpriteManager spriteManager;

    /**
     * Constructs a new HUDRenderer with the specified SpriteManager.
     *
     * @param spriteManager the SpriteManager used to retrieve sprites for rendering
     */
    public HUDRenderer(final SpriteManager spriteManager) {
        this.spriteManager = spriteManager;
    }

    /**
     * Renders the HUD elements (energy and money) on the GraphicsContext.
     *
     * @param gc   the GraphicsContext to draw on
     * @param data the HUDRenderData containing current energy and coin information
     */
    public void render(final GraphicsContext gc, final HUDRenderData data) {
        final double screenWidth = gc.getCanvas().getWidth();
        final double hudX = screenWidth - ENERGY_START_X; 
        gc.save();
        gc.setImageSmoothing(false);

        // --------- ENERGY STATS ---------
        final Image energySprite = spriteManager.getSpriteSheet("sprites/hud/energy" + data.currentEnergy());
        if (energySprite != null) {
            final double energyWidth = energySprite.getWidth() * SCALE;
            final double energyHeight = energySprite.getHeight() * SCALE;
            gc.drawImage(energySprite, hudX, ENERGY_START_Y, energyWidth, energyHeight);
        }
        gc.setFont(Font.font("Helvetica", FontWeight.BOLD, FONT_SIZE));
        gc.setFill(Color.WHITE);
        gc.fillText(
            data.currentEnergy() + " / " + data.maxEnergy(), 
            hudX + TEXT_ENERGY_OFFSET_X,
            ENERGY_START_Y + TEXT_ENERGY_OFFSET_Y
        );

        // --------- MONEY ---------
        final Image coinSprite = spriteManager.getSpriteSheet("sprites/hud/coin");
        if (coinSprite != null) {
            gc.drawImage(coinSprite, COIN_START_X, COIN_START_Y, ICON_SIZE, ICON_SIZE);
        }
        gc.setFont(Font.font("Helvetica", FontWeight.BOLD, FONT_SIZE));
        gc.fillText("x " + data.coins(), COIN_START_X + TEXT_COIN_OFFSET_X, COIN_START_Y + TEXT_COIN_OFFSET_Y);

        gc.restore();
    }
}
