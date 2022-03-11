package com.meandmyphone.server.action.impl;

import com.meandmyphone.server.action.api.ActionProcessor;
import com.meandmyphone.server.services.impl.VolumeService;
import com.meandmyphone.shared.JSONKeys;
import com.meandmyphone.shared.action.Action;
import com.meandmyphone.shared.action.ActionType;
import com.meandmyphone.shared.action.SuccessAction;
import com.meandmyphone.shared.payload.Payload;

import org.json.JSONObject;

import java.util.Collections;
import java.util.List;

public class SetVolumeActionProcessor implements ActionProcessor {

    private final VolumeService volumeService;

    public SetVolumeActionProcessor(VolumeService volumeService) {
        this.volumeService = volumeService;
    }

    @Override
    public List<Action> process(Payload payload) {
        JSONObject payloadJson = new JSONObject(payload.getContent());
        int volume = payloadJson.getInt(JSONKeys.VOLUME);
        volumeService.setVolume(volume);
        return Collections.singletonList(new SuccessAction(ActionType.SET_VOLUME));
    }
}
