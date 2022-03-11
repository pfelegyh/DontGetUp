package com.meandmyphone.server.tasks;

import com.meandmyphone.server.ServerInfoFactory;
import com.meandmyphone.shared.ChupacabraRemote;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.logging.Logger;

public class HearbeatProviderTask implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(ClientListener.class.getSimpleName());

    private final ServerInfoFactory serverInfoFactory;

    public HearbeatProviderTask(ServerInfoFactory serverInfoFactory) {
        this.serverInfoFactory = serverInfoFactory;
    }

    @Override
    public void run() {
        try (DatagramSocket socket = new DatagramSocket()) {

            InetAddress group = InetAddress.getByName(ChupacabraRemote.MULTICAST_GROUP_ADDRESS);

            String hearBeatMessage = serverInfoFactory.createHearbeatMessage().toString();
            byte[] buf = hearBeatMessage.getBytes();

            DatagramPacket packet = new DatagramPacket(buf, buf.length, group, ChupacabraRemote.MULTICAST_PORT);
            socket.send(packet);

        } catch (IOException e) {
            LOGGER.severe("Heartbeat failure: " + e.getMessage());
        }
    }

    public void stop() {
        // unused
    }
}