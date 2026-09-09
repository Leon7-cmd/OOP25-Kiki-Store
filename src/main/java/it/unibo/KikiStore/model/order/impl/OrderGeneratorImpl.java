package it.unibo.KikiStore.model.order.impl;

import java.util.List;
import java.util.Random;

import it.unibo.KikiStore.model.inventory.api.Ingredient;
import it.unibo.KikiStore.model.order.api.Customer;
import it.unibo.KikiStore.model.order.api.CustomerBook;
import it.unibo.KikiStore.model.order.api.CustomerRequest;
import it.unibo.KikiStore.model.order.api.Need;
import it.unibo.KikiStore.model.order.api.NeedGenerator;
import it.unibo.KikiStore.model.order.api.Order;
import it.unibo.KikiStore.model.order.api.OrderGenerator;

/**
 * Generatore di ordini casuali.
 * Crea richieste per i clienti scegliendo tra singoli ingredienti o bisogni (pozioni).
 */
public final class OrderGeneratorImpl implements OrderGenerator {

    private final CustomerBook customerBook;
    private final NeedGenerator needGenerator;
    private final Random random;

    /**
     * @param customerBook  il registro dei clienti disponibili
     * @param needGenerator il generatore per i bisogni dei clienti
     */
    public OrderGeneratorImpl(final CustomerBook customerBook, final NeedGenerator needGenerator) {
        this.customerBook = customerBook;
        this.needGenerator = needGenerator;
        this.random = new Random();
    }

    @Override
    public Order generateOrder() {
        final List<Customer> customers = customerBook.getCustomers();
        if (customers.isEmpty()) {
            return null;
        }

        final Customer customer = customers.get(random.nextInt(customers.size()));
        final CustomerRequest request = generateRequest(customer);

        if (request == null) {
            return null;
        }

        return new OrderImpl(customer, request);
    }

    private CustomerRequest generateRequest(final Customer customer) {
        final List<Ingredient> possibleIngredients = customer.getPossibleIngredients();
        final boolean wantsIngredientRequest = random.nextBoolean();

        // 50% di probabilità di richiedere un ingrediente (se il cliente ne ha nella sua lista)
        if (wantsIngredientRequest && !possibleIngredients.isEmpty()) {
            final Ingredient chosen = possibleIngredients.get(random.nextInt(possibleIngredients.size()));
            return new IngredientRequest(chosen);
        }

        // Altrimenti genera un bisogno per una pozione
        final Need need = needGenerator.generateNeed();
        return new NeedRequest(need);
    }
}