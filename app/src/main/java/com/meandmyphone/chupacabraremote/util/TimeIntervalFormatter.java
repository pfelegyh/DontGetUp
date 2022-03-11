package com.meandmyphone.chupacabraremote.util;

import java.util.Locale;

@SuppressWarnings("squid:S3776")
public class TimeIntervalFormatter {

    private static final String TIMEFORMAT = "%d %s";

    private TimeIntervalFormatter() {}

    public static String toStringInterval(long millis) {
        long seconds = millis / 1000;
        if (seconds > 60) {
            long minutes = seconds / 60;
            if (minutes > 60) {
                long hours = minutes / 60;
                if (hours > 24) {
                    long days = hours / 24;
                    return String.format(Locale.ROOT, TIMEFORMAT, days, days > 1 ? "days" : "day");
                } else {
                    return String.format(Locale.ROOT, TIMEFORMAT, hours, hours > 1 ? "hours" : "hour");
                }
            } else {
                return String.format(Locale.ROOT, TIMEFORMAT, minutes, minutes > 1 ? "minutes" : "minute");
            }
        } else {
            return String.format(Locale.ROOT, TIMEFORMAT, seconds, seconds > 1 ? "seconds" : "second");
        }
    }
}
