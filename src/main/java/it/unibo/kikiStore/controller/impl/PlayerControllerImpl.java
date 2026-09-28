package it.unibo.kikistore.controller.impl;

import java.util.Objects;

import it.unibo.kikistore.controller.api.InputHandler;
import it.unibo.kikistore.controller.api.PlayerController;
import it.unibo.kikistore.model.map.impl.CollisionHandler;
import it.unibo.kikistore.model.player.api.Player;
import it.unibo.kikistore.model.utility.BoundingBox;

/**
 * Controller translating user input into movement intents on the Player model.
 */
public final class PlayerControllerImpl implements PlayerController {

    private final Player player;
    private final InputHandler input;

    /**
     * Constructs a new PlayerControllerImpl.
     *
     * @param player the player model to control
     * @param input  the input handler for user interactions
     */
    public PlayerControllerImpl(final Player player, final InputHandler input) {
        this.player = Objects.requireNonNull(player, "Player model cannot be null");
        this.input = Objects.requireNonNull(input, "InputHandler cannot be null");
    }

    @Override
    public void update() {
        double dx = 0.0;
        double dy = 0.0;

        if (input.isUp()) {
            dy -= 1.0;
        }
        if (input.isDown()) {
            dy += 1.0;
        }
        if (input.isLeft()) {
            dx -= 1.0;
        }
        if (input.isRight()) {
            dx += 1.0;
        }

        if (dx != 0.0 && dy != 0.0) {
            final double length = Math.hypot(dx, dy);
            dx /= length;
            dy /= length;
        }

        player.move(dx, dy);
    }

    @Override
    public double getX() {
        return player.getX();
    }

    @Override
    public double getY() {
        return player.getY();
    }

    @Override
    public String getState() {
        return player.getState();
    }

    @Override
    public String getDirection() {
        return player.getDirection();
    }

    @Override
    public void setPosition(final double x, final double y) {
        player.setX(x);
        player.setY(y);
    }

    @Override
    public void setCollisionHandler(final CollisionHandler collisionHandler) {
        player.setCollisionHandler(collisionHandler);
    }

    @Override
    public int getMoney() {
        return player.getMoney();
    }

    @Override
    public void addMoney(final int amount) {
        player.addMoney(amount);
    }

    @Override
    public boolean spendMoney(final int amount) {
        return player.spendMoney(amount);
    }

    @Override
    public int getEnergy() {
        return player.getEnergy();
    }

    @Override
    public int maxEnergy() {
        return player.maxEnergy();
    }

    @Override
    public void restoreEnergy(final int amount) {
        player.restoreEnergy(amount);
    }

    @Override
    public void consumeEnergy(final int amount) {
        player.consumeEnergy(amount);
    }

    @Override
    public BoundingBox getHitbox() {
        return player.getHitbox();
    }

}
