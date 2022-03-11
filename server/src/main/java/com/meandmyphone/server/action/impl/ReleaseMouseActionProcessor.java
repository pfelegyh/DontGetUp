package com.meandmyphone.server.action.impl;

import com.meandmyphone.server.action.api.ActionProcessor;
import com.meandmyphone.server.services.impl.MouseService;
import com.meandmyphone.shared.JSONKeys;
import com.meandmyphone.shared.action.Action;
import com.meandmyphone.shared.action.ActionType;
import com.meandmyphone.shared.action.SuccessAction;
import com.meandmyphone.shared.payload.Payload;

import org.json.JSONObject;

import java.util.Collections;
import java.util.List;

public class ReleaseMouseActionProcessor implements ActionProcessor {

    private final MouseService mouseService;

    public ReleaseMouseActionProcessor(MouseService mouseService) {
        this.mouseService = mouseService;
    }

    @Override
    public List<Action> process(Payload payload) {
        JSONObject payloadJson = new JSONObject(payload.getContent());
        int button = payloadJson.getInt(JSONKeys.MOUSE_BUTTON);
        mouseService.releaseMouseButton(button);
        return Collections.singletonList(new SuccessAction(ActionType.RELEASE_MOUSE_BUTTON));
    }
}
