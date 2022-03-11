package com.meandmyphone.chupacabraremote.service.impl;

import com.meandmyphone.chupacabraremote.logger.Log;
import com.meandmyphone.chupacabraremote.properties.Connection;
import com.meandmyphone.chupacabraremote.providers.impl.ClientContext;
import com.meandmyphone.chupacabraremote.service.api.CommunicationService;
import com.meandmyphone.shared.Vector;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

public class MouseConnection {

    private static final String TAG = MouseConnection.class.getSimpleName();

    private final AtomicBoolean running = new AtomicBoolean(true);
    private final Queue<Vector> vectorQueue = new ConcurrentLinkedQueue<>();
    private ExecutorService executorService;
    private Future<?> vectorConsumerTask;
    private final CommunicationService communicationService;

    public MouseConnection(CommunicationService communicationService) {
        this.communicationService = communicationService;
    }

    public void start() {
        executorService = Executors.newSingleThreadExecutor();
        vectorConsumerTask = executorService.submit(new VectorConsumer());
    }

    public void addVector(Vector vector) {
        vectorQueue.add(vector);
    }

    public void stop() {
        running.set(false);
        vectorConsumerTask.cancel(true);
        executorService.shutdown();
    }

    private class VectorConsumer implements Runnable {

        @Override
        public void run() {
            Connection connection = ClientContext.getInstance().getConnection().getValue();
            try (Socket socket = communicationService.createConnection(connection.getHost(), connection.getPort())) {
                ObjectOutputStream stream = new ObjectOutputStream(socket.getOutputStream());
                while (running.get()) {
                    if (!vectorQueue.isEmpty()) {
                        stream.writeObject(vectorQueue.remove());
                    }
                }
            } catch (IOException e) {
                running.set(false);
                Log.e(TAG, "Failed to open stream: " + e.getMessage());
            }
        }
    }
}
