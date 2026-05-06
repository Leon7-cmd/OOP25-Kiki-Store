package it.unibo.KikiStore.view.utility;

/**
 * Utility class to calculate the viewport offset.
 */
public class Camera {
    private double x, y;

    public void update(double targetX, double targetY, double screenWidth, double screenHeight) {
        // Calculation to center the target (Kiki)
        this.x = targetX - (screenWidth / 2) + (64 / 2);
        this.y = targetY - (screenHeight / 2) + (64 / 2);
    }

    public double getX() { return x; }
    public double getY() { return y; }
}