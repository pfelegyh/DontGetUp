package com.meandmyphone.chupacabraremote.service.impl;

import com.meandmyphone.chupacabraremote.providers.impl.ClientContext;
import com.meandmyphone.chupacabraremote.service.api.ActionSenderService;
import com.meandmyphone.chupacabraremote.service.api.MouseService;
import com.meandmyphone.shared.action.ActionType;
import com.meandmyphone.shared.payload.PayloadFactory;

public class DefaultMouseService implements MouseService {

    private final ActionSenderService actionSenderService;
    private final PayloadFactory payloadFactory;

    public DefaultMouseService(ActionSenderService actionSenderService, PayloadFactory payloadFactory) {
        this.actionSenderService = actionSenderService;
        this.payloadFactory = payloadFactory;
    }

    @Override
    public void clickCurrentMousePosition(int button) {
        actionSenderService.sendAction(ActionType.CLICK_MOUSE, payloadFactory.createMouseClickPayload(0, button, -1, -1));
    }

    @Override
    public MouseConnection buildMouseConnection() {
        return new MouseConnection(ClientContext.getInstance().getCommunicationService());
    }

    @Override
    public void holdMouseButton(int button) {
        actionSenderService.sendAction(ActionType.HOLD_MOUSE_BUTTON, payloadFactory.createHoldMouseButtonPayload(button));
    }

    @Override
    public void releaseMouseButton(int button) {
        actionSenderService.sendAction(ActionType.RELEASE_MOUSE_BUTTON, payloadFactory.createReleaseMouseButtonPayload(button));
    }
}
