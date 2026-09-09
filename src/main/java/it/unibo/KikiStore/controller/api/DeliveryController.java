package it.unibo.KikiStore.controller.api;

import java.util.Optional;

public interface DeliveryController {

    /**
     * Da chiamare una volta per ogni frame/tick del game loop.
     */
    void update();

    /**
     * @return il nome del proprietario a cui va consegnato il pacco attuale, se c'è.
     */
    Optional<String> getCurrentRecipient();

    /**
     * Tenta la consegna alla casa indicata dal tileId.
     * @return true se la casa era quella giusta (consegna riuscita)
     */
    boolean attemptDelivery(String ownerNameTried);
}
