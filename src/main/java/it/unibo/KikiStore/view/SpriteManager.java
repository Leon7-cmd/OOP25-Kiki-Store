package it.unibo.KikiStore.view;

import javafx.scene.image.Image;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * A centralized manager for graphical assets.
 * It implements a Caching pattern to ensure images are loaded from disk only once,
 * significantly improving performance during the rendering cycle.
 */
public class SpriteManager {

    // Internal cache to store previously loaded images, indexed by their unique IDs/paths
    private final Map<String, Image> cache = new HashMap<>();

    /**
     * Retrieves a non-animated sprite.
     * 
     * @param spriteId The path/identifier of the sprite resource.
     * @return The Image object, or null if the resource is missing.
     */
    public Image getStaticSprite(String spriteId) {
        return loadFromCacheOrDisk(spriteId);
    }

    /**
     * Retrieves an animated sprite sheet.
     * 
     * @param entityId The path/identifier of the entity's sprite sheet.
     * @return The Image object containing the full sheet, or null if missing.
     */
    public Image getSpriteSheet(String entityId) {
        return loadFromCacheOrDisk(entityId);
    }

    /**
     * Core logic for asset retrieval. 
     * Checks the RAM first; if not found, it attempts to load from the resources folder.
     * 
     * @param spriteId The identifier used as the filename (without extension).
     * @return The loaded Image or null.
     */
    private Image loadFromCacheOrDisk(String spriteId) {
        // 1. Memory Check: Return the image immediately if it's already in the HashMap
        if (cache.containsKey(spriteId)) {
            return cache.get(spriteId);
        }

        // 2. Resource Path Resolution: Constructs the standard path for PNG files
        String resourcePath = "/" + spriteId + ".png";
        InputStream stream = getClass().getResourceAsStream(resourcePath);

        // 3. Manage cases where files are missing
        if (stream == null) {
            System.err.println("Attenzione: Immagine mancante -> " + resourcePath);
            // Cache the "null" result to avoid expensive disk lookups for the same 
            // missing file in subsequent frames.
            cache.put(spriteId, null); 
            return null;
        }

        // 4. Loading & Storage: Create the JavaFX Image and store it for future use
        Image image = new Image(stream);
        cache.put(spriteId, image);
        return image;
    }
}