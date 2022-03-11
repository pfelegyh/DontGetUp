package com.meandmyphone.shared;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.Objects;

public class InterpolationData implements Serializable {
    private final int startValue;
    private final int endValue;
    private final int interpolationType;
    private final long duration;
    private final long startTime;

    public InterpolationData(JSONObject jsonObject) {
        this(
                jsonObject.getInt(JSONKeys.INTERPOLATE_VOLUME_START_VALUE),
                jsonObject.getInt(JSONKeys.INTERPOLATE_VOLUME_END_VALUE),
                jsonObject.getInt(JSONKeys.INTERPOLATE_VOLUME_INTERPOLATIONTYPE),
                jsonObject.getInt(JSONKeys.INTERPOLATE_VOLUME_DURATION),
                jsonObject.getInt(JSONKeys.INTERPOLATE_START_TIME)
        );
    }

    public InterpolationData(int startValue, int endValue, int interpolationType, long duration, long startTime) {
        this.startValue = startValue;
        this.endValue = endValue;
        this.interpolationType = interpolationType;
        this.duration = duration;
        this.startTime = startTime;
    }

    public int getStartValue() {
        return startValue;
    }

    public int getEndValue() {
        return endValue;
    }

    public int getInterpolationType() {
        return interpolationType;
    }

    public long getDuration() {
        return duration;
    }

    public long getStartTime() {
        return startTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InterpolationData that = (InterpolationData) o;
        return startValue == that.startValue &&
                endValue == that.endValue &&
                interpolationType == that.interpolationType &&
                duration == that.duration &&
                startTime == that.startTime;
    }

    @Override
    public int hashCode() {
        return Objects.hash(startValue, endValue, interpolationType, duration, startTime);
    }

    @Override
    public String toString() {
        return "InterpolationData{" +
                "startValue=" + startValue +
                ", endValue=" + endValue +
                ", interpolationType=" + interpolationType +
                ", duration=" + duration +
                ", startTime=" + startTime +
                '}';
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(JSONKeys.INTERPOLATE_VOLUME_START_VALUE, startValue);
        jsonObject.put(JSONKeys.INTERPOLATE_VOLUME_END_VALUE, endValue);
        jsonObject.put(JSONKeys.INTERPOLATE_VOLUME_INTERPOLATIONTYPE, interpolationType);
        jsonObject.put(JSONKeys.INTERPOLATE_VOLUME_DURATION, duration);
        jsonObject.put(JSONKeys.INTERPOLATE_START_TIME, startTime);
        return jsonObject;
    }
}
