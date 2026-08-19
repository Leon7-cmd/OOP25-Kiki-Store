package it.unibo.KikiStore.model.order.impl;
import java.util.List;

import it.unibo.KikiStore.model.order.api.Dialogue;
import it.unibo.KikiStore.model.order.api.DialogueLine;

public class DialogueImpl implements Dialogue {
    private final List<DialogueLine> lines;
    private int currentIndex;
    public DialogueImpl(final List<DialogueLine> lines) {
        this.lines = List.copyOf(lines); // immutabile
        this.currentIndex = 0;
    }
    @Override
    public DialogueLine getCurrentLine() {
        return lines.get(currentIndex);//record speaker+text
    }
    @Override
    public boolean hasNext() {
        return currentIndex < lines.size() - 1;
    }
    @Override
    public void advance() {
        if (hasNext()) {
            currentIndex++;
        }
    }
    @Override
    public boolean isFinished() {
        return !hasNext();//opposite of hasNext
    }
}
