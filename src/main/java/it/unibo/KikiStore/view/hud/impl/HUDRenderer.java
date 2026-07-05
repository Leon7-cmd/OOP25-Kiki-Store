package it.unibo.KikiStore.view.hud.impl;

import it.unibo.KikiStore.view.hud.api.HUDRenderData;
import it.unibo.KikiStore.view.utility.SpriteManager;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Font;

/**
 * Class used to display HUD for energy and money.
 */
public final class HUDRenderer {

    // Energy variables
    private static final int ENERGY_START_X = 120;
    private static final int ENERGY_START_Y = 20;
    private static final int TEXT_ENERGY_OFFSET_X = -35;
    private static final int TEXT_ENERGY_OFFSET_Y = 10;

    // Coin variables
    private static final int COIN_START_X = 20;
    private static final int COIN_START_Y = 18;
    private static final int TEXT_COIN_OFFSET_X = 24;
    private static final int TEXT_COIN_OFFSET_Y = 12;

    // Utility variables
    private static final int FONT_SIZE = 12;
    private static final int ICON_SIZE = 16;

    private final SpriteManager spriteManager;

    /**
     * Constructs HUDRenderer.
     * 
     * @param spriteManager class used to retrive the sprites
     */
    public HUDRenderer(final SpriteManager spriteManager) {
        this.spriteManager = spriteManager;
    }

    /**
     * Draws all visual elements of the state on the screen.
     * 
     * @param gc The JavaFX GraphicsContext to use for drawing on the Canvas.
     * @param data The data that needs to be displayed
     */
    public void render(final GraphicsContext gc, final HUDRenderData data) {
        final double screenWidth = gc.getCanvas().getWidth();
        final double hudX = screenWidth - ENERGY_START_X; 
        gc.save();

        // --------- ENERGY STATS ---------
        final Image energySprite = spriteManager.getSpriteSheet("sprites/hud/energy" + data.currentEnergy());
        if (energySprite != null) {
            gc.drawImage(energySprite, hudX, ENERGY_START_Y);
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
