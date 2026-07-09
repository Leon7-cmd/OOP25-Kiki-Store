package it.unibo.KikiStore.model.order.impl;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import it.unibo.KikiStore.model.order.api.Order;
import it.unibo.KikiStore.model.order.api.OrderBook;

public class OrderBookImpl implements OrderBook {

    private final List<Order> orders;

    public OrderBookImpl(String jsonFile) {
        orders = new ArrayList<>();
        loadFromJson(jsonFile);
    }


    private void loadFromJson(String jsonFile) {

        InputStream stream = getClass()
                .getClassLoader()
                .getResourceAsStream(jsonFile);

        if (stream == null) {
            return;
        }

        InputStreamReader reader = new InputStreamReader(stream);

        JsonArray orderArray = new Gson()
                .fromJson(reader, JsonArray.class);


        for (JsonElement entry : orderArray) {

            JsonObject orderData = entry.getAsJsonObject();

            String customer = orderData
                    .get("customer")
                    .getAsString();

            String need = orderData
                    .get("need")
                    .getAsString();


            orders.add(new OrderImpl(customer, need));
        }
    }


    @Override
    public List<Order> getOrders() {
        return orders;
    }


    @Override
    public void addOrder(Order order) {
        orders.add(order);
    }


    @Override
    public void removeOrder(Order order) {
        orders.remove(order);
    }
}