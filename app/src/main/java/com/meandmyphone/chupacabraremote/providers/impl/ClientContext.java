package com.meandmyphone.chupacabraremote.providers.impl;

import com.meandmyphone.chupacabraremote.exceptions.ClientContextNotInitalizedException;
import com.meandmyphone.chupacabraremote.network.ServerConnectionInfo;
import com.meandmyphone.chupacabraremote.properties.Connection;
import com.meandmyphone.chupacabraremote.properties.ObservableList;
import com.meandmyphone.chupacabraremote.properties.ObservableProperty;
import com.meandmyphone.chupacabraremote.providers.api.ConnectedServersProvider;
import com.meandmyphone.chupacabraremote.providers.api.ServicesProvider;
import com.meandmyphone.chupacabraremote.service.api.ActionSenderService;
import com.meandmyphone.chupacabraremote.service.api.CommunicationService;
import com.meandmyphone.chupacabraremote.service.api.KeyboardService;
import com.meandmyphone.chupacabraremote.service.api.MouseService;
import com.meandmyphone.chupacabraremote.service.api.ShutdownService;
import com.meandmyphone.chupacabraremote.service.api.VolumeService;
import com.meandmyphone.chupacabraremote.service.impl.DefaultActionSenderService;
import com.meandmyphone.chupacabraremote.service.impl.DefaultCommunicationService;
import com.meandmyphone.chupacabraremote.service.impl.DefaultConnectionService;
import com.meandmyphone.chupacabraremote.service.impl.DefaultKeyboardService;
import com.meandmyphone.chupacabraremote.service.impl.DefaultMouseService;
import com.meandmyphone.chupacabraremote.service.impl.DefaultShutdownService;
import com.meandmyphone.chupacabraremote.service.impl.DefaultVolumeService;
import com.meandmyphone.chupacabraremote.ui.CountDownTask;
import com.meandmyphone.chupacabraremote.ui.listeners.ConnectionListener;
import com.meandmyphone.shared.ChupacabraRemote;
import com.meandmyphone.shared.InterpolationData;
import com.meandmyphone.shared.payload.DefaultPayloadFactory;
import com.meandmyphone.shared.payload.PayloadFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * A singleton center piece of the client code, holds information regarding available servers, and
 * also providers quick access to internal dependencies.
 */
public class ClientContext implements ServicesProvider, ConnectedServersProvider<Connection, ServerConnectionInfo> {

    private static final ClientContext INSTANCE = new ClientContext();

    private ShutdownService shutdownService;
    private ActionSenderService actionSenderService;
    private CommunicationService communicationService;
    private VolumeService volumeService;
    private PayloadFactory payloadFactory;
    private MouseService mouseService;
    private DefaultConnectionService connectionService;
    private KeyboardService keyboardService;

    private final ObservableList<ServerConnectionInfo> connectedServerDetails = new ObservableList<>();
    private final ObservableProperty<Connection> connection = new ObservableProperty<>(null);
    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

    private CountDownTask countDownTask;
    private ScheduledFuture<?> runningCountdownTask;
    private InterpolationData interpolationData;

    private ClientContext() {
    }

    public static ClientContext getInstance() {
        return INSTANCE;
    }

    public void connect(String host, ConnectionListener connectionListener) {
        shutdownService = null;
        volumeService = null;
        payloadFactory = null;
        mouseService = null;
        communicationService = null;

        if (host != null) {
            actionSenderService = new DefaultActionSenderService(host);
            getLocatedServerDetails()
                    .stream()
                    .filter(sci -> sci.getHostAddress().equals(host))
                    .findFirst()
                    .ifPresent(connectionInfo -> connectionListener.onConnectedToServer(
                            new Connection(
                                    host,
                                    ChupacabraRemote.CLIENT_LISTENER_PORT,
                                    connectionInfo)));
        } else {
            connectionListener.onDisconnected(connection.getValue());
        }
    }

    @Override
    public ObservableList<ServerConnectionInfo> getLocatedServerDetails() {
        return connectedServerDetails;
    }

    @Override
    public ObservableProperty<Connection> getConnection() {
        return connection;
    }

    @Override
    public ShutdownService getShutdownService() {
        if (shutdownService == null) {
            shutdownService = new DefaultShutdownService(getActionSenderService(), getPayloadFactory());
        }
        return shutdownService;
    }

    @Override
    public VolumeService getVolumeService() {
        if (volumeService == null) {
            volumeService = new DefaultVolumeService(getActionSenderService(), getPayloadFactory());
        }
        return volumeService;
    }

    @Override
    public MouseService getMouseService() {
        if (mouseService == null) {
            mouseService = new DefaultMouseService(getActionSenderService(), getPayloadFactory());
        }
        return mouseService;
    }

    @Override
    public ActionSenderService getActionSenderService() {
        if (actionSenderService == null) {
            throw new ClientContextNotInitalizedException("Client context has not been initalized");
        }
        return actionSenderService;
    }

    @Override
    public PayloadFactory getPayloadFactory() {
        if (payloadFactory == null) {
            payloadFactory = new DefaultPayloadFactory();
        }
        return payloadFactory;
    }

    @Override
    public CommunicationService getCommunicationService() {
        if (communicationService == null) {
            communicationService = new DefaultCommunicationService();
        }
        return communicationService;
    }

    @Override
    public KeyboardService getKeyboardService() {
        if (keyboardService == null) {
            keyboardService = new DefaultKeyboardService(getPayloadFactory(), getActionSenderService());
        }
        return keyboardService;
    }

    public void startCountdownTask(InterpolationData interpolationData, long waitUntil) {
        this.interpolationData = interpolationData;
        countDownTask = new CountDownTask(interpolationData, System.currentTimeMillis(), waitUntil);
        runningCountdownTask = scheduledExecutorService.scheduleAtFixedRate(countDownTask, 0, 1, TimeUnit.SECONDS);
    }

    public void stopCountdownTask() {
        countDownTask.registerShutdownCounter(null);
        countDownTask.registerLinechart(null);

        countDownTask.stop();
        runningCountdownTask.cancel(true);

        interpolationData = null;
        countDownTask = null;
        runningCountdownTask = null;
    }

    public CountDownTask getCountDownTask() {
        return countDownTask;
    }

    public InterpolationData getInterpolationData() {
        return interpolationData;
    }

    public void setInterpolationData(InterpolationData interpolationData) {
        this.interpolationData = interpolationData;
    }

    public DefaultConnectionService getConnectionService() {
        return connectionService;
    }

    public void setConnectionService(DefaultConnectionService connectionService) {
        this.connectionService = connectionService;
    }
}
