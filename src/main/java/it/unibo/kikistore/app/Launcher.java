package it.unibo.kikistore.app;

/**
 * Utility class to launch the game.
 */
public final class Launcher {

    private Launcher() {

    }

    /**
     * Method that lauches the main class.
     * 
     * @param args main agruments
     */
    public static void main(final String[] args) {
        final String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("nix") || os.contains("nux")) {
            // Fix for Linux systems to avoid rendering issues with JavaFX (Tested on Nobara Linux 38)
            System.setProperty("prism.vsync", "false");
            System.setProperty("prism.allowhidpi", "false");
            System.setProperty("prism.order", "es2,sw");
        }
        Main.main(args);
    }
}
