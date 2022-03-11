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

public class InterpolateVolumeActionProcessor implements ActionProcessor {

    private final VolumeService volumeService;

    public InterpolateVolumeActionProcessor(VolumeService volumeService) {
        this.volumeService = volumeService;
    }

    @Override
    public List<Action> process(Payload payload) {

        volumeService.stopInterpolatingVolume();

        JSONObject payloadJson = new JSONObject(payload.getContent());

        if (payloadJson.optBoolean(JSONKeys.CANCEL_INTERPOLATE, false)) {
            return Collections.singletonList(new SuccessAction(ActionType.CANCEL_VOLUME_INTERPOLATION));
        }

        final int startValue = payloadJson.getInt(JSONKeys.INTERPOLATE_VOLUME_START_VALUE);
        final int endValue = payloadJson.getInt(JSONKeys.INTERPOLATE_VOLUME_END_VALUE);
        final int interpolationType = payloadJson.getInt(JSONKeys.INTERPOLATE_VOLUME_INTERPOLATIONTYPE);
        final int change = endValue - startValue;
        final long duration = payloadJson.getLong(JSONKeys.INTERPOLATE_VOLUME_DURATION);
        final long startTime = System.currentTimeMillis();

        volumeService.startInterpolatingVolume(
                startValue,
                endValue,
                interpolationType,
                change,
                duration,
                startTime,
                volumeService::stopInterpolatingVolume);

        return Collections.singletonList(new SuccessAction(ActionType.INTERPOLATE_VOLUME));
    }
}
