package com.meandmyphone.chupacabraremote.logger;

import com.meandmyphone.chupacabraremote.BuildConfig;

/**
 * Logger that picks up property set from buildscript and it has separate levels of logging in
 * debug/release builds based on said property
 */
public class Log {

    private Log(){}

    public static void d(String tag, String message) {
        if (BuildConfig.LOGLEVEL <= 0) {
            android.util.Log.d(tag, message);
        }
    }

    public static void w(String tag, String message) {
        if (BuildConfig.LOGLEVEL <= 1) {
            android.util.Log.w(tag, message);
        }
    }

    public static void e(String tag, String message) {
        if (BuildConfig.LOGLEVEL <= 2) {
            android.util.Log.e(tag, message);
        }
    }
}
