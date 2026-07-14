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

public class OrderGeneratorImpl implements OrderGenerator {

    private final CustomerBook customerBook;
    private final NeedGenerator needGenerator;
    private final Random random;

    public OrderGeneratorImpl(final CustomerBook customerBook, final NeedGenerator needGenerator) {
        this.customerBook = customerBook;
        this.needGenerator = needGenerator;
        this.random = new Random();
    }

    @Override
    public Order generateOrder() {
        List<Customer> customers = customerBook.getCustomers();
        Customer customer = customers.get(random.nextInt(customers.size()));

        CustomerRequest request = generateRequest(customer);

        return new OrderImpl(customer, request);
    }

    private CustomerRequest generateRequest(Customer customer) {
        List<Ingredient> possibleIngredients = customer.getPossibleIngredients();
        boolean wantsIngredientRequest = random.nextBoolean();

        if (wantsIngredientRequest && !possibleIngredients.isEmpty()) {
            Ingredient chosen = possibleIngredients.get(random.nextInt(possibleIngredients.size()));
            return new IngredientRequest(chosen);
        }

        Need need = needGenerator.generateNeed();
        return new NeedRequest(need);
    }
}
