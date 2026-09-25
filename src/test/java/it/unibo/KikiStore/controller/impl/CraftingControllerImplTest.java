package it.unibo.KikiStore.controller.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unibo.KikiStore.model.inventory.api.Ingredient;
import it.unibo.KikiStore.model.inventory.impl.IngredientImpl;
import it.unibo.KikiStore.model.inventory.impl.RecipeBookImpl;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link CraftingControllerImpl}.
 */
class CraftingControllerImplTest {

    private static final String RECIPES_JSON = "textFiles/recipes.json";

    private InventoryControllerImpl inventoryController;
    private CraftingControllerImpl craftingController;

    /**
     * Builds a fresh set of controllers before each test, with the
     * ingredients for the Windrunner Potion recipe already in the inventory.
     */
    @BeforeEach
    void setUp() {
        final RecipeBookImpl recipeBook = new RecipeBookImpl(RECIPES_JSON);
        inventoryController = new InventoryControllerImpl();
        final RecipeBookControllerImpl recipeBookController = new RecipeBookControllerImpl(recipeBook,
                inventoryController);
        craftingController = new CraftingControllerImpl(inventoryController, recipeBookController);

        inventoryController.addIngredient("Basil", "sprites/basil", 1, "plant");
        inventoryController.addIngredient("Dandelion", "sprites/dandelion", 1, "flower");
        inventoryController.addIngredient("Sage", "sprites/sage", 1, "plant");
    }

    /**
     * Verifies that canCraft returns true for a known valid combination.
     */
    @Test
    void canCraftReturnsTrueForValidCombination() {
        final List<Ingredient> selected = correctIngredients();

        assertTrue(craftingController.canCraft(selected));
    }

    /**
     * Verifies that canCraft returns false for a combination matching no recipe.
     */
    @Test
    void canCraftReturnsFalseForInvalidCombination() {
        final Ingredient basil = new IngredientImpl("Basil", "sprites/basil", 1, "plant");
        final Ingredient chamomile = new IngredientImpl("Chamomile", "sprites/chamomile", 1, "flower");

        assertFalse(craftingController.canCraft(List.of(basil, chamomile)));
    }

    /**
     * Verifies that crafting a valid combination adds the correct potion
     * to the inventory and consumes the ingredients used.
     */
    @Test
    void craftPotionWithValidCombinationAddsPotionAndConsumesIngredients() {
        craftingController.craftPotion(correctIngredients());

        assertTrue(inventoryController.hasPotion("Windrunner Potion"));
        assertFalse(inventoryController.hasIngredient("Basil"));
    }

    /**
     * Verifies that crafting an invalid combination adds a black (failed)
     * potion instead of a real one.
     */
    @Test
    void craftPotionWithInvalidCombinationAddsBlackPotion() {
        final Ingredient basil = new IngredientImpl("Basil", "sprites/basil", 1, "plant");
        final Ingredient chamomile = new IngredientImpl("Chamomile", "sprites/chamomile", 1, "flower");

        craftingController.craftPotion(List.of(basil, chamomile));

        assertTrue(inventoryController.hasPotion("Failed Potion"));
    }

    /**
     * @return the ingredients matching the Windrunner Potion recipe
     */
    private List<Ingredient> correctIngredients() {
        final Ingredient basil = new IngredientImpl("Basil", "sprites/basil", 1, "plant");
        final Ingredient dandelion = new IngredientImpl("Dandelion", "sprites/dandelion", 1, "flower");
        final Ingredient sage = new IngredientImpl("Sage", "sprites/sage", 1, "plant");
        return List.of(basil, dandelion, sage);
    }

    /**
     * Verifies that crafting consumes only one unit of each required
     * ingredient, not the whole stock.
     */
    @Test
    void craftPotionConsumesOnlyRequiredQuantity() {
        inventoryController.addIngredient("Basil", "sprites/basil", 4, "plant");

        craftingController.craftPotion(correctIngredients());

        assertEquals(4, inventoryController.getIngredientQuantity("Basil"));
    }

    /**
     * Verifies that a crafted potion is added with quantity one.
     */
    @Test
    void craftPotionAddsOnePotion() {
        craftingController.craftPotion(correctIngredients());

        assertEquals(1, inventoryController.getPotionQuantity("Windrunner Potion"));
    }

    /**
     * Verifies that an unlocked recipe is not available once its
     * ingredients have been used up.
     */
    @Test
    void unlockedRecipeIsNotAvailableWithoutIngredients() {
        craftingController.craftPotion(correctIngredients());

        assertTrue(craftingController.getAvailableRecipes().isEmpty());
    }
}
