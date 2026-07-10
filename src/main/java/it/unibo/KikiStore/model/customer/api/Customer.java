package it.unibo.KikiStore.model.customer.api;
import java.util.List;

public interface Customer {
    public String getName();
    public List<String> getPossibleNeeds();
    public List<String> getPossibleIngredients();//string e non List<Ingredient> perche' il customer non ha accesso alla classe Ingredient
    
}
