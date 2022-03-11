package com.meandmyphone.chupacabraremote.ui.listeners;

import com.meandmyphone.chupacabraremote.network.ServerConnectionInfo;

public interface ServerUpdatedListener {
    void onServerInformationUpdated(ServerConnectionInfo serverConnectionInfo);
}
