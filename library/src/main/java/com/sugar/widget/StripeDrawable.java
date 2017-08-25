package com.sugar.widget;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * 条纹背景
 *
 * @author Sugar
 */
public class StripeDrawable extends Drawable {

    private Paint mPaint;
    private int borderWidth;

    public StripeDrawable() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.GREEN);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        Rect rect = getBounds();
        int mWidth = rect.right - rect.left - borderWidth;
        int mHeight = rect.bottom - rect.top - borderWidth;
        for (int i = 2; i < mWidth / 10 - 1; i += 2) {
            Path item = new Path();
            item.moveTo(10 * i, borderWidth);
            item.lineTo(10 * i + 10, borderWidth);
            item.lineTo(10 * i - 10, mHeight - borderWidth);
            item.lineTo(10 * i - 20, mHeight - borderWidth);
            item.close();
            canvas.drawPath(item, mPaint);
        }
    }

    @Override
    public void setAlpha(@IntRange(from = 0, to = 255) int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    public void setBorderWidth(int borderWidth) {
        this.borderWidth = borderWidth;
    }
}
