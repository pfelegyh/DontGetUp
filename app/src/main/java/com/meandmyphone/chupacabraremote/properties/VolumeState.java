package com.meandmyphone.chupacabraremote.properties;

import com.meandmyphone.chupacabraremote.R;

/**
 * Intervals defined here categorize the actual volume value ranging between 0 to 65535 to three
 * different categories: MUTED, FAINT and LOUD.
 */
public enum VolumeState {

    MUTED(0, 10, R.drawable.ic_volume_mute_black_24dp),
    FAINT(11, 21999, R.drawable.ic_volume_down_black_24dp),
    LOUD(22000, 65535, R.drawable.ic_volume_up_black_24dp);

    private final int min;
    private final int max;
    private final int resId;

    VolumeState(int min, int max, int resId) {
        this.min = min;
        this.max = max;
        this.resId = resId;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public int getResId() {
        return resId;
    }

    public static VolumeState forVolume(int volume) {
        for (VolumeState state : VolumeState.values()) {
            if (state.getMin() <= volume && state.getMax() >= volume ) {
                return state;
            }
        }
        throw new IllegalArgumentException("No state for volume: " + volume);
    }
}
