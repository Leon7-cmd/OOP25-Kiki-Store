package it.unibo.KikiStore.model.economy.api;

/**
 * Rappresenta il risultato di una singola transazione (acquisto o vendita),
 * comprensivo dell'esito e del prezzo effettivamente applicato.
 *
 * @param outcome l'esito della transazione
 * @param price il prezzo applicato (di acquisto o di vendita)
 */
public record TransactionResults(TransactionOutcome outcome, int price) {

    /**
     * @return true se la transazione è andata a buon fine
     */
    public boolean isSuccess() {
        return outcome == TransactionOutcome.SUCCESS;
    }
}
