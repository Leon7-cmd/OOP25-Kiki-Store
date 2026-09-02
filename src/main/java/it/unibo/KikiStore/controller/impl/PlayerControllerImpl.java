package it.unibo.KikiStore.controller.impl;

import java.util.Objects;

import it.unibo.KikiStore.controller.api.InputHandler;
import it.unibo.KikiStore.controller.api.PlayerController;
import it.unibo.KikiStore.model.player.api.Player;

/**
 * Controller translating raw user input into movement intents on the Player model.
 */
public final class PlayerControllerImpl implements PlayerController {

    private final Player player;
    private final InputHandler input;

    /**
     * Constructs a controller binding a Player model to an InputHandler.
     *
     * @param player the player model to control.
     * @param input  the active input provider.
     */
    public PlayerControllerImpl(final Player player, final InputHandler input) {
        this.player = Objects.requireNonNull(player, "Player model cannot be null");
        this.input = Objects.requireNonNull(input, "InputHandler cannot be null");
    }

    /**
     * Updates the player position.
     */
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

        player.move(dx, dy);
    }
}
