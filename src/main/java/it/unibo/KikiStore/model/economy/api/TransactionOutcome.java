package it.unibo.KikiStore.model.economy.api;

/**
 * Esito immutabile di un'operazione di acquisto o vendita.
 * Usato dal Controller per comunicare l'esito alla View senza che
 * quest'ultima debba conoscere le regole di business che lo determinano.
 */
public enum TransactionOutcome {
    SUCCESS,
    INSUFFICIENT_FUNDS,
    ITEM_NOT_AVAILABLE,
    ITEM_NOT_OWNED
}
