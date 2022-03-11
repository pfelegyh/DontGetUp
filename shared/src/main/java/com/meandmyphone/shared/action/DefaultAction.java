package com.meandmyphone.shared.action;

import com.meandmyphone.shared.payload.Payload;

public class DefaultAction implements Action {

    private final String type;
    private final Payload payload;

    public DefaultAction(String type, Payload payload) {
        this.type = type;
        this.payload = payload;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public Payload getPayload() {
        return payload;
    }
}
