package com.meandmyphone.chupacabraremote.network;

import android.os.AsyncTask;

import com.meandmyphone.chupacabraremote.logger.Log;
import com.meandmyphone.chupacabraremote.providers.impl.ClientContext;
import com.meandmyphone.chupacabraremote.service.impl.DefaultActionSenderService;
import com.meandmyphone.shared.action.Action;
import com.meandmyphone.shared.action.ActionType;
import com.meandmyphone.shared.action.FailureAction;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A task that sends an action to be executed to the server application.
 */
public class SendActionTask extends AsyncTask<Action, Void, List<Action>> {

    private static final String TAG = SendActionTask.class.getSimpleName();

    private final int port;
    private final String host;
    private final DefaultActionSenderService.OnResultReceivedListener listener;

    public SendActionTask(int port, String host) {
        this(port, host, null);
    }

    public SendActionTask(int port, String host, DefaultActionSenderService.OnResultReceivedListener listener) {
        this.port = port;
        this.host = host;
        this.listener = listener;
    }

    @Override
    protected List<Action> doInBackground(Action... actions) {
        List<Action> response = new ArrayList<>();

        try {
            for (Action action : actions) {
                response.addAll(postAction(action));
            }
        } catch (IOException ioe) {
            response.add(new FailureAction(ActionType.ERROR, ioe.getMessage()));
        }

        return response;
    }

    @Override
    protected void onPostExecute(List<Action> actions) {
        super.onPostExecute(actions);
        if (listener != null) {
            listener.onResultReceived(actions);
        }

    }

    private List<Action> postAction(Action action) throws IOException {
        try (Socket sock = ClientContext.getInstance().getCommunicationService().createConnection(host, port)) {

            ObjectOutputStream out = new ObjectOutputStream(sock.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(sock.getInputStream());

            out.writeObject(action);
            try {
                List<Action> response = new ArrayList<>();

                Action responseAction;
                while ((responseAction = (Action) in.readObject()) != null) {
                    Log.d("Action", "Received response: " + responseAction);
                    response.add(responseAction);
                }
                return response;
            } catch (ClassNotFoundException | ClassCastException e) {
                return Collections.singletonList(new FailureAction(ActionType.ERROR, e.getMessage()));
            }
        }
    }
}