package com.meandmyphone.chupacabraremote.network;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;

import com.meandmyphone.chupacabraremote.logger.Log;
import com.meandmyphone.chupacabraremote.providers.impl.ClientContext;
import com.meandmyphone.chupacabraremote.ui.listeners.NewServerLocatedListener;
import com.meandmyphone.chupacabraremote.ui.listeners.ServerUpdatedListener;
import com.meandmyphone.shared.ChupacabraRemote;
import com.meandmyphone.shared.ServerStatus;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Task responsible for receiving a multicast packet from the server app. Packet contains
 * information necessary to potentially build a connection between the client and the server.
 */
public class KeepAliveTask implements Runnable {

    private static final String TAG = KeepAliveTask.class.getSimpleName();

    private final List<NewServerLocatedListener> newServerLocatedListeners = new ArrayList<>();
    private final List<ServerUpdatedListener> serverUpdatedListeners = new ArrayList<>();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Context context;

    public KeepAliveTask(Context context) {
        this.context = context;
    }

    @Override
    public void run() {

        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiManager.MulticastLock multicastLock = null;

        if (wifi != null) {
            multicastLock = wifi.createMulticastLock(getClass().getName());
            multicastLock.setReferenceCounted(true);
            multicastLock.acquire();
        }

        try (MulticastSocket socket = new MulticastSocket(ChupacabraRemote.MULTICAST_PORT)) {
            InetAddress group = InetAddress.getByName(ChupacabraRemote.MULTICAST_GROUP_ADDRESS);
            socket.joinGroup(group);
            while (!Thread.currentThread().isInterrupted()) {
                byte[] buf = new byte[512];

                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                String received = new String(
                        packet.getData(), 0, packet.getLength());
                Log.d(TAG, "Multicast message received: " + received);

                parseAndPostMulticastMessage(received);
            }
            socket.leaveGroup(group);
            Log.d(TAG, "Multicast message receiver disposed.");
        } catch (IOException ioe) {
            Log.e(TAG, "KeepAliveTask failed: " + ioe.getMessage());
        } finally {
            if (multicastLock != null && multicastLock.isHeld()) {
                multicastLock.release();
            }
        }
    }

    private void parseAndPostMulticastMessage(String received) {
        try {

            JSONObject json = new JSONObject(received);
            final ServerStatus serverStatus = new ServerStatus(json);

            Optional<ServerConnectionInfo> serverConnectionInfo = ClientContext.getInstance()
                    .getLocatedServerDetails().stream()
                    .filter(s -> s.getHostAddress()
                            .equals(serverStatus.getHostAddress()))
                    .findFirst();

            if (serverConnectionInfo.isPresent()) {

                ServerConnectionInfo updated = serverConnectionInfo.get();
                updated.setCurrentVolume(serverStatus.getCurrentVolume());
                updated.setServerTime(serverStatus.getServerTime());
                updated.setShutdownInSeconds(serverStatus.getShutdownInSeconds());
                updated.setServerStartTime(serverStatus.getServerStartTime());
                updated.setAlive(true);
                updated.setInterpolationData(serverStatus.getInterpolationData());
                updated.refresh();

                handler.post(() -> serverUpdatedListeners.forEach(list -> {
                    if (list != null) list.onServerInformationUpdated(updated);
                }));

            } else {
                handler.post(() -> newServerLocatedListeners.forEach(list -> {
                    if (list != null)
                        list.onNewServerLocated(new ServerConnectionInfo(serverStatus));
                }));
            }

        } catch (JSONException e) {
            Log.e(TAG, "Invalid multicast message received: " + received);
        }
    }

    public void registerNewServerLocatedListener(NewServerLocatedListener newServerLocatedListener) {
        newServerLocatedListeners.add(newServerLocatedListener);
    }

    public void registerServerUpdatedListener(ServerUpdatedListener serverUpdatedListener) {
        serverUpdatedListeners.add(serverUpdatedListener);
    }

    public void removeServerUpdatedListener(ServerUpdatedListener serverUpdatedListener) {
        serverUpdatedListeners.remove(serverUpdatedListener);
    }
}
