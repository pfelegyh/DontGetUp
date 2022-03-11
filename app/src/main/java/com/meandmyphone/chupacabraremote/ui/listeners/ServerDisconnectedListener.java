package com.meandmyphone.chupacabraremote.ui.listeners;

import com.meandmyphone.chupacabraremote.network.ServerConnectionInfo;

public interface ServerDisconnectedListener {
    void onServerLost(ServerConnectionInfo serverConnectionInfo);
}
