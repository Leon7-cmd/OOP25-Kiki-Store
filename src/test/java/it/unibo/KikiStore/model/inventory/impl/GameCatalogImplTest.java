package it.unibo.KikiStore.model.inventory.impl;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link GameCatalogImpl}.
 */
class GameCatalogImplTest {

    private static final String INGREDIENTS_JSON = "textFiles/ingredients.json";
    private static final String POTIONS_JSON = "textFiles/potions.json";

    /**
     * Verifies that ingredients are loaded from the JSON file.
     */
    @Test
    void loadsIngredientsFromJson() {
        final GameCatalogImpl catalog = new GameCatalogImpl(INGREDIENTS_JSON, POTIONS_JSON);

        assertFalse(catalog.getAllIngredients().isEmpty());
    }

    /**
     * Verifies that potions are loaded from the JSON file.
     */
    @Test
    void loadsPotionsFromJson() {
        final GameCatalogImpl catalog = new GameCatalogImpl(INGREDIENTS_JSON, POTIONS_JSON);

        assertFalse(catalog.getAllPotions().isEmpty());
    }
}
