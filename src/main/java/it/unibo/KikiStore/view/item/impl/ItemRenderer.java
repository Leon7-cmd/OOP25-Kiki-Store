package it.unibo.KikiStore.view.item.impl;

import it.unibo.KikiStore.view.item.api.ItemRenderData;
import it.unibo.KikiStore.view.utility.SpriteManager;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.List;

/**
 * Handles the visual representation of all items in the game world.
 * It supports both static textures and horizontal sprite-sheet animations.
 */
public class ItemRenderer {

    private final SpriteManager spriteManager;
    private final double SPRITE_SIZE = 64.0;

    public ItemRenderer(final SpriteManager spriteManager) {
        this.spriteManager = spriteManager;
    }

    /**
     * Renders a list of items onto the canvas.
     * 
     * @param gc         The GraphicsContext used for drawing.
     * @param items      The list of DTOs containing item position and metadata.
     * @param frameCount The global engine frame counter used to synchronize
     *                   animations.
     */
    public void render(final GraphicsContext gc, final List<ItemRenderData> items, final int frameCount) {
        for (final ItemRenderData data : items) {

            // --- ANIMATED ITEM LOGIC ---
            if (data.isAnimated()) {
                // 1. Retrieve the horizontal sprite sheet from the cache
                final Image spriteSheet = spriteManager.getSpriteSheet(data.itemId());

                if (spriteSheet != null) {
                    // 2. Determine the total number of frames based on image width
                    final int totalFrames = (int) (spriteSheet.getWidth() / SPRITE_SIZE);

                    // 3. Calculate current frame using the frameCount (20 frames per second
                    // animation speed)
                    final int currentFrame = (frameCount / 20) % totalFrames;

                    // 4. Find the starting point (X)
                    final double sourceX = currentFrame * SPRITE_SIZE;

                    // 5. Draw the specific 64x64 sub-rectangle of the image
                    gc.drawImage(
                            spriteSheet,
                            sourceX, 0, SPRITE_SIZE, SPRITE_SIZE,
                            data.x(), data.y(), data.width(), data.height());
                }
                // --- STATIC ITEM LOGIC ---
            } else {
                final Image sprite = spriteManager.getStaticSprite(data.itemId());
                if (sprite != null) {
                    gc.drawImage(sprite, data.x(), data.y(), data.width(), data.height());
                }
                if (sprite == null) {
                    // Optional: Draw a placeholder or log a warning if the sprite is missing
                    System.err.println("Warning: Sprite not found for item ID: " + data.itemId());
                }
            }
        }
    }
}
