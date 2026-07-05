package it.unibo.KikiStore.view.hud.api;

/**
 * Data Transfer Object containing the values required to render the HUD.
 * 
 * @param currentEnergy kiki's current energy
 * @param maxEnergy kiki's max energy
 * @param coins kiki's coins
 */
public record HUDRenderData(
    int currentEnergy,
    int maxEnergy,
    int coins
) { }
