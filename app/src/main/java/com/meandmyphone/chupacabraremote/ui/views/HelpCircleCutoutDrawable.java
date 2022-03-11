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
import androidx.annotation.Nullable;

import com.meandmyphone.chupacabraremote.R;

public class HelpCircleCutoutDrawable extends Drawable {

    private final Context context;
    private final RectF screenSize;
    private final float x;
    private final float y;
    private final float radius;

    public HelpCircleCutoutDrawable(Context context, RectF screenSize, float x, float y, float radius) {
        this.context = context;
        this.screenSize = screenSize;
        this.x = x;
        this.y = y;
        this.radius = radius;
    }

    @Override
    public void draw(Canvas canvas) {
        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);

        path.addRect(screenSize, Path.Direction.CW);
        path.addCircle(x, y, radius, Path.Direction.CCW);

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
