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

public class ClickMouseActionProcessor implements ActionProcessor {

    private final MouseService mouseService;

    public ClickMouseActionProcessor(MouseService mouseService) {
        this.mouseService = mouseService;
    }

    @Override
    public List<Action> process(Payload payload) {
        JSONObject payloadJson = new JSONObject(payload.getContent());
        int button = payloadJson.getInt(JSONKeys.MOUSE_BUTTON);
        int mouseX = payloadJson.getInt(JSONKeys.MOUSE_X);
        int mouseY = payloadJson.getInt(JSONKeys.MOUSE_Y);
        if (mouseX == -1 && mouseY == -1) {
            Point mousePos = mouseService.getMousePosition();
            mouseX = mousePos.getX();
            mouseY = mousePos.getY();
        }
        mouseService.setMousePosition(
                mouseX,
                mouseY);
        mouseService.clickMouse(button);
        return Collections.singletonList(new SuccessAction(ActionType.CLICK_MOUSE));
    }
}
