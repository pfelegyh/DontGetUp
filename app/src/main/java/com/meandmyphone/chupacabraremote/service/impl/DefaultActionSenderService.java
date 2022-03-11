package com.meandmyphone.chupacabraremote.service.impl;

import com.meandmyphone.chupacabraremote.network.SendActionTask;
import com.meandmyphone.chupacabraremote.service.api.ActionSenderService;
import com.meandmyphone.shared.ChupacabraRemote;
import com.meandmyphone.shared.action.Action;
import com.meandmyphone.shared.action.DefaultAction;
import com.meandmyphone.shared.payload.Payload;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Can send an action to the connected server application either in a synchronous or asynchronous
 * fashion. The asynchronus method {@link DefaultActionSenderService#sendAction(String, Payload)}
 * does not have a callback at the moment.
 */
public class DefaultActionSenderService implements ActionSenderService {

    private final String host;

    public DefaultActionSenderService(String host) {
        this.host = host;
    }

    @Override
    public void sendAction(String type, Payload payload) {
        sendAction(type, payload, null);
    }

    @Override
    public void sendAction(String type, Payload payload, OnResultReceivedListener listener) {
        Action action = new DefaultAction(type, payload);
        new SendActionTask(ChupacabraRemote.CLIENT_LISTENER_PORT, host, listener).execute(action);
    }

    @Override
    public List<Action> sendActionSync(String type, Payload payload) throws IOException, ExecutionException, InterruptedException {
        Action action = new DefaultAction(type, payload);
        return new SendActionTask(ChupacabraRemote.CLIENT_LISTENER_PORT, host).execute(action).get();
    }

    public interface OnResultReceivedListener {
        void onResultReceived(List<Action> response);
    }


}
