package com.meandmyphone.util;

public enum Interpolation {

    LINEAR("Linear", 0),
    CUBICEASEIN("Cubic Ease-in", 1),
    CUBICEASEOUT("Cubic Ease-out", 2),
    CUBICEASEINOUT("Cubic Ease-in and ~out", 3),
    QUADRATICEASEIN("Quadratic Ease-in", 4),
    QUADRATICEASEOUT("Quadratic Ease-out", 5),
    QUADRATICEASEINOUT("Quadratic Ease-in and ~out", 6),
    SINEASEIN("Sine Ease-in", 7),
    SINEASEOUT("Sine Ease-out", 8),
    SINEASEINOUT("Sine Ease-in and ~out", 9),
    ;

    private String prettyPrintedName;
    private int integerValue;

    Interpolation(String prettyPrintedName, int integerValue) {
        this.prettyPrintedName = prettyPrintedName;
        this.integerValue = integerValue;
    }

    public int getIntegerValue() {
        return integerValue;
    }

    public static Interpolation forValue(int value) {
        for (Interpolation interpolation : Interpolation.values()) {
            if (value == interpolation.integerValue) {
                return interpolation;
            }
        }
        throw new IllegalArgumentException("No interpolation: " + value);
    }

    @Override
    public String toString() {
        return prettyPrintedName;
    }
}