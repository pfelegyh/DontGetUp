package com.meandmyphone.server.action.impl;

import com.meandmyphone.server.action.api.ActionProcessor;
import com.meandmyphone.server.action.api.ActionProcessorFactory;
import com.meandmyphone.server.services.api.ShutdownService;
import com.meandmyphone.server.services.impl.KeyboardService;
import com.meandmyphone.server.services.impl.MouseService;
import com.meandmyphone.server.services.impl.VolumeService;
import com.meandmyphone.shared.action.ActionType;

import java.util.function.Consumer;

public class DefaultActionProcessorFactory implements ActionProcessorFactory {

    private final ShutdownService shutdownService;
    private final VolumeService volumeService;
    private final MouseService mouseService;
    private final KeyboardService keyboardService;
    private final Consumer<String> notificationReceiver;

    private ActionProcessor shutdownActionProcessor;
    private ActionProcessor abortShutdownActionProcessor;
    private ActionProcessor setVolumeActionProcessor;
    private ActionProcessor clickMouseActionProcessor;
    private ActionProcessor interpolateVolumeActionProcessor;
    private ActionProcessor sendKeysActionProcessor;


    public DefaultActionProcessorFactory(ShutdownService shutdownService, VolumeService volumeService,
                                         MouseService mouseService, KeyboardService keyboardService,
                                         Consumer<String> notificationReceiver) {

        this.shutdownService = shutdownService;
        this.volumeService = volumeService;
        this.mouseService = mouseService;
        this.keyboardService = keyboardService;
        this.notificationReceiver = notificationReceiver;

    }

    @Override
    public ActionProcessor createActionProcessor(String type) {
        switch (type) {
            case ActionType.SHUTDOWN : return getShutdownActionProcessor();
            case ActionType.ABORT_SHUTDOWN : return getAbortShutdownActionProcessor();
            case ActionType.SET_VOLUME: return getSetVolumeActionProcessor();
            case ActionType.INTERPOLATE_VOLUME: return getInterpolateVolumeActionProcessor();
            case ActionType.CANCEL_VOLUME_INTERPOLATION: return getInterpolateVolumeActionProcessor();
            case ActionType.ACKNOWLEDGE : return getAcknowledgeActionProcessor();
            case ActionType.CLICK_MOUSE : return getClickMouseActionProcessor(mouseService);
            case ActionType.HOLD_MOUSE_BUTTON: return new HoldMouseActionProcessor(mouseService);
            case ActionType.RELEASE_MOUSE_BUTTON: return new ReleaseMouseActionProcessor(mouseService);
            case ActionType.SEND_KEYS: return getSendKeysActionProcessor(keyboardService);
            case ActionType.SEND_NOTIFICATION: return new SendNotificationActionProcessor(notificationReceiver);
            default: return new LoggingActionProcessor("No action found for: " + type);
        }
    }

    private ActionProcessor getSendKeysActionProcessor(KeyboardService keyboardService) {
        if (sendKeysActionProcessor == null) {
            sendKeysActionProcessor = new SendKeysActionProcessor(keyboardService);
        }
        return sendKeysActionProcessor;
    }

    private ActionProcessor getClickMouseActionProcessor(MouseService mouseService) {
        if (clickMouseActionProcessor == null) {
            clickMouseActionProcessor = new ClickMouseActionProcessor(mouseService);
        }
        return clickMouseActionProcessor;
    }

    private ActionProcessor getAcknowledgeActionProcessor() {
        return new AcknowledgeActionProcessor();
    }

    private ActionProcessor getShutdownActionProcessor() {
        if (shutdownActionProcessor == null) {
            shutdownActionProcessor = new ShutdownActionProcessor(shutdownService);
        }
        return shutdownActionProcessor;
    }

    private ActionProcessor getAbortShutdownActionProcessor() {
        if (abortShutdownActionProcessor == null) {
            abortShutdownActionProcessor = new AbortShutdownActionProcessor(shutdownService);
        }
        return abortShutdownActionProcessor;
    }

    private ActionProcessor getSetVolumeActionProcessor() {
        if (setVolumeActionProcessor == null) {
            setVolumeActionProcessor = new SetVolumeActionProcessor(volumeService);
        }
        return setVolumeActionProcessor;
    }

    private ActionProcessor getInterpolateVolumeActionProcessor() {
        if (interpolateVolumeActionProcessor == null) {
            interpolateVolumeActionProcessor = new InterpolateVolumeActionProcessor(volumeService);
        }
        return interpolateVolumeActionProcessor;
    }
}
