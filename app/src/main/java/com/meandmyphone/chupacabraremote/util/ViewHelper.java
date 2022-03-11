package com.meandmyphone.chupacabraremote.util;

import androidx.appcompat.widget.AppCompatImageButton;

public class ViewHelper {
    private ViewHelper() {}

    public static void switchEnabledState(AppCompatImageButton button) {
        if (button.isEnabled()) {
            button.setEnabled(false);
            button.setImageAlpha(128);
            button.setAlpha(0.5f);
        } else {
            button.setEnabled(true);
            button.setImageAlpha(255);
            button.setAlpha(1.0f);
        }
    }
}
