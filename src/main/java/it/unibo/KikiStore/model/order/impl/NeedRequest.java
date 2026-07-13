package it.unibo.KikiStore.model.order.impl;

import it.unibo.KikiStore.model.order.api.CustomerRequest;
import it.unibo.KikiStore.model.order.api.Need;

public class NeedRequest implements CustomerRequest {

    private final Need need;

    public NeedRequest(final Need need) {
        this.need = need;
    }

    public Need getNeed() {
        return need;
    }

    @Override
    public String getDialogue() {
        return need.getDialogue();
    }
}
