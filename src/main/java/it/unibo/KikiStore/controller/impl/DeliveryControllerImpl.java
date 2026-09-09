package it.unibo.KikiStore.controller.impl;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import it.unibo.KikiStore.controller.api.DeliveryController;
import it.unibo.KikiStore.model.house.api.HouseBook;
import it.unibo.KikiStore.model.player.impl.PlayerImpl;

public class DeliveryControllerImpl implements DeliveryController {

    private static final int REWARD_COINS = 10;

    private final HouseBook houseBook;
    private final PlayerImpl player;
    private final int spawnIntervalFrames;

    private final Random random = new Random();
    private int frameCount;
    private Optional<String> currentRecipient = Optional.empty();

    public DeliveryControllerImpl(final HouseBook houseBook, final PlayerImpl player,
            final int spawnIntervalFrames) {
        this.houseBook = houseBook;
        this.player = player;
        this.spawnIntervalFrames = spawnIntervalFrames;
        this.frameCount = 0;
    }

    @Override
    public void update() {
        if (currentRecipient.isEmpty()) {
            frameCount++;
            if (frameCount >= spawnIntervalFrames) {
                currentRecipient = Optional.of(pickRandomOwnerName());
                frameCount = 0;
            }
        }
    }

    @Override
    public Optional<String> getCurrentRecipient() {
        return currentRecipient;
    }

    @Override
    public boolean attemptDelivery(final String ownerNameTried) {
        final boolean correct = currentRecipient.isPresent()
                && currentRecipient.get().equals(ownerNameTried);
        if (correct) {
            player.setMoney(player.getMoney() + REWARD_COINS);
            currentRecipient = Optional.empty();
        }
        return correct;
    }

    private String pickRandomOwnerName() {
        final List<String> names = houseBook.getAllOwnerNames();
        return names.get(random.nextInt(names.size()));
    }
}