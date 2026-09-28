package it.unibo.kikistore.model.inventory.impl;

import java.util.List;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import it.unibo.kikistore.model.inventory.api.Ingredient;
import it.unibo.kikistore.model.inventory.api.Potion;
import it.unibo.kikistore.model.inventory.api.Recipe;

/**
 * Concrete potion recipe - the ingredients needed and the potion
 * it produces, plus whether the player has discovered it yet.
 */
public final class RecipeImpl implements Recipe {
    private final List<Ingredient> ingredients;
    private final Potion resultingPotion;
    private boolean isUnlocked;

    /**
     * @param ingredients the ingredients required to craft this recipe
     * @param resultingPotion the potion produced by this recipe
     * @param isUnlocked whether the recipe starts already unlocked
     */
    @SuppressFBWarnings(
        value = "EI_EXPOSE_REP2",
        justification = "The recipe keeps a reference to its resulting potion on purpose"
    )
    public RecipeImpl(final List<Ingredient> ingredients, final Potion resultingPotion, final boolean isUnlocked) {
        this.ingredients = List.copyOf(ingredients);
        this.resultingPotion = resultingPotion;
        this.isUnlocked = isUnlocked;
    }

    @Override
    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    @Override
    @SuppressFBWarnings(
        value = "EI_EXPOSE_REP",
        justification = "The recipe returns its reference potion, which is only read"
    )
    public Potion getPotion() {
        return resultingPotion;
    }

    @Override
    public boolean isUnlocked() {
        return isUnlocked;
    }

    @Override
    public void setUnlocked() {
        isUnlocked = true;
    }
}
