package com.meandmyphone.server.action.api;

import com.meandmyphone.shared.action.Action;
import com.meandmyphone.shared.payload.Payload;

import java.util.List;

/**
 * Consumes a {@link Payload}. Used to execute the actions on the server app, received from the
 * connected client.
 */
public interface ActionProcessor {
    List<Action> process(Payload action);
}