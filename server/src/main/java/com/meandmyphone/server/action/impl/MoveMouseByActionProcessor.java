package com.meandmyphone.server.action.impl;

import com.meandmyphone.server.action.api.ActionProcessor;
import com.meandmyphone.server.services.impl.MouseService;
import com.meandmyphone.server.vo.Point;
import com.meandmyphone.shared.JSONKeys;
import com.meandmyphone.shared.action.Action;
import com.meandmyphone.shared.action.ActionType;
import com.meandmyphone.shared.action.SuccessAction;
import com.meandmyphone.shared.payload.Payload;

import org.json.JSONObject;

import java.util.Collections;
import java.util.List;

@Deprecated
class MoveMouseByActionProcessor implements ActionProcessor {

    private MouseService mouseService;

    public MoveMouseByActionProcessor(MouseService mouseService) {
        this.mouseService = mouseService;
    }

    @Override
    public List<Action> process(Payload action) {
        JSONObject payloadJson = new JSONObject(action.getContent());
        double vectorX = payloadJson.getDouble(JSONKeys.MOUSE_VECTOR_X);
        double vectorY = payloadJson.getDouble(JSONKeys.MOUSE_VECTOR_Y);
        Point currentMousePosition = mouseService.getMousePosition();
        int sensitivity = 100;
        mouseService.setMousePosition(currentMousePosition.getX() + (int) (vectorX * sensitivity), currentMousePosition.getY() + (int) (vectorY * sensitivity));
        return Collections.singletonList(new SuccessAction(ActionType.MOVE_MOUSE_BY));
    }
}
