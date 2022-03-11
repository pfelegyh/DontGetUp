package com.meandmyphone.util;

import java.util.Random;

/**
 * Created by csumpakadabra on 2017.10.18..
 */

public class Ease {



    static Random rand = new Random();

    private Ease() {}

    /**
     * @param easeType type of ease
     * @param t current time
     * @param b start value
     * @param c change
     * @param d duration
     * @return
     */
    public static float calculateFloat(int easeType, float t, float b, float c, float d ) {
        Interpolation interpolation = Interpolation.forValue(easeType);
        switch (interpolation) {
            case LINEAR: return linear(t, b, c, d);
            case CUBICEASEIN: return cubicEaseIn(t, b, c, d);
            case CUBICEASEOUT: return cubicEaseOut(t, b, c, d);
            case CUBICEASEINOUT: return cubicEaseInOut(t, b, c, d);
            case QUADRATICEASEIN: return quadraticEaseIn(t, b, c, d);
            case QUADRATICEASEOUT: return quadraticEaseOut(t, b, c, d);
            case QUADRATICEASEINOUT: return quadraticEaseInOut(t, b, c, d);
            case SINEASEIN: return sinEaseIn(t, b, c, d);
            case SINEASEOUT: return sinEaseOut(t, b, c, d);
            case SINEASEINOUT: return sinEaseInOut(t, b, c, d);

            default: throw new IllegalArgumentException("No easeType: " + easeType);
        }
    }

    public static int randomEase() {
        return rand.nextInt(Interpolation.values().length);
    }

    private static float linear(float t, float b, float c, float d ) {
        return c*t/d+b;
    }

    private static float quadraticEaseIn(float t, float b, float c, float d ){
        t /= d;
        return c*t*t+b;
    }

    private static float quadraticEaseOut(float t, float b, float c, float d ) {
        t /= d;
        return -c * t*(t-2) + b;
    }

    private static float quadraticEaseInOut(float t, float b, float c, float d ){
        t /= d/2;
        if (t < 1) return c/2*t*t + b;
        t--;
        return -c/2 * (t*(t-2) - 1) + b;
    }

    private static float cubicEaseIn(float t, float b, float c, float d ){
        t /= d;
        return c*t*t*t + b;
    }

    private static float cubicEaseOut(float t, float b, float c, float d) {
        t /= d;
        t--;
        return c * (t * t * t + 1) + b;
    }

    private static float cubicEaseInOut(float t, float b, float c, float d) {
        t /= d / 2;
        if (t < 1) return c / 2 * t * t * t + b;
        t -= 2;
        return c / 2 * (t * t * t + 2) + b;
    }

    private static float sinEaseIn(float t, float b, float c, float d) {
        return (float) (-c * Math.cos(t / d * (Math.PI / 2)) + c + b);
    }

    private static float sinEaseOut(float t, float b, float c, float d) {
        return (float) (c * Math.sin(t / d * (Math.PI / 2)) + b);
    }

    private static float sinEaseInOut(float t, float b, float c, float d) {
        return (float) (-c / 2 * (Math.cos(Math.PI * t / d) - 1) + b);
    }


}