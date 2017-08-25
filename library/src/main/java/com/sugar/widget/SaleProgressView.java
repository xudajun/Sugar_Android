package com.sugar.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.sugar.utils.DensityUtil;

/**
 * Progress followed TaoBao sale
 *
 * @author Sugar
 */
public class SaleProgressView extends View {

    private Paint bgPaint;
    private Paint bgStripePaint;
    private Paint progressPaint;
    private Paint textPaint;
    private int mWidth;
    private int mHeight;
    private int borderWidth = 5;
    private RectF bgRectF;
    private RectF progressRectF;
    private int currentProgress = 100;
    private int progress = 15;
    private int progressMax = 100;
    private float baseLineY;

    public SaleProgressView(Context context) {
        this(context, null);
    }

    public SaleProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SaleProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        bgPaint = new Paint();
        bgPaint.setAntiAlias(true);
        bgPaint.setColor(Color.GREEN);

        bgStripePaint = new Paint();
        bgStripePaint.setAntiAlias(true);
        bgStripePaint.setColor(Color.GREEN);


        progressPaint = new Paint();
        progressPaint.setAntiAlias(true);
        progressPaint.setColor(Color.RED);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextSize(DensityUtil.dip2px(getContext(), 15));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        float density = getResources().getDisplayMetrics().density;
        switch (widthMode) {
            case MeasureSpec.AT_MOST:
                mWidth = (int) (300 * density);//default size 200dp
                break;
        }
        switch (heightMode) {
            case MeasureSpec.AT_MOST:
                mHeight = (int) (30 * density);//default size 15dp
                break;
        }
        setMeasuredDimension(mWidth, mHeight);
        bgRectF = new RectF(borderWidth, borderWidth, mWidth - borderWidth, mHeight - borderWidth);
        if (baseLineY == 0.0f) {
            Paint.FontMetricsInt fm = textPaint.getFontMetricsInt();
            baseLineY = mHeight / 2 - (fm.descent / 2 + fm.ascent / 2);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBackground(canvas);

        progressRectF = new RectF(2 * borderWidth, 2 * borderWidth, (mWidth - 2 * borderWidth) * currentProgress / progressMax, mHeight - 2 * borderWidth);
        canvas.drawRoundRect(progressRectF, mHeight / 2, mHeight / 2, progressPaint);

        drawText(canvas);
        //这里是为了演示动画方便，实际开发中进度只会增加
        if (progress != currentProgress) {
            if (currentProgress < progress) {
                currentProgress++;
            } else {
                currentProgress--;
            }
            postInvalidate();
        }
    }

    public void setProgress(int progress) {
        this.progress = progress;
        invalidate();
    }

    private void drawBackground(Canvas canvas) {
        Bitmap bgBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        Canvas bgCanvas = new Canvas(bgBitmap);
        bgCanvas.drawRoundRect(bgRectF, mHeight / 2, mHeight / 2, bgPaint);

        Bitmap stripeBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        Canvas stripeCanvas = new Canvas(stripeBitmap);
        for (int i = 2; i < mWidth / 20 - 1; i += 2) {
            Path item = new Path();
            item.moveTo(20 * i, 0);
            item.lineTo(20 * i + 20, 0);
            item.lineTo(20 * i - 20, mHeight);
            item.lineTo(20 * i - 40, mHeight);
            item.close();
            stripeCanvas.drawPath(item, bgStripePaint);
        }
        bgPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        bgCanvas.drawBitmap(stripeBitmap, null, bgRectF, bgPaint);
        bgPaint.setXfermode(null);
        canvas.drawBitmap(bgBitmap, null, bgRectF, bgStripePaint);
        bgStripePaint.setStrokeWidth(borderWidth);
        bgStripePaint.setStyle(Paint.Style.STROKE);
        canvas.drawRoundRect(bgRectF, mHeight / 2, mHeight / 2, bgStripePaint);
    }

    private void drawText(Canvas canvas) {
        float scale = (float) currentProgress / (float) progressMax;
        String scaleText = currentProgress + "%";
        String saleText = String.format("已抢%s件", currentProgress);
        float scaleTextWidth = textPaint.measureText(scaleText);
        Bitmap textBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        Canvas textCanvas = new Canvas(textBitmap);
        textPaint.setColor(Color.BLACK);
        textCanvas.drawText(saleText, DensityUtil.dip2px(getContext(), 20), baseLineY, textPaint);
        textCanvas.drawText(scaleText, mWidth - scaleTextWidth - DensityUtil.dip2px(getContext(), 10), baseLineY, textPaint);
        textPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        textPaint.setColor(Color.WHITE);
        textCanvas.drawRoundRect(new RectF(borderWidth, borderWidth, (mWidth - borderWidth) * scale, mHeight - borderWidth),
                mHeight / 2, mHeight / 2, textPaint);
        canvas.drawBitmap(textBitmap, 0, 0, null);
        textPaint.setXfermode(null);
    }
}
