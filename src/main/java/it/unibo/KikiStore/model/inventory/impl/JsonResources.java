package it.unibo.KikiStore.model.inventory.impl;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

/**
 * Utility for loading JSON game data from the classpath resources.
 */
final class JsonResources {

    private JsonResources() {
        // utility class, not instantiable
    }

    /**
     * Reads a JSON array from a resource file.
     *
     * @param path the resource path of the JSON file
     * @return the parsed JSON array
     * @throws IllegalArgumentException if the resource does not exist
     * @throws UncheckedIOException if the resource cannot be read
     */
    static JsonArray readArray(final String path) {
        final InputStream stream = JsonResources.class.getClassLoader().getResourceAsStream(path);
        if (stream == null) {
            throw new IllegalArgumentException("Resource not found: " + path);
        }
        try (Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            return new Gson().fromJson(reader, JsonArray.class);
        } catch (final IOException e) {
            throw new UncheckedIOException("Cannot read resource: " + path, e);
        }
    }
}
