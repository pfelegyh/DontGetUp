package com.meandmyphone.server.action.impl;

import com.meandmyphone.server.action.api.ActionProcessor;
import com.meandmyphone.server.services.api.ShutdownService;
import com.meandmyphone.shared.JSONKeys;
import com.meandmyphone.shared.action.Action;
import com.meandmyphone.shared.action.ActionType;
import com.meandmyphone.shared.action.SuccessAction;
import com.meandmyphone.shared.payload.Payload;

import org.json.JSONObject;

import java.util.Collections;
import java.util.List;

public class ShutdownActionProcessor implements ActionProcessor {

    private final ShutdownService shutdownService;

    public ShutdownActionProcessor(ShutdownService shutdownService) {
        this.shutdownService = shutdownService;
    }

    @Override
    public List<Action> process(Payload payload) {
        JSONObject payloadJson = new JSONObject(payload.getContent());
        int seconds = payloadJson.getInt(JSONKeys.DELAY_IN_SECONDS);
        shutdownService.shutdownDelayed(seconds);
        return Collections.singletonList(new SuccessAction(ActionType.SHUTDOWN));
    }
}
