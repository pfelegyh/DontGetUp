package com.meandmyphone.server.action.api;

/**
 * Factory to create various {@link ActionProcessor}s based on the
 * {@link com.meandmyphone.shared.action.ActionType} of the
 * {@link com.meandmyphone.shared.action.Action}
 */
public interface ActionProcessorFactory {
    ActionProcessor createActionProcessor(String type);
}
