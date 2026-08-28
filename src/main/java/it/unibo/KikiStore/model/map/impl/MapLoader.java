package it.unibo.KikiStore.model.map.impl;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class dedicated to loading map data.
 * It parses .txt files from the application resources and converts them into 2D integer arrays.
 */
public class MapLoader {

    /**
     * Reads a .txt file from the resources folder and converts it into a matrix.
     * 
     * @param fileName The name of the file to load (e.g., "map.txt").
     * @return A 2D integer array ready for rendering or collision logic.
     */
    public static int[][] loadMap(String fileName) {
        List<int[]> lines = new ArrayList<>();
        String path = "/" + fileName;
        
        // Open the file as an InputStream from the classpath
        try (InputStream is = MapLoader.class.getResourceAsStream(path)) {
            
            if (is == null) {
                System.err.println("Errore: File mappa non trovato -> " + path);
                return createFallbackMap();
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            
            // Read the file line by line
            while ((line = reader.readLine()) != null) {
                // Clean up whitespace and skip empty lines to prevent parsing errors
                line = line.trim();
                if (line.isEmpty()) continue;

                // Split numbers using a comma as the delimiter
                String[] values = line.split(",");
                int[] row = new int[values.length];
                
                for (int i = 0; i < values.length; i++) {
                    row[i] = Integer.parseInt(values[i].trim());
                }
                lines.add(row);
            }
            
        } catch (Exception e) {
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
            {1, 1, 1}
        };
    }
}
