package it.unibo.KikiStore.model.order.api;

public enum Rarity {
    COMMON(5),
    UNCOMMON(3),
    RARE(1);

    private final int weight;

    Rarity(final int weight) {
        this.weight = weight;
    }

    public int getWeight() {
        return weight;
    }
}
