package it.unibo.KikiStore.model.order.api;

public interface Dialogue {
    DialogueLine getCurrentLine();
    boolean hasNext();
    void advance();
    boolean isFinished();
}
