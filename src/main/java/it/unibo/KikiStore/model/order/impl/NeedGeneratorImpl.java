package it.unibo.KikiStore.model.order.impl;

import java.util.List;
import java.util.Random;

import it.unibo.KikiStore.model.order.api.Need;
import it.unibo.KikiStore.model.order.api.NeedBook;
import it.unibo.KikiStore.model.order.api.NeedGenerator;

public class NeedGeneratorImpl implements NeedGenerator {

    private final NeedBook needBook;
    private final Random random;

    public NeedGeneratorImpl(final NeedBook needBook) {
        this.needBook = needBook;
        this.random = new Random();
    }

    @Override
    public Need generateNeed() {
        List<Need> needs = needBook.getNeeds();

        int totalWeight = 0;
        for (Need need : needs) {
            totalWeight += need.getRarity().getWeight();
        }

        int r = random.nextInt(totalWeight);
        for (Need need : needs) {
            r -= need.getRarity().getWeight();
            if (r < 0) {
                return need;
            }
        }

        // non dovrebbe mai accadere se totalWeight è calcolato correttamente
        throw new IllegalStateException("No need could be generated");
    }
}