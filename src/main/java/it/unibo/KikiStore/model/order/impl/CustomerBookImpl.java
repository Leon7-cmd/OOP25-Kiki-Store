package it.unibo.KikiStore.model.order.impl;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import it.unibo.KikiStore.model.inventory.api.GameCatalog;
import it.unibo.KikiStore.model.inventory.api.Ingredient;
import it.unibo.KikiStore.model.order.api.Customer;
import it.unibo.KikiStore.model.order.api.CustomerBook;

public class CustomerBookImpl implements CustomerBook {

    private final List<Customer> customers;
    private final GameCatalog gameCatalog;

    public CustomerBookImpl(final String jsonFile, final GameCatalog gameCatalog) {
        this.customers = new ArrayList<>();
        this.gameCatalog = gameCatalog;
        loadFromJson(jsonFile);
    }

    private void loadFromJson(final String jsonFile) {
        final InputStream stream = getClass().getClassLoader().getResourceAsStream(jsonFile);
        if (stream == null) {
            return;
        }

        final InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
        final JsonArray customersArray = new Gson().fromJson(reader, JsonArray.class);

        if (customersArray == null) {
            return;
        }

        for (final JsonElement entry : customersArray) {
            final JsonObject customerData = entry.getAsJsonObject();

            // 1. Prendo il nome del cliente
            final String name = customerData.get("name").getAsString();

            // 2. Prendo l'array di NOMI ingredienti (sono stringhe, non oggetti)
            final JsonArray ingredientNames = customerData.getAsJsonArray("possibleIngredients");

            // 3. Traduco ogni nome in un vero Ingredient, cercandolo nel catalogo
            final List<Ingredient> ingredients = new ArrayList<>();
            for (final JsonElement ingredientNameElement : ingredientNames) {
                final String ingredientName = ingredientNameElement.getAsString();
                final Ingredient ingredient = findIngredientByName(ingredientName);
                if (ingredient != null) {
                    ingredients.add(ingredient);
                }
            }

            // 4. Costruisco il Customer e lo aggiungo alla lista
            this.customers.add(new CustomerImpl(name, ingredients));
        }
    }

    /**
     * Cerca un Ingredient nel catalogo confrontando il nome.
     * Restituisce null se non trovato (es. errore di battitura nel JSON).
     */
    private Ingredient findIngredientByName(final String name) {
        for (final Ingredient ingredient : gameCatalog.getAllIngredients()) {
            if (ingredient.getName().equals(name)) {
                return ingredient;
            }
        }
        return null;
    }

    @Override
    public List<Customer> getCustomers() {
        return new ArrayList<>(this.customers);
    }
}
