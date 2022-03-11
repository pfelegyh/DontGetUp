package com.meandmyphone.chupacabraremote.service.impl;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import androidx.annotation.Nullable;

import com.meandmyphone.chupacabraremote.logger.Log;
import com.meandmyphone.chupacabraremote.network.KeepAliveTask;
import com.meandmyphone.chupacabraremote.network.ServerConnectionInfo;
import com.meandmyphone.chupacabraremote.network.ServerDisconnectionCheckerTask;
import com.meandmyphone.chupacabraremote.properties.ObservableList;
import com.meandmyphone.chupacabraremote.providers.impl.ClientContext;
import com.meandmyphone.chupacabraremote.ui.listeners.ServerLocatedListener;
import com.meandmyphone.chupacabraremote.ui.listeners.ServerUpdatedListener;
import com.meandmyphone.shared.action.Action;
import com.meandmyphone.shared.action.ActionType;
import com.meandmyphone.shared.payload.DefaultPayload;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * A {@link android.app.Service} that is responsible for managing the tasks receiving the multicast
 * broadcasts from available servers, and also manages the {@link ServerDisconnectionCheckerTask}s
 */
public class DefaultConnectionService extends Service {

    private static final String TAG = DefaultConnectionService.class.getSimpleName();
    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(2);
    private KeepAliveTask keepAliveTask;
    private final ServerDisconnectionCheckerTask serverDisconnectionCheckerTask = new ServerDisconnectionCheckerTask();

    private Future<?> runningKeepAliveHandle;
    private ScheduledFuture<?> runningDisconnectionWatcherHandle;

    private ConnectionServiceBinder binder;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {

        super.onCreate();
        binder = new ConnectionServiceBinder();

        Log.d(TAG, "Starting connection service...");


    }

    public void init() {

        initTaskIfNecessary();

        runningKeepAliveHandle = scheduledExecutorService.submit(keepAliveTask);
        runningDisconnectionWatcherHandle = scheduledExecutorService.scheduleAtFixedRate(
                serverDisconnectionCheckerTask,
                ServerDisconnectionCheckerTask.DISCONNECTION_THRESHOLD,
                ServerDisconnectionCheckerTask.DISCONNECTION_THRESHOLD,
                TimeUnit.SECONDS);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (runningKeepAliveHandle != null) {
            runningKeepAliveHandle.cancel(true);
        }

        if (runningDisconnectionWatcherHandle != null) {
            runningDisconnectionWatcherHandle.cancel(true);
        }
    }

    void initTaskIfNecessary() {
        if (keepAliveTask == null) {
            keepAliveTask = new KeepAliveTask(getApplicationContext());
        }
    }

    public void addServerLocatedListener(ServerLocatedListener serverLocatedListener) {
        ObservableList<ServerConnectionInfo> locatedServers = ClientContext.getInstance().getLocatedServerDetails();

        initTaskIfNecessary();


        keepAliveTask.registerNewServerLocatedListener(serverLocatedListener);
        keepAliveTask.registerServerUpdatedListener(serverLocatedListener);

        serverDisconnectionCheckerTask.registerServerDisonnectedListener(serverInfo -> {
            try {
                JSONObject ack = new JSONObject();
                ack.put("ack", "ack");
                List<Action> response = ClientContext.getInstance().getActionSenderService().sendActionSync(ActionType.ACKNOWLEDGE, new DefaultPayload(ack.toString()));

                if (ActionType.ERROR.equals(response.get(0).getType())) {
                    Log.d(TAG, "Error receiving acknowledge from " + serverInfo.getHostAddress() + ". Notifying view, server is disconnected.");
                    for (ServerConnectionInfo connectionInfo : locatedServers) {
                        if (connectionInfo.getHostAddress().equals(serverInfo.getHostAddress())) {
                            connectionInfo.setAlive(false);
                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.post(() -> serverLocatedListener.onServerLost(connectionInfo));
                            return;
                        }
                    }
                } else {
                    Log.d(TAG, "Acknowledge received from " + serverInfo.getHostAddress() + ". Nothing to see here...");
                }

            } catch (JSONException | ExecutionException | IOException e) {
                Log.e(TAG, e.getMessage());
            } catch (InterruptedException e) {
                Log.e(TAG, e.getMessage());
                Thread.currentThread().interrupt();
            }
        });

    }

    public void addServerUpdatedListener(ServerUpdatedListener serverUpdatedListener) {
        keepAliveTask.registerServerUpdatedListener(serverUpdatedListener);
    }

    public void removeServerUpdatedListener(ServerUpdatedListener serverUpdatedListener) {
        keepAliveTask.removeServerUpdatedListener(serverUpdatedListener);
    }

    public class ConnectionServiceBinder extends Binder {
        public DefaultConnectionService getService() {
            return DefaultConnectionService.this;
        }
    }
}
