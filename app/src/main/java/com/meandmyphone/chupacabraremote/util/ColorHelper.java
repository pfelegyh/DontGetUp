package com.meandmyphone.chupacabraremote.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

public class ColorHelper {

    private ColorHelper() {}

    public static int getColor(Context context, int color) {
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        theme.resolveAttribute(color, typedValue, true);
        return typedValue.data;
    }
}
