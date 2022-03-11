package com.meandmyphone.shared.action;

import com.meandmyphone.shared.JSONKeys;
import com.meandmyphone.shared.payload.DefaultPayload;
import com.meandmyphone.shared.payload.Payload;

import org.json.JSONObject;

import java.util.Locale;

public class SuccessAction implements Action {

    private final String actionType;

    public SuccessAction(String actionType) {
        this.actionType = actionType;
    }

    @Override
    public String getType() {
        return ActionType.ACKNOWLEDGE;
    }

    @Override
    public Payload getPayload() {
        JSONObject json = new JSONObject();
        json.put(JSONKeys.SUCCESS, true);
        json.put(JSONKeys.DESCRIPTION, String.format(Locale.ROOT, "Successfully processed %s action.", actionType));
        return new DefaultPayload(json.toString());
    }
}
