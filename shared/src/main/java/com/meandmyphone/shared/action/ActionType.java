package com.meandmyphone.shared.action;

public class ActionType {



    public static final String SHUTDOWN = "shutdownNow";
    public static final String ABORT_SHUTDOWN = "abortShutdown";
    public static final String SET_VOLUME = "setVolume";
    public static final String INTERPOLATE_VOLUME = "interpolateVolume";
    public static final String CANCEL_VOLUME_INTERPOLATION = "cancelVolumeInterpolation";
    public static final String ACKNOWLEDGE = "acknowledge";
    public static final String CLICK_MOUSE = "clickMouse";
    public static final String HOLD_MOUSE_BUTTON = "holdMouseButton";
    public static final String RELEASE_MOUSE_BUTTON = "releaseMouseButton";
    public static final String SEND_KEYS ="sendKeys";
    public static final String SEND_NOTIFICATION = "sendNotification";
    public static final String ERROR = "error";
    public static final String LOGGING = "log";
    @Deprecated
    public static final String MOVE_MOUSE = "moveMouse";
    @Deprecated
    public static final String GET_SCREENS = "getScreens";
    @Deprecated
    public static final String MOVE_MOUSE_BY = "moveMouseBy";

    private ActionType() {
    }
}
