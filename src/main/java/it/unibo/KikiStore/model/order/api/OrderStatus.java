package it.unibo.KikiStore.model.order.api;

public enum OrderStatus {
    PROPOSED,       // Kiki ha proposto la pozione, non ancora craftata
    PENDING_CRAFT,  // mancano ingredienti, in attesa
    READY,          // pozione craftata, in inventario, pronta per la consegna
    DELIVERED       // consegnata, pagata
}
