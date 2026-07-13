package it.unibo.KikiStore.model.order.impl;


import it.unibo.KikiStore.model.order.api.NeedBook;
import it.unibo.KikiStore.model.order.api.Need;
import it.unibo.KikiStore.model.order.api.Rarity;
import java.util.List;
import java.util.ArrayList;
import java.io.InputStream;
import java.io.InputStreamReader;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class NeedBookImpl implements NeedBook {

    private final List<Need> allNeeds;

    public NeedBookImpl(String jsonFile) {
        allNeeds = new ArrayList<>();
        loadFromJson(jsonFile);
    }

    private void loadFromJson(String jsonFile) {
        InputStream stream = getClass().getClassLoader().getResourceAsStream(jsonFile);
        if (stream == null) {
            return;
        }
        InputStreamReader reader = new InputStreamReader(stream);
        JsonArray needs = new Gson().fromJson(reader, JsonArray.class);

        for (JsonElement entry : needs) {
            JsonObject needData = entry.getAsJsonObject();
            String name = needData.get("name").getAsString();
            Rarity rarity = Rarity.valueOf(needData.get("rarity").getAsString());
            String dialogue = needData.get("dialogue").getAsString();
            allNeeds.add(new NeedImpl(name, rarity, dialogue));
        }
    }

    @Override
    public List<Need> getNeeds() {
        return allNeeds;
    }
}