package it.unibo.KikiStore.controller.api;

import java.util.List;

import it.unibo.KikiStore.model.economy.api.TransactionResults;
//import it.unibo.KikiStore.model.item.api.GameItem;

/**
 * Gestisce le regole di business dello scambio commerciale (acquisto/vendita)
 * tra il giocatore e il negozio, mantenendo la logica separata dalla View.
 * Ogni stand del negozio (pozioni, ingredienti) ha la propria implementazione
 * concreta, specializzata sul proprio tipo.
 * @param <T> il tipo di item gestito dallo stand (Ingredient o Potion)
 */
public interface ShopTradingController <T> { 

    /**
     * @return gli item acquistabili dal catalogo del negozio
     */
    List<T> getBuyableItems();

    /**
     * @return gli item vendibili, ossia quelli attualmente nell'inventario del giocatore
     */
    List<T> getSellableItems();

    /**
     * @param item l'item di cui calcolare il prezzo di acquisto
     * @return il prezzo di acquisto per l'item indicato
     */
    int getBuyPrice(T item);

    /**
     * @param item l'item di cui calcolare il prezzo di vendita
     * @return il prezzo di vendita per l'item indicato
     */
    int getSellPrice(T item);

    /**
     * Tenta di acquistare l'item indicato: verifica fondi disponibili,
     * scala le monete del giocatore e aggiunge l'item all'inventario.
     *
     * @param item l'item da acquistare
     * @return il risultato della transazione
     */
    TransactionResults buy(T item);

    /**
     * Tenta di vendere l'item indicato: verifica che sia posseduto,
     * lo rimuove dall'inventario e accredita le monete al giocatore.
     *
     * @param item l'item da vendere
     * @return il risultato della transazione
     */
    TransactionResults sell(T item);
}