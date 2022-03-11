package com.meandmyphone.server.action.impl;

import com.meandmyphone.server.action.api.ActionProcessor;
import com.meandmyphone.server.services.api.ShutdownService;
import com.meandmyphone.shared.action.Action;
import com.meandmyphone.shared.action.ActionType;
import com.meandmyphone.shared.action.SuccessAction;
import com.meandmyphone.shared.payload.Payload;

import java.util.Collections;
import java.util.List;

public class AbortShutdownActionProcessor implements ActionProcessor {

    private final ShutdownService shutdownService;

    public AbortShutdownActionProcessor(ShutdownService shutdownService) {
        this.shutdownService = shutdownService;
    }

    @Override
    public List<Action> process(Payload payload) {
        shutdownService.abortDelayedShutdown();
        return Collections.singletonList(new SuccessAction(ActionType.ABORT_SHUTDOWN));
    }
}
