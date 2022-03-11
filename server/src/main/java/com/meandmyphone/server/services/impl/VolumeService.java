package com.meandmyphone.server.services.impl;

import com.meandmyphone.shared.InterpolationData;
import com.meandmyphone.util.Ease;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class VolumeService {

    private static final int TICK = 1000;
    private static final Logger LOGGER = Logger.getLogger(VolumeService.class.getSimpleName());

    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> runningTask;
    private InterpolationData interpolationData;

    public native int getVolume();

    public native void setVolume(int volume);

    public void startInterpolatingVolume(final int startValue, final int endValue, final int interpolationType, final int change, final long duration, final long startTime, final VolumeInterpolationFinishedCallback volumeInterpolationFinishedCallback) {
        interpolationData = new InterpolationData(startValue, endValue, interpolationType, duration, startTime);
        runningTask = executorService.scheduleAtFixedRate(() -> {
            long elapsedTime = System.currentTimeMillis() - startTime;
            int currentVolume = endValue;
            if (elapsedTime >= duration) {
                LOGGER.info("Interpolaiton finished: " + elapsedTime);
                interpolationData = null;
                volumeInterpolationFinishedCallback.volumeInterpolationFinished();
            } else {
                currentVolume = (int) Ease.calculateFloat(interpolationType, elapsedTime, startValue, change, duration);
            }
            if (currentVolume != getVolume()) {
                LOGGER.info("Interpolating volume... current value = " + currentVolume);
                setVolume(currentVolume);
            }
        }, TICK, TICK, TimeUnit.MILLISECONDS);
    }

    public void stopInterpolatingVolume() {
        if (runningTask != null) {
            runningTask.cancel(true);
        }
        interpolationData = null;
    }

    public interface VolumeInterpolationFinishedCallback {
        void volumeInterpolationFinished();
    }

    public InterpolationData getRunningInterpolationData() {
        return interpolationData;
    }
}
