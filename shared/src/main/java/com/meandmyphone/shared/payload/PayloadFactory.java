package com.meandmyphone.shared.payload;

public interface PayloadFactory {

    Payload createAbortShutdownPayload();

    Payload createShutdownDelayedPayload(long millis);

    Payload createSetVolumePayload(int volume);

    Payload createInterpolateVolumePayload(int interpolationType, long duration, int startVolume, int endVolume);

    Payload createCancelInterpolationPayload();

    Payload createMouseClickPayload(int screenIndex, int button, int x, int y);

    @Deprecated
    Payload createMoveMousePayload(int x, int y);

    Payload createMoveMouseByPayload(double vectorX, double vectorY);

    Payload createHoldMouseButtonPayload(int button);

    Payload createReleaseMouseButtonPayload(int button);

    Payload createSendKeyAction(String keys);
}
