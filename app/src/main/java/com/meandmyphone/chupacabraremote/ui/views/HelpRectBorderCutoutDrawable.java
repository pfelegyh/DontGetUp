package com.meandmyphone.chupacabraremote.ui.views;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.meandmyphone.chupacabraremote.R;

public class HelpRectBorderCutoutDrawable extends Drawable {

    private final Context context;
    private final RectF screenSize;
    private final float left;
    private final float top;
    private final float right;
    private final float bottom;
    private final boolean innerRect;

    public HelpRectBorderCutoutDrawable(Context context, RectF screenSize, float left, float top, float right, float bottom, boolean innerRect) {
        this.context = context;
        this.screenSize = screenSize;
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.innerRect = innerRect;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);

        float width = 0.0f;
        float height = 0.0f;

        if (innerRect) {
            width = right - left;
            height = bottom - top;
        }

        path.addRect(screenSize, Path.Direction.CW);
        path.addRect(
                left + width / 10.0f,
                top + height / 10.0f,
                right - width / 10.0f,
                bottom - height / 10.0f,
                Path.Direction.CCW
        );

        Paint paint = new Paint();
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        theme.resolveAttribute(R.attr.colorPrimaryDark, typedValue, true);
        @ColorInt int color = typedValue.data;

        paint.setColor(color);
        paint.setAlpha(200);

        paint.setMaskFilter(new BlurMaskFilter(20, BlurMaskFilter.Blur.NORMAL));
        canvas.drawPath(path, paint);
    }

    @Override
    public void setAlpha(int alpha) {
        // unused
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        // unused
    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }
}
