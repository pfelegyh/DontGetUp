package com.meandmyphone.chupacabraremote.service.api;

import java.io.IOException;
import java.net.Socket;

/**
 * Creates a socket from a hostname-port pair
 */
public interface CommunicationService {

    Socket createConnection(String host, int port) throws IOException;

}
