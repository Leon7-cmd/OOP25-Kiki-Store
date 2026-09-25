package it.unibo.KikiStore.model.utility;

public record BoundingBox(double x, double y, double width, double height) {
    /**
     * Check the collision with another bounding box
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
