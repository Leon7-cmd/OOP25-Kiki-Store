package it.unibo.kikiStore.view.utility;

/**
 * Utility class that manages the camera.
 */
public class Camera {
    private static final double DEFAULT_ZOOM = 2;
    private static final double PLAYER_SIZE = 64.0;

    private double x;
    private double y;
    private double cameraHeight;
    private double cameraWidth;
    private double zoom = DEFAULT_ZOOM;

    /**
     * Method used to calculate the center point for the camera every frame.
     * 
     * @param targetX The x coordinate of the camera target
     * @param targetY The y coordinate of the camera target
     * @param screenWidth The width of the screen
     * @param screenHeight The height of the screen
     */
    public void update(final double targetX, final double targetY, final double screenWidth, final double screenHeight) {
        this.cameraWidth = screenWidth / zoom;
        this.cameraHeight = screenHeight / zoom;

        this.x = targetX - (this.cameraWidth / 2.0) + (PLAYER_SIZE / 2.0);
        this.y = targetY - (this.cameraHeight / 2.0) + (PLAYER_SIZE / 2.0);
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
     * @return The height of the visible area
     */
    public double getH() {
        return cameraHeight; 
    }

    /**
     * @return The width of the visible area
     */
    public double getW() {
        return cameraWidth; 
    }

    /**
     * @return The current zoom
     */
    public double getZoom() {
        return zoom;
    }

    /**
     * @param zoom The new zoom to set
     */
    public void setZoom(final double zoom) {
        this.zoom = zoom;
    }
}
