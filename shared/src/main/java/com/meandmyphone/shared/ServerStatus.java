package com.meandmyphone.shared;

import org.json.JSONObject;

import java.util.Objects;

@SuppressWarnings("squid:S107")
public class ServerStatus {

    private final JSONObject json;

    public ServerStatus(JSONObject json) {
        this.json = json;
    }

    public ServerStatus(String hostName, String hostAddress, long serverTime, long serverStartTime, int volume, int shutdownInSeconds, InterpolationData interpolationData, String versionString) {
        json = new JSONObject();
        json.put(JSONKeys.HOST_ADDRESS, hostAddress);
        json.put(JSONKeys.HOST_NAME, hostName);
        json.put(JSONKeys.SERVER_TIME, serverTime);
        json.put(JSONKeys.SERVER_START_TIME, serverStartTime);
        json.put(JSONKeys.CURRENT_VOLUME, volume);
        json.put(JSONKeys.SHUTDOWN_SCHEDULED, shutdownInSeconds);
        if (interpolationData != null) {
            json.put(JSONKeys.INTERPOLATION, interpolationData.toJson());
        }
        json.put(JSONKeys.VERSION_KEY, versionString);
    }

    public String getHostAddress() {
        return json.getString(JSONKeys.HOST_ADDRESS);
    }

    public String getHostName() {
        return json.getString(JSONKeys.HOST_NAME);
    }

    public long getServerStartTime() {
        return json.getLong(JSONKeys.SERVER_START_TIME);
    }

    public int getShutdownInSeconds() {
        return json.getInt(JSONKeys.SHUTDOWN_SCHEDULED);
    }

    public long getServerTime() {
        return json.getLong(JSONKeys.SERVER_TIME);
    }

    public int getCurrentVolume() {
        return json.getInt(JSONKeys.CURRENT_VOLUME);
    }

    public InterpolationData getInterpolationData() {
        JSONObject interpolationJson = json.optJSONObject(JSONKeys.INTERPOLATION);
        if (interpolationJson != null) {
            return new InterpolationData(interpolationJson);
        }
        return null;
    }

    public String getVersion() {
        return json.getString(JSONKeys.VERSION_KEY);
    }

    public void setServerStartTime(long serverStartTime) {
        json.put(JSONKeys.SERVER_START_TIME, serverStartTime);
    }

    public void setServerTime(long serverTime) {
        json.put(JSONKeys.SERVER_TIME, serverTime);
    }

    public void setCurrentVolume(int currentVolume) {
        json.put(JSONKeys.CURRENT_VOLUME, currentVolume);
    }

    public void setShutdownScheduled(int shutdownScheduled) {
        json.put(JSONKeys.SHUTDOWN_SCHEDULED, shutdownScheduled);
    }

    public void setInterpolationData(InterpolationData interpolationData) {
        if (interpolationData != null) {
            json.put(JSONKeys.INTERPOLATION, interpolationData.toJson());
        } else {
            json.remove(JSONKeys.INTERPOLATION);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServerStatus that = (ServerStatus) o;
        return Objects.equals(json, that.json);
    }

    @Override
    public int hashCode() {
        return Objects.hash(json);
    }

    @Override
    public String toString() {
        return json.toString();
    }


}
