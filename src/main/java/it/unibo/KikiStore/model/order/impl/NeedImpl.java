package it.unibo.KikiStore.model.order.impl;
import it.unibo.KikiStore.model.order.api.Need;
import it.unibo.KikiStore.model.order.api.Rarity;

public class NeedImpl implements Need {
    private final String name;
    private final Rarity rarity;
    private final String dialogue;

    public NeedImpl(String name, Rarity rarity, String dialogue) {
        this.name = name;
        this.rarity = rarity;
        this.dialogue = dialogue;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Rarity getRarity() {
        return rarity;
    }

    @Override
    public String getDialogue() {
        return dialogue;
    }
}