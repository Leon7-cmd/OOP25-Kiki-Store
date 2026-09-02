package it.unibo.KikiStore.model.player.impl;

import it.unibo.KikiStore.model.map.impl.CollisionHandler;
import it.unibo.KikiStore.model.player.api.Player;
import javafx.geometry.Rectangle2D;

/**
 * Concrete Model implementation representing the playable character.
 */
public final class PlayerImpl implements Player {

    private static final double SPEED = 3.5;
    private static final int MAX_ENERGY = 5;
    private static final int BASE_MONEY = 40;

    private static final double HITBOX_WIDTH = 32.0;
    private static final double HITBOX_HEIGHT = 32.0;
    private static final double HITBOX_OFFSET_X = 12.0;
    private static final double HITBOX_OFFSET_Y = 44.0;

    private int money;
    private int energy;
    private double x;
    private double y;
    private String direction = "down";
    private String state = "idle";
    private CollisionHandler collisionHandler;

    /**
     * Initializes the player at a specific starting position.
     *
     * @param startX initial horizontal world position.
     * @param startY initial vertical world position.
     */
    public PlayerImpl(final double startX, final double startY) {
        this.x = startX;
        this.y = startY;
        this.money = BASE_MONEY;
        this.energy = MAX_ENERGY;
    }

    /**
     * Injects the collision handler used to validate movement against the map.
     *
     * @param handler the collision detection engine.
     */
    public void setCollisionHandler(final CollisionHandler handler) {
        this.collisionHandler = handler;
    }

    @Override
    public void move(final double dx, final double dy) {
        if (dx == 0 && dy == 0) {
            this.state = "idle";
            return;
        }

        this.state = "walk";

        // Determine orientation based on primary axis
        if (dy < 0) {
            this.direction = "up";
        } else if (dy > 0) {
            this.direction = "down";
        } else if (dx < 0) {
            this.direction = "left";
        } else if (dx > 0) {
            this.direction = "right";
        }

        final double nextX = this.x + (dx * SPEED);
        final double nextY = this.y + (dy * SPEED);

        if (collisionHandler != null) {
            // Horizontal sliding collision check
            final double nextHitboxX = nextX + HITBOX_OFFSET_X;
            final double currentHitboxY = this.y + HITBOX_OFFSET_Y;
            if (collisionHandler.canMove(nextHitboxX, currentHitboxY, HITBOX_WIDTH, HITBOX_HEIGHT)) {
                this.x = nextX;
            }

            // Vertical sliding collision check
            final double currentHitboxX = this.x + HITBOX_OFFSET_X;
            final double nextHitboxY = nextY + HITBOX_OFFSET_Y;
            if (collisionHandler.canMove(currentHitboxX, nextHitboxY, HITBOX_WIDTH, HITBOX_HEIGHT)) {
                this.y = nextY;
            }
        } else {
            this.x = nextX;
            this.y = nextY;
        }
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    @Override
    public void setX(final double newX) {
        this.x = newX;
    }

    @Override
    public void setY(final double newY) {
        this.y = newY;
    }

    @Override
    public String getDirection() {
        return direction;
    }

    @Override
    public String getState() {
        return state;
    }

    @Override
    public int getMoney() {
        return money;
    }

    @Override
    public void addMoney(final int amount) {
        if (amount > 0) {
            this.money += amount;
        }
    }

    @Override
    public boolean spendMoney(final int amount) {
        if (amount > 0 && this.money >= amount) {
            this.money -= amount;
            return true;
        }
        return false;
    }

    @Override
    public int getEnergy() {
        return energy;
    }

    @Override
    public void restoreEnergy(final int amount) {
        if (amount > 0) {
            this.energy = Math.min(MAX_ENERGY, this.energy + amount);
        }
    }

    @Override
    public boolean consumeEnergy(final int amount) {
        if (amount > 0 && this.energy >= amount) {
            this.energy -= amount;
            return true;
        }
        return false;
    }

    @Override
    public Rectangle2D getHitbox() {
        return new Rectangle2D(
            x + HITBOX_OFFSET_X,
            y + HITBOX_OFFSET_Y,
            HITBOX_WIDTH,
            HITBOX_HEIGHT
        );
    }
}
