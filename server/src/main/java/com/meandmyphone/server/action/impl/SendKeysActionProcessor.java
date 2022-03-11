package com.meandmyphone.server.action.impl;

import com.meandmyphone.server.action.api.ActionProcessor;
import com.meandmyphone.server.services.impl.KeyboardService;
import com.meandmyphone.shared.JSONKeys;
import com.meandmyphone.shared.action.Action;
import com.meandmyphone.shared.action.ActionType;
import com.meandmyphone.shared.action.SuccessAction;
import com.meandmyphone.shared.payload.Payload;

import org.json.JSONObject;

import java.util.Collections;
import java.util.List;

public class SendKeysActionProcessor implements ActionProcessor {

    private final KeyboardService keyboardService;

    public SendKeysActionProcessor(KeyboardService keyboardService) {
        this.keyboardService = keyboardService;
    }

    @Override
    public List<Action> process(Payload payload) {
        JSONObject payloadJson = new JSONObject(payload.getContent());
        String keys = payloadJson.getString(JSONKeys.KEYS);
        keyboardService.inputKeys(keys);
        return Collections.singletonList(new SuccessAction(ActionType.SEND_KEYS));
    }
}
