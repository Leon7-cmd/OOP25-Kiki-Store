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
    private static final double BASE_ENERGY_X = 100.0;
    private static final double BASE_ENERGY_Y = 20.0;
    private static final double BASE_TEXT_ENERGY_OFFSET_X = -45.0;
    private static final double BASE_TEXT_ENERGY_OFFSET_Y = 12.0;

    // Coin variables
    private static final double BASE_COIN_X = 20.0;
    private static final double BASE_COIN_Y = 18.0;
    private static final double BASE_TEXT_COIN_OFFSET_X = 24.0;
    private static final double BASE_TEXT_COIN_OFFSET_Y = 14.0;

    // Utility variables
    private static final double BASE_FONT_SIZE = 14.0;
    private static final double BASE_ICON_SIZE = 16.0;

    // Scaled constants
    private static final double ENERGY_START_X = BASE_ENERGY_X * SCALE;
    private static final double ENERGY_START_Y = BASE_ENERGY_Y * SCALE;
    private static final double TEXT_ENERGY_OFFSET_X = BASE_TEXT_ENERGY_OFFSET_X * SCALE;
    private static final double TEXT_ENERGY_OFFSET_Y = BASE_TEXT_ENERGY_OFFSET_Y * SCALE;

    private static final double COIN_START_X = BASE_COIN_X * SCALE;
    private static final double COIN_START_Y = BASE_COIN_Y * SCALE;
    private static final double TEXT_COIN_OFFSET_X = BASE_TEXT_COIN_OFFSET_X * SCALE;
    private static final double TEXT_COIN_OFFSET_Y = BASE_TEXT_COIN_OFFSET_Y * SCALE;

    private static final double FONT_SIZE = BASE_FONT_SIZE * SCALE;
    private static final double ICON_SIZE = BASE_ICON_SIZE * SCALE;

    // Assets
    private static final String FONT_FAMILY = "Helvetica";
    private static final String ENERGY_SPRITE_PREFIX = "sprites/hud/energy";
    private static final String COIN_SPRITE_PATH = "sprites/hud/coin";

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
        final Image energySprite = spriteManager.getSpriteSheet(ENERGY_SPRITE_PREFIX + data.currentEnergy());
        if (energySprite != null) {
            final double energyWidth = energySprite.getWidth() * SCALE;
            final double energyHeight = energySprite.getHeight() * SCALE;
            gc.drawImage(energySprite, hudX, ENERGY_START_Y, energyWidth, energyHeight);
        }

        gc.setFont(Font.font(FONT_FAMILY, FontWeight.BOLD, FONT_SIZE));
        gc.setFill(Color.WHITE);
        gc.fillText(
            data.currentEnergy() + " / " + data.maxEnergy(), 
            hudX + TEXT_ENERGY_OFFSET_X,
            ENERGY_START_Y + TEXT_ENERGY_OFFSET_Y
        );

        // --------- MONEY ---------
        final Image coinSprite = spriteManager.getSpriteSheet(COIN_SPRITE_PATH);
        if (coinSprite != null) {
            gc.drawImage(coinSprite, COIN_START_X, COIN_START_Y, ICON_SIZE, ICON_SIZE);
        }
        gc.fillText("x " + data.coins(), COIN_START_X + TEXT_COIN_OFFSET_X, COIN_START_Y + TEXT_COIN_OFFSET_Y);

        gc.restore();
    }
}
