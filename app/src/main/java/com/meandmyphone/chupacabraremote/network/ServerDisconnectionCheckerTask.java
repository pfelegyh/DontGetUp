package com.meandmyphone.chupacabraremote.network;

import com.meandmyphone.chupacabraremote.logger.Log;
import com.meandmyphone.chupacabraremote.providers.impl.ClientContext;
import com.meandmyphone.chupacabraremote.ui.listeners.ServerDisconnectedListener;

import java.util.Locale;

/**
 * Looks for servers which weren't seen for a long period of time, and initiates the process of
 * marking them as disconnected.
 */
public class ServerDisconnectionCheckerTask implements Runnable {

    private static final String TAG = ServerDisconnectionCheckerTask.class.getSimpleName();
    public static final int DISCONNECTION_THRESHOLD = 10;

    private ServerDisconnectedListener serverDisconnectedListener;

    @Override
    public void run() {
        for (ServerConnectionInfo serverConnectionInfo : ClientContext.getInstance().getLocatedServerDetails()) {
            if (!serverConnectionInfo.getHostAddress().equals("127.0.0.1") /* for demo */
                            && serverConnectionInfo.isAlive()
                            && System.currentTimeMillis() - serverConnectionInfo.getLastSeen() > DISCONNECTION_THRESHOLD * 1000) {

                Log.d(TAG, String.format(Locale.ROOT, "Server: %s(%s) seems to be disconnected, notifying listener",
                        serverConnectionInfo.getHostAddress(), serverConnectionInfo.getHostName()));
                serverDisconnectedListener.onServerLost(serverConnectionInfo);

            }
        }
    }

    public void registerServerDisonnectedListener(ServerDisconnectedListener serverDisconnectedListener) {
        this.serverDisconnectedListener = serverDisconnectedListener;
    }
}
