package it.unibo.KikiStore.controller.impl;

import it.unibo.KikiStore.controller.api.OrderSpawner;
import it.unibo.KikiStore.model.order.api.OrderBook;
import it.unibo.KikiStore.model.order.api.OrderGenerator;

public class OrderSpawnerImpl implements OrderSpawner {

    private final OrderGenerator orderGenerator;
    private final OrderBook orderBook;
    private final int spawnIntervalFrames;
    private final int maxPendingOrders;
    private final int resetThreshold;

    private int frameCount;

    public OrderSpawnerImpl(final OrderGenerator orderGenerator, final OrderBook orderBook,
            final int spawnIntervalFrames, final int maxPendingOrders, final int resetThreshold) {
        this.orderGenerator = orderGenerator;
        this.orderBook = orderBook;
        this.spawnIntervalFrames = spawnIntervalFrames;
        this.maxPendingOrders = maxPendingOrders;
        this.resetThreshold = resetThreshold;
        this.frameCount = 0;
    }

    /**
     * Da chiamare una volta per ogni frame/tick del game loop.
     */
    @Override
    public void update() {
        frameCount++;

        if (orderBook.getOrders().size() < maxPendingOrders && frameCount % spawnIntervalFrames == 0) {
            orderBook.addOrder(orderGenerator.generateOrder());
        }

        if (frameCount >= resetThreshold) {
            frameCount = 0;
        }
    }
}
