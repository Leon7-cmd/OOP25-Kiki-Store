package it.unibo.kikiStore.model.utility;

/**
 * Represents a rectangular area in the game world.
 *
 * @param x      the horizontal coordinate of the top-left corner
 * @param y      the vertical coordinate of the top-left corner
 * @param width  the width of the bounding box
 * @param height the height of the bounding box
 */
public record BoundingBox(double x, double y, double width, double height) {

    /**
     * Check the collision with another bounding box.
     */
    public boolean intersects(final BoundingBox other) {
        if (other == null) {
            return false;
        }
        return this.x < other.x() + other.width()
            && this.x + this.width > other.x()
            && this.y < other.y() + other.height()
            && this.y + this.height > other.y();
    }
}
