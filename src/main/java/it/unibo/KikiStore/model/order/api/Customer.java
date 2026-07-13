package it.unibo.KikiStore.model.order.api;
import java.util.List;

import it.unibo.KikiStore.model.inventory.api.Ingredient;

public interface Customer {
    public String getName();
    /*i needs sono del villaggio scelti dal generatore, qualsiasi npc puo avere qualsiasi need */
    /*@return the list of ingredients the customer can provide */
    public List<Ingredient> getPossibleIngredients();   
}

