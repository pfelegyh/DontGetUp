package com.meandmyphone.chupacabraremote.service.impl;

import com.meandmyphone.chupacabraremote.service.api.ActionSenderService;
import com.meandmyphone.chupacabraremote.service.api.VolumeService;
import com.meandmyphone.shared.action.ActionType;
import com.meandmyphone.shared.payload.PayloadFactory;

import java.io.IOException;

public class DefaultVolumeService implements VolumeService {

    private static final int VOLUME_CHANGING_THRESHOLD = 3000;

    private final ActionSenderService actionSenderService;
    private final PayloadFactory payloadFactory;
    private int currentVolume = 0;
    private long lastSendTime = 0;

    public DefaultVolumeService(ActionSenderService actionSenderService, PayloadFactory payloadFactory) {
        this.actionSenderService = actionSenderService;
        this.payloadFactory = payloadFactory;
    }

    @Override
    public boolean isVolumeChanging() {
        return System.currentTimeMillis() - lastSendTime < VOLUME_CHANGING_THRESHOLD;
    }

    @Override
    public void startVolumeChange(int volume) {
        sendVolumeChangeAction(volume);
    }

    @Override
    public void changeVolume(int volume) {
        long elapsedTime = lastSendTime - System.currentTimeMillis();
        if (Math.abs(volume - currentVolume) > 655 || elapsedTime > 1000) {
            sendVolumeChangeAction(volume);
        }
    }

    @Override
    public void finishVolumeChange(int volume) {
        sendVolumeChangeAction(volume);
    }

    @Override
    public void interpolateVolumeToZero(int interpolationType, long duration, int volume) throws IOException {
        actionSenderService.sendAction(ActionType.INTERPOLATE_VOLUME, payloadFactory.createInterpolateVolumePayload(interpolationType, duration, volume, 0));
    }

    private void sendVolumeChangeAction(int volume) {
        currentVolume = volume;
        lastSendTime = System.currentTimeMillis();
        actionSenderService.sendAction(ActionType.SET_VOLUME, payloadFactory.createSetVolumePayload(currentVolume));
    }
}