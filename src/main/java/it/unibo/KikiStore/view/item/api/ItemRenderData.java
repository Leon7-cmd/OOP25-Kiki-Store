package it.unibo.KikiStore.view.item.api;

/**
 * A DTO containing all the information necessary 
 * for the ItemRenderer to draw an object on the screen.
 *
 * @param x          The X-coordinate in the game world where the item should be drawn.
 * @param y          The Y-coordinate in the game world where the item should be drawn.
 * @param width      The visual width of the item on the canvas.
 * @param height     The visual height of the item on the canvas.
 * @param itemId     The unique identifier used by the SpriteManager to locate the correct texture.
 * @param isAnimated Flag indicating if the renderer should treat the asset as a multi-frame sprite sheet.
 */
public record ItemRenderData(
    double x,
    double y,
    double width,
    double height,
    String itemId, 
    boolean isAnimated
) { }
