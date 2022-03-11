package com.meandmyphone.server.tasks;

import com.meandmyphone.server.action.api.ActionProcessorFactory;
import com.meandmyphone.server.services.impl.MouseService;
import com.meandmyphone.shared.ChupacabraRemote;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

public class ClientListener implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(ClientListener.class.getSimpleName());

    private final AtomicBoolean running;
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final ActionProcessorFactory actionProcessorFactory;
    private final MouseService mouseService;
    private ServerSocket serverSocketRef;

    public ClientListener(AtomicBoolean running, ActionProcessorFactory actionProcessorFactory, MouseService mouseService) {
        this.running = running;
        this.actionProcessorFactory = actionProcessorFactory;
        this.mouseService = mouseService;
    }

    @Override
    public void run() {
        LOGGER.info("ClientListener started");
        try (ServerSocket serverSocket = new ServerSocket(ChupacabraRemote.CLIENT_LISTENER_PORT)) {

            this.serverSocketRef = serverSocket;

            while (!Thread.currentThread().isInterrupted() && running.get()) {
                Socket socket = serverSocket.accept();
                executorService.submit(
                        new ProcessorTask(socket, actionProcessorFactory, mouseService));
            }
            LOGGER.info("ClientListener terminated.");
        } catch (IOException e) {
            LOGGER.severe("Failed to start client listener service: " + e.getMessage());
        }
    }

    public void stop() {
        if (serverSocketRef != null) {
            try {
                serverSocketRef.close();
            } catch (IOException e) {
                LOGGER.severe(e.getMessage());
            }
        }
    }
}
