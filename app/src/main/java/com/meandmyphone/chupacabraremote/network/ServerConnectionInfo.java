package com.meandmyphone.chupacabraremote.network;

import androidx.annotation.NonNull;

import com.meandmyphone.shared.InterpolationData;
import com.meandmyphone.shared.ServerStatus;

import java.util.Objects;
import java.util.Observable;

/**
 * Contains the status {@link ServerStatus} of a server enriched with metadata related actual
 * connection status
 */
public class ServerConnectionInfo extends Observable implements Comparable<ServerConnectionInfo> {

    private long lastSeen;
    private boolean alive;
    private final ServerStatus serverStatus;

    public ServerConnectionInfo(ServerStatus serverStatus) {
        this.serverStatus = serverStatus;
        this.alive = true;
        lastSeen = System.currentTimeMillis();
    }

    @Override
    public synchronized void setChanged() {
        super.setChanged();
    }

    public long getLastSeen() {
        return lastSeen;
    }

    public String getHostAddress() {
        return serverStatus.getHostAddress();
    }

    public String getHostName() {
        return serverStatus.getHostName();
    }

    public long getServerStartTime() {
        return serverStatus.getServerStartTime();
    }

    public long getServerTime() {
        return serverStatus.getServerTime();
    }

    public int getCurrentVolume() {
        return serverStatus.getCurrentVolume();
    }

    public int getShutdownInSeconds() {
        return serverStatus.getShutdownInSeconds();
    }

    public String getVersionString() {return serverStatus.getVersion(); }

    public boolean isShutdownScheduled() {
        return getShutdownInSeconds() != -1;
    }

    public boolean isAlive() {
        return alive;
    }

    public InterpolationData getInterpolationData() {
        return serverStatus.getInterpolationData();
    }

    public void setServerTime(long serverTime) {
        // notifying observers would cause two invalidaitons of adapterlist?
        serverStatus.setServerTime(serverTime);
    }

    public void setCurrentVolume(int currentVolume) {
        if (this.getCurrentVolume() != currentVolume) {
            serverStatus.setCurrentVolume(currentVolume);
            setChanged();
            notifyObservers();
        }
    }

    public void setShutdownInSeconds(int seconds) {
        if (seconds != serverStatus.getShutdownInSeconds()) {
            serverStatus.setShutdownScheduled(seconds);
            setChanged();
            notifyObservers();
        }
    }

    public void setInterpolationData(InterpolationData interpolationData) {
        if (interpolationData != serverStatus.getInterpolationData()) {
            serverStatus.setInterpolationData(interpolationData);
            setChanged();
            notifyObservers();
        }
    }

    public void setAlive(boolean alive) {
        if (this.alive != alive) {
            this.alive = alive;
            setChanged();
            notifyObservers();
        }
    }

    public void setServerStartTime(long serverStartTime) {
        serverStatus.setServerStartTime(serverStartTime);
    }

    public void refresh() {
        this.lastSeen = System.currentTimeMillis();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServerConnectionInfo that = (ServerConnectionInfo) o;
        return Objects.equals(serverStatus.getHostAddress(), that.serverStatus.getHostAddress());
    }

    @Override
    public int hashCode() {
        return Objects.hash(serverStatus.getHostAddress());
    }

    @Override
    public int compareTo(@NonNull ServerConnectionInfo serverConnectionInfo) {
        return serverStatus.getHostAddress().compareTo(serverConnectionInfo.getHostAddress());
    }

    @Override
    public String toString() {
        return "ServerConnectionInfo{" +
                "lastSeen=" + lastSeen +
                ", connected=" + alive +
                ", serverStatus=" + serverStatus +
                '}';
    }
}
