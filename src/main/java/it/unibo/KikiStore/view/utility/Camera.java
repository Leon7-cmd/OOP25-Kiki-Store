package it.unibo.KikiStore.view.utility;

/**
 * Utility class that manages the camera.
 */
public class Camera {
    private double x;
    private double y;
    private double cameraHeight;
    private double cameraWidth;

    /**
     * Method used to calculate the center point for the camera every frame.
     * 
     * @param targetX The x coordinate of the camera target
     * @param targetY The y coordinate of the camera target
     * @param screenWidth The width of the screen
     * @param screenHeight The height of the screen
     */
    public void update(final double targetX, final double targetY, final double screenWidth, final double screenHeight) {
        this.x = targetX - (screenWidth / 2) + (64 / 2);
        this.y = targetY - (screenHeight / 2) + (64 / 2);
        this.cameraHeight = screenHeight;
        this.cameraWidth = screenWidth;
    }

    /**
     * @return The x coordinate of the camera
     */
    public double getX() { 
        return x; 
    }

    /**
     * @return The y coordinate of the camera
     */
    public double getY() {
        return y; 
    }

    /**
     * @return The height of the camera
     */
    public double getH() {
        return cameraHeight; 
    }

    /**
     * @return The width of the camera
     */
    public double getW() {
        return cameraWidth; 
    }
}