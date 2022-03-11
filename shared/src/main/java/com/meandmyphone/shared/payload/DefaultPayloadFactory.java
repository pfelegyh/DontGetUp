package com.meandmyphone.shared.payload;

import com.meandmyphone.shared.JSONKeys;

import org.json.JSONObject;

public class DefaultPayloadFactory implements PayloadFactory {

    @Override
    public Payload createAbortShutdownPayload() {
        return DefaultPayload.EMPTY;
    }

    @Override
    public Payload createShutdownDelayedPayload(long seconds) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(JSONKeys.DELAY_IN_SECONDS, seconds);
        return new DefaultPayload(jsonObject.toString());
    }

    @Override
    public Payload createSetVolumePayload(int volume) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(JSONKeys.VOLUME, volume);
        return new DefaultPayload(jsonObject.toString());
    }

    @Override
    public Payload createInterpolateVolumePayload(int interpolationType, long duration, int startVolume, int endVolume) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(JSONKeys.INTERPOLATE_VOLUME_START_VALUE, startVolume);
        jsonObject.put(JSONKeys.INTERPOLATE_VOLUME_END_VALUE, endVolume);
        jsonObject.put(JSONKeys.INTERPOLATE_VOLUME_INTERPOLATIONTYPE, interpolationType);
        jsonObject.put(JSONKeys.INTERPOLATE_VOLUME_DURATION, duration);
        return new DefaultPayload(jsonObject.toString());
    }

    @Override
    public Payload createCancelInterpolationPayload() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(JSONKeys.CANCEL_INTERPOLATE, true);
        return new DefaultPayload(jsonObject.toString());
    }

    @Override
    public Payload createMouseClickPayload(int screenIndex, int button, int x, int y) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(JSONKeys.SCREEN_INDEX, screenIndex);
        jsonObject.put(JSONKeys.MOUSE_BUTTON, button);
        jsonObject.put(JSONKeys.MOUSE_X, x);
        jsonObject.put(JSONKeys.MOUSE_Y, y);
        return new DefaultPayload(jsonObject.toString());
    }

    @Override
    public Payload createMoveMousePayload(int x, int y) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(JSONKeys.MOUSE_X, x);
        jsonObject.put(JSONKeys.MOUSE_Y, y);
        return new DefaultPayload(jsonObject.toString());
    }

    @Override
    public Payload createMoveMouseByPayload(double vectorX, double vectorY) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(JSONKeys.MOUSE_VECTOR_X, vectorX);
        jsonObject.put(JSONKeys.MOUSE_VECTOR_Y, vectorY);
        return new DefaultPayload(jsonObject.toString());
    }

    @Override
    public Payload createHoldMouseButtonPayload(int button) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(JSONKeys.MOUSE_BUTTON, button);
        return new DefaultPayload(jsonObject.toString());
    }

    @Override
    public Payload createReleaseMouseButtonPayload(int button) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(JSONKeys.MOUSE_BUTTON, button);
        return new DefaultPayload(jsonObject.toString());
    }

    @Override
    public Payload createSendKeyAction(String keys) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(JSONKeys.KEYS, keys);
        return new DefaultPayload(jsonObject.toString());
    }
}
