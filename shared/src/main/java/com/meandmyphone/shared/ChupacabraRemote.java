package com.meandmyphone.shared;

public class ChupacabraRemote {

    private ChupacabraRemote() {
    }

    public static final int CLIENT_LISTENER_PORT = 9010;
    public static final int MULTICAST_PORT = 9082;
    public static final String MULTICAST_GROUP_ADDRESS = "230.0.9.1";
    public static final int MAX_NUMBER_OF_CONNECTED_SERVERS = 20;
    public static final int VERSION_MAJOR = 1;
    public static final int VERSION_MINOR = 0;
    public static final String THEME_KEY = "theme";
    public static final String DARK_THEME = "dark";
    public static final String LIGHT_THEME = "light";
    public static final String ANIMATIONS_ENABLED_KEY = "animationsEnabled";

}
