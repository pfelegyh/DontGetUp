package com.meandmyphone.chupacabraremote.ui.listeners;

import com.meandmyphone.chupacabraremote.network.ServerConnectionInfo;

@Deprecated
public class DefaultServerLocatedListener implements ServerLocatedListener {
    private final NewServerLocatedListener newServerLocatedListener;
    private final ServerUpdatedListener serverUpdatedListener;
    private final ServerDisconnectedListener serverDisconnectedListener;

    public DefaultServerLocatedListener(NewServerLocatedListener newServerLocatedListener,
                                        ServerUpdatedListener serverUpdatedListener,
                                        ServerDisconnectedListener serverDisconnectedListener) {

        this.newServerLocatedListener = newServerLocatedListener;
        this.serverUpdatedListener = serverUpdatedListener;
        this.serverDisconnectedListener = serverDisconnectedListener;
    }


    @Override
    public void onNewServerLocated(ServerConnectionInfo serverConnectionInfo) {
        newServerLocatedListener.onNewServerLocated(serverConnectionInfo);
    }

    @Override
    public void onServerLost(ServerConnectionInfo serverConnectionInfo) {
        serverDisconnectedListener.onServerLost(serverConnectionInfo);
    }

    @Override
    public void onServerInformationUpdated(ServerConnectionInfo serverConnectionInfo) {
        serverUpdatedListener.onServerInformationUpdated(serverConnectionInfo);
    }
}
