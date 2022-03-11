package com.meandmyphone.server.action.impl;

import com.meandmyphone.server.action.api.ActionProcessor;
import com.meandmyphone.shared.action.Action;
import com.meandmyphone.shared.action.ActionType;
import com.meandmyphone.shared.action.SuccessAction;
import com.meandmyphone.shared.payload.Payload;

import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

public class AcknowledgeActionProcessor implements ActionProcessor {

    private static final Logger LOGGER = Logger.getLogger(AcknowledgeActionProcessor.class.getSimpleName());

    @Override
    public List<Action> process(Payload action) {
        return Collections.singletonList(new SuccessAction(ActionType.ACKNOWLEDGE));

    }
}
