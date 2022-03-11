package com.meandmyphone.server.action.impl;

import com.meandmyphone.server.action.api.ActionProcessor;
import com.meandmyphone.shared.action.Action;
import com.meandmyphone.shared.action.ActionType;
import com.meandmyphone.shared.action.SuccessAction;
import com.meandmyphone.shared.payload.Payload;

import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

class LoggingActionProcessor implements ActionProcessor {

    private static final Logger LOGGER = Logger.getLogger(LoggingActionProcessor.class.getSimpleName());

    public LoggingActionProcessor() {
    }

    public LoggingActionProcessor(String message) {
        LOGGER.severe(message);
    }


    @Override
    public List<Action> process(Payload payload) {
        LOGGER.info(() -> String.format("Received action with payload=%s", payload.getContent()));
        return Collections.singletonList(new SuccessAction(ActionType.LOGGING));
    }
}