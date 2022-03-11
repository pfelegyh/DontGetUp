package com.meandmyphone.chupacabraremote.ui.listeners;

import com.meandmyphone.chupacabraremote.properties.Connection;

public interface ConnectionListener {
    void onConnectedToServer(Connection connection);
    void onDisconnected(Connection connection);
}
