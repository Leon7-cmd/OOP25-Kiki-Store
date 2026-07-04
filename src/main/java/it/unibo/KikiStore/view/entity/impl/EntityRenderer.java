package it.unibo.KikiStore.view.entity.impl;

import it.unibo.KikiStore.view.entity.api.EntityRenderData;
import it.unibo.KikiStore.view.utility.SpriteManager;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.List;

/**
 * Handles the graphical rendering of all entities on the game canvas.
 * It translates logical entity data into visual frames by slicing sprite sheets.
 */
public class EntityRenderer {

    private final SpriteManager spriteManager;

    /**
     * Constructor of EntityRenderer.
     * 
     * @param spriteManager used to take the sprite of the entity
     */
    public EntityRenderer(final SpriteManager spriteManager) {
        this.spriteManager = spriteManager;
    }

    /**
     * Renders a list of entities onto the canvas.
     * 
     * @param gc         The GraphicsContext used for drawing.
     * @param entities   A list of DTOs containing position and state data.
     * @param frameCount The global tick counter used to synchronize animations.
     */
    public void render(final GraphicsContext gc, final List<EntityRenderData> entities, final int frameCount) {
        for (final EntityRenderData data : entities) {

            // 1. Retrieve the full sprite sheet for the specific entity
            final Image sheet = spriteManager.getSpriteSheet(data.entityId());

            if (sheet != null) {
                // 2. Calculate frame dimensions.
                final double frameWidth = sheet.getWidth() / 4;
                final double frameHeight = sheet.getHeight() / 4;

                // 3. Determine the ROW based on facing direction.
                final int row = switch (data.direction()) {
                    case "down" -> 0;
                    case "up" -> 1;
                    case "left" -> 2;
                    case "right" -> 3;
                    default -> 0; // Fallback
                };

                // 4. Determine the COLUMN based on animation state.
                int col = 0; // Di base (es. state "idle") restiamo sul primo frame
                if ("walk".equals(data.state())) {
                    col = frameCount / 10 % 4; // Cicla tra 0, 1, 2, 3
                }

                // 5. Calculate the exact crop coordinates on the source image.
                final double sourceX = col * frameWidth;
                final double sourceY = row * frameHeight;

                // 6. Execute the draw call.
                gc.drawImage(
                    sheet, 
                    sourceX, sourceY, frameWidth, frameHeight, 
                    data.x(), data.y(), data.width(), data.height() 
                );
            } else {
                // Draw a magenta placeholder if the sprite is missing
                gc.setFill(javafx.scene.paint.Color.MAGENTA);
                gc.fillRect(data.x(), data.y(), data.width(), data.height());
            }
        }
    }
}
