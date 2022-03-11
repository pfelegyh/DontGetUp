package com.meandmyphone.chupacabraremote.service.api;

import com.meandmyphone.chupacabraremote.service.impl.DefaultActionSenderService;
import com.meandmyphone.shared.action.Action;
import com.meandmyphone.shared.payload.Payload;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * A service that can send an action-to-be-executed to the server application.
 */
public interface ActionSenderService {

    /**
     * Example of a Json representation of an action
     *
     * {
     *     "type" : "shutdownAction",
     *     "payload" : {
     *         "seconds" : 300
     *     }
     * }
     *
     *      * {
     *     "type" : "abortShutdownAction",
     *     "payload" : {
     *     }
     * }
     *
     * @param type type of the Action, types defined in: {@link com.meandmyphone.shared.action.ActionType}
     * @param payload the actual contents of the action
     */
    void sendAction(String type, Payload payload, DefaultActionSenderService.OnResultReceivedListener listener);

    void sendAction(String type, Payload payload);

    List<Action> sendActionSync(String type, Payload payload) throws IOException, ExecutionException, InterruptedException;
}
