package it.unibo.KikiStore.model.map.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class dedicated to loading map data.
 * It parses .txt files from the application resources and converts them into 2D integer arrays.
 */
public final class MapLoader {

    private MapLoader() { }

    /**
     * Reads a .txt file from the resources folder and converts it into a matrix.
     * 
     * @param fileName The name of the file to load (e.g., "map.txt").
     * @return A 2D integer array ready for rendering or collision logic.
     */
    public static int[][] loadMap(final String fileName) {
        final List<int[]> lines = new ArrayList<>();
        final String path = "/" + fileName;

        try (InputStream is = MapLoader.class.getResourceAsStream(path)) {
            if (is == null) {
                return createFallbackMap();
            }
            final BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String line = reader.readLine();
            while (line != null) {
                line = line.trim();
                if (line.isEmpty()) { 
                    continue; 
                }
                final String[] values = line.split(",");
                final int[] row = new int[values.length];
                for (int i = 0; i < values.length; i++) {
                    row[i] = Integer.parseInt(values[i].trim());
                }
                lines.add(row);
                line = reader.readLine();
            }

        } catch (final IOException e) {
            return createFallbackMap();
        }

        // Convert the dynamic List of rows into a static 2D array (int[][])
        return lines.toArray(new int[0][]);
    }

    /**
     * Generates a simple 3x3 emergency map.
     * Used as a safeguard if a requested map file is corrupted or missing.
     * 
     * @return A 2D array representing a safe "room" (walls surrounding an empty space).
     */
    private static int[][] createFallbackMap() {
        return new int[][] {
            {1, 1, 1},
            {1, 0, 1},
            {1, 1, 1},
        };
    }
}
