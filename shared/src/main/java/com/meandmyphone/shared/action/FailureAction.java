package com.meandmyphone.shared.action;

import com.meandmyphone.shared.JSONKeys;
import com.meandmyphone.shared.payload.DefaultPayload;
import com.meandmyphone.shared.payload.Payload;

import org.json.JSONObject;

import java.util.Locale;

public class FailureAction implements Action {

    private final String actionType;
    private final String message;

    public FailureAction(String actionType, String message) {
        this.actionType = actionType;
        this.message = message;
    }

    @Override
    public String getType() {
        return ActionType.ERROR;
    }

    @Override
    public Payload getPayload() {
        JSONObject json = new JSONObject();
        json.put(JSONKeys.SUCCESS, false);
        json.put(JSONKeys.DESCRIPTION, String.format(Locale.ROOT, "Failed to process %s action: %s", actionType, message));
        return new DefaultPayload(json.toString());
    }
}
