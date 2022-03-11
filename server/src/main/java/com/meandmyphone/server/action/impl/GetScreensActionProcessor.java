package com.meandmyphone.server.action.impl;

import com.meandmyphone.server.action.api.ActionProcessor;
import com.meandmyphone.server.services.impl.MouseService;
import com.meandmyphone.server.vo.Screen;
import com.meandmyphone.shared.JSONKeys;
import com.meandmyphone.shared.action.Action;
import com.meandmyphone.shared.action.ActionType;
import com.meandmyphone.shared.action.SuccessAction;
import com.meandmyphone.shared.payload.Payload;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collections;
import java.util.List;

@Deprecated
public class GetScreensActionProcessor implements ActionProcessor {

    private final Screen[] screens;

    public GetScreensActionProcessor(MouseService mouseService) {
        screens = mouseService.getScreens();
    }

    @Override
    public List<Action> process(Payload payload) {
        JSONArray jsonArray = new JSONArray();
        for (Screen screen : screens) {
            JSONObject screenJson = new JSONObject();
            screenJson.put(JSONKeys.SCREEN_INDEX, screen.getIndex());
            screenJson.put(JSONKeys.SCREEN_LEFT, screen.getLeft());
            screenJson.put(JSONKeys.SCREEN_TOP, screen.getTop());
            screenJson.put(JSONKeys.SCREEN_RIGHT, screen.getRight());
            screenJson.put(JSONKeys.SCREEN_BOTTOM, screen.getBottom());
            jsonArray.put(screenJson);
        }

        JSONObject payloadJson = new JSONObject();
        payloadJson.put(JSONKeys.SCREENS, jsonArray);

        return Collections.singletonList(new SuccessAction(ActionType.GET_SCREENS));

    }
}
