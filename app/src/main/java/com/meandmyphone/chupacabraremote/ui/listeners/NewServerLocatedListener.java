package com.meandmyphone.chupacabraremote.ui.listeners;

import com.meandmyphone.chupacabraremote.network.ServerConnectionInfo;

public interface NewServerLocatedListener {
    void onNewServerLocated(ServerConnectionInfo serverConnectionInfo);
}
