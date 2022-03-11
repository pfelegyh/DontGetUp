package com.meandmyphone.chupacabraremote.service.impl;

import com.meandmyphone.chupacabraremote.service.api.ActionSenderService;
import com.meandmyphone.chupacabraremote.service.api.ShutdownService;
import com.meandmyphone.shared.action.ActionType;
import com.meandmyphone.shared.payload.PayloadFactory;

import java.io.IOException;

public class DefaultShutdownService implements ShutdownService {

    private final ActionSenderService actionSenderService;
    private final PayloadFactory payloadFactory;

    public DefaultShutdownService(ActionSenderService actionSenderService, PayloadFactory payloadFactory) {
        this.actionSenderService = actionSenderService;
        this.payloadFactory = payloadFactory;
    }

    @Override
    public void shutdown(int seconds) throws IOException {
        abortShutdown();
        actionSenderService.sendAction(ActionType.SHUTDOWN, payloadFactory.createShutdownDelayedPayload(seconds));
    }

    @Override
    public void abortShutdown() throws IOException {
        actionSenderService.sendAction(ActionType.ABORT_SHUTDOWN, payloadFactory.createAbortShutdownPayload());
        actionSenderService.sendAction(ActionType.CANCEL_VOLUME_INTERPOLATION, payloadFactory.createCancelInterpolationPayload());
    }
}
