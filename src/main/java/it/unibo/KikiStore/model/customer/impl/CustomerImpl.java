package it.unibo.KikiStore.model.customer.impl;
import java.util.List;

import it.unibo.KikiStore.model.customer.api.Customer;

public class CustomerImpl implements Customer{
    private final String name;
    private final List<String> possibleNeeds;
    private final List<String> possibleIngredients;

    public CustomerImpl(
            final String name,
            final List<String> possibleNeeds,
            final List<String> possibleIngredients) {
                this.name = name;
                this.possibleNeeds = possibleNeeds;
                this.possibleIngredients = possibleIngredients;
            }
    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<String> getPossibleNeeds() {
        return possibleNeeds;
    }

    @Override
    public List<String> getPossibleIngredients() {
        return possibleIngredients;
    }
}
