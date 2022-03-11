package com.meandmyphone.chupacabraremote.service.impl;

import com.meandmyphone.chupacabraremote.network.ServerDisconnectionCheckerTask;
import com.meandmyphone.chupacabraremote.service.api.CommunicationService;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class DefaultCommunicationService implements CommunicationService {

    @Override
    public Socket createConnection(String host, int port) throws IOException {
        Socket sock = new Socket();
        sock.setSoTimeout(ServerDisconnectionCheckerTask.DISCONNECTION_THRESHOLD * 1000);
        sock.connect(new InetSocketAddress(host, port), ServerDisconnectionCheckerTask.DISCONNECTION_THRESHOLD * 1000);
        return sock;
    }
}
