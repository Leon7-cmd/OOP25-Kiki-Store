package it.unibo.KikiStore.view.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to map every sprite in the game using txt files.
 */
public final class TileRegistry {
    private static final List<String> TILE_LIST = new ArrayList<>();
    private static final String DEFAULT_SPRITE = "unknown";

    private TileRegistry() {

    }

    static {
        loadMappings("sprites/tileMappings.txt");
    }

    private static void loadMappings(final String filePath) {
        try (InputStream is = TileRegistry.class.getClassLoader().getResourceAsStream(filePath)) {
            if (is == null) {
                return;
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                String line = reader.readLine();
                while (line != null) {
                    line = line.trim();
                    if (line.isEmpty()) {
                        continue;
                    }
                    TILE_LIST.add(line);
                    line = reader.readLine();
                }
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Helper method to associate numeric IDs with their corresponding asset paths.
     * 
     * @param tileId The ID from the map matrix.
     * @return The key used by SpriteManager to locate the texture.
     */
    public static String getSpriteNameFromId(final int tileId) {
        if (tileId < 0 || tileId >= TILE_LIST.size()) {
            return DEFAULT_SPRITE;
        }
        return TILE_LIST.get(tileId);
    }
}
