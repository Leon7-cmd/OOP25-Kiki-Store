package it.unibo.KikiStore.model.order.impl;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it.unibo.KikiStore.model.inventory.api.Ingredient;
import it.unibo.KikiStore.model.order.api.Customer;

public class CustomerImpl implements Customer{
    private final String name;
    private final List<Ingredient> possibleIngredients;

    public CustomerImpl(
            final String name, final List<Ingredient> possibleIngredients) {
                this.name = name;
                this.possibleIngredients = new ArrayList<>(possibleIngredients);
            }
    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<Ingredient> getPossibleIngredients() {
        return Collections.unmodifiableList(possibleIngredients);
    }
}
