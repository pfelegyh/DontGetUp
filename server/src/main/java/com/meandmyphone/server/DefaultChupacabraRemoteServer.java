package com.meandmyphone.server;

import com.meandmyphone.server.action.api.ActionProcessorFactory;
import com.meandmyphone.server.action.impl.DefaultActionProcessorFactory;
import com.meandmyphone.server.services.api.ProcessService;
import com.meandmyphone.server.services.api.ShutdownService;
import com.meandmyphone.server.services.impl.DefaultProcessService;
import com.meandmyphone.server.services.impl.DefaultShutdownService;
import com.meandmyphone.server.services.impl.KeyboardService;
import com.meandmyphone.server.services.impl.MouseService;
import com.meandmyphone.server.services.impl.VolumeService;
import com.meandmyphone.server.tasks.ClientListener;
import com.meandmyphone.server.tasks.HearbeatProviderTask;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.logging.Logger;

/**
 * Loads the native DLLs, initializes the high level services used by the server application, and
 * executes the necessary tasks to maintain connection with the clients. The server broadcasts a
 * "heartbeat" on regular intervals, based on which the clients can locate it, and build a
 * connection. It is also responsible for maintaining the task with the
 * {@link java.net.ServerSocket} listening for {@link com.meandmyphone.shared.action.Action}s
 * received from the client
 */
public class DefaultChupacabraRemoteServer {

    static {
        System.loadLibrary("servercpp");
    }

    private static final Logger LOGGER = Logger.getLogger(DefaultChupacabraRemoteServer.class.getSimpleName());

    private final AtomicBoolean running = new AtomicBoolean(false);

    private Consumer<String> notificationReceiver;
    private final ExecutorService clientListenerExecutor = Executors.newSingleThreadExecutor();
    private final ScheduledExecutorService heartbeatExecutor = Executors.newSingleThreadScheduledExecutor();

    private Future<?> clientListenerTaskHandle;
    private Future<?> heartBeatTaskHandle;

    private ClientListener clientListener;
    private HearbeatProviderTask hearbeatProviderTask;

    public void start() throws IOException {

        LOGGER.info("Starting server...");

        ProcessService processService = new DefaultProcessService();
        VolumeService volumeService = new VolumeService();
        ShutdownService shutdownService = new DefaultShutdownService(processService);
        MouseService mouseService = new MouseService();
        KeyboardService keyboardService = new KeyboardService();

        ActionProcessorFactory actionProcessorFactory = new DefaultActionProcessorFactory(
                shutdownService, volumeService, mouseService, keyboardService,
                this::sendNotificationToReceiver);

        ServerInfoFactory serverInfoFactory = new ServerInfoFactory(volumeService, shutdownService, System.currentTimeMillis());

        clientListener = new ClientListener(running, actionProcessorFactory, mouseService);
        hearbeatProviderTask = new HearbeatProviderTask(serverInfoFactory);

        clientListenerTaskHandle = clientListenerExecutor.submit(clientListener);
        heartBeatTaskHandle = heartbeatExecutor.scheduleAtFixedRate(hearbeatProviderTask, 0, 1, TimeUnit.SECONDS);

        running.set(true);
        LOGGER.info("Server started!");
    }

    public void stop() {
        running.set(false);
        clientListener.stop();
        hearbeatProviderTask.stop();
        clientListenerTaskHandle.cancel(true);
        heartBeatTaskHandle.cancel(true);
    }

    public boolean isRunning() {
        return running.get();
    }

    public void registerNotificationReceiver(Consumer<String> notificationReceiver) {
        this.notificationReceiver = notificationReceiver;
    }

    private void sendNotificationToReceiver(String message) {
        if (notificationReceiver != null) {
            notificationReceiver.accept(message);
        }
    }
}
