package com.meandmyphone.chupacabraremote.service.api;

import java.io.IOException;

/**
 * A high level service responsible for various volume related operations on the connected server.
 */
public interface VolumeService {

    boolean isVolumeChanging();
    void startVolumeChange(int volume);
    void changeVolume(int volume);
    void finishVolumeChange(int volume);
    void interpolateVolumeToZero(int interpolationType, long duration, int volume) throws IOException;
}
