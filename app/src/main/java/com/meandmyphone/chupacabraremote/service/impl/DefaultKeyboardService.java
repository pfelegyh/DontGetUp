package com.meandmyphone.chupacabraremote.service.impl;

import com.meandmyphone.chupacabraremote.service.api.ActionSenderService;
import com.meandmyphone.chupacabraremote.service.api.KeyboardService;
import com.meandmyphone.shared.action.ActionType;
import com.meandmyphone.shared.payload.PayloadFactory;

public class DefaultKeyboardService implements KeyboardService {

    private final PayloadFactory payloadFactory;
    private final ActionSenderService actionSenderService;

    public DefaultKeyboardService(PayloadFactory payloadFactory, ActionSenderService actionSenderService) {
        this.payloadFactory = payloadFactory;
        this.actionSenderService = actionSenderService;
    }

    @Override
    public void inputKeys(String input) {
        actionSenderService.sendAction(ActionType.SEND_KEYS, payloadFactory.createSendKeyAction(input));
    }
}