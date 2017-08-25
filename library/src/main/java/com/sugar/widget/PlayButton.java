package com.sugar.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Button play and pause
 *
 * @author Sugar
 */
public class PlayButton extends View {

    /**
     * Paint of background
     */
    private Paint bgPaint;
    /**
     * Paint of Button pause and play
     */
    private Paint btnPaint;
    /**
     * View width
     */
    private int mWidth;
    /**
     * View height
     */
    private int mHeight;

    private int mRectWidth;
    private int mRectHeigth;
    /**
     * background circle radius
     */
    private int mRadius;
    /**
     * 是否正在播放
     */
    private boolean isPlaying;
    private Path leftRect;
    private Path rightRect;

    private float mProgress = 0;


    public PlayButton(Context context) {
        this(context, null);
    }

    public PlayButton(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlayButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        bgPaint = new Paint();
        bgPaint.setColor(Color.GRAY);
        bgPaint.setAntiAlias(true);
        btnPaint = new Paint();
        btnPaint.setColor(Color.BLACK);
        leftRect = new Path();
        rightRect = new Path();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        float density = getResources().getDisplayMetrics().density;
        switch (widthMode) {
            case MeasureSpec.EXACTLY:
                mWidth = mHeight = Math.min(mWidth, mHeight);
                break;
            case MeasureSpec.AT_MOST:
                mWidth = mHeight = (int) (50 * density);//default size 50dp
                break;
        }
        setMeasuredDimension(mWidth, mHeight);
        mRadius = mWidth / 2;
        mRectWidth = mRectHeigth = (int) (mWidth / Math.sqrt(2)) - (int) (15 * density);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(mWidth / 2, mHeight / 2, mRadius, bgPaint);

        int xLeft1 = mWidth / 2 - mRectWidth / 2;
        int yLeft1 = mHeight / 2 - mRectHeigth / 2 + (int) (mRectWidth / 8 * (1 - mProgress));

        int xLeft2 = mWidth / 2 - (int) (mRectWidth / 2 * mProgress);
        int yLeft2 = mHeight / 2 + mRectHeigth / 2 + (int) (mRectWidth / 8 * (1 - mProgress));

        int xLeft3 = mWidth / 2 - (int) (mRectWidth / 4 * mProgress);
        int yLeft3 = mHeight / 2 + mRectHeigth / 2 + (int) (mRectWidth / 8 * (1 - mProgress));

        int xLeft4 = mWidth / 2 - (int) (mRectWidth / 4 * mProgress);
        int yLeft4 = mHeight / 2 - mRectHeigth / 2 + (int) (mRectWidth / 8 * (1 - mProgress));

        int xRight1 = mWidth / 2 + mRectWidth / 2;
        int yRight1 = mHeight / 2 - mRectHeigth / 2 + (int) (mRectWidth / 8 * (1 - mProgress));

        int xRight2 = mWidth / 2 + (int) (mRectWidth / 2 * mProgress);
        int yRight2 = mHeight / 2 + mRectHeigth / 2 + (int) (mRectWidth / 8 * (1 - mProgress));

        int xRight3 = mWidth / 2 + (int) (mRectWidth / 4 * mProgress);
        int yRight3 = mHeight / 2 + mRectHeigth / 2 + (int) (mRectWidth / 8 * (1 - mProgress));

        int xRight4 = mWidth / 2 + (int) (mRectWidth / 4 * mProgress);
        int yRight4 = mWidth / 2 - mRectHeigth / 2 + (int) (mRectWidth / 8 * (1 - mProgress));

        if (mProgress == 1) {
            leftRect.reset();
            leftRect.moveTo(mWidth / 2 - mRectWidth / 2, mHeight / 2 - mRectHeigth / 2);
            leftRect.lineTo(mWidth / 2 - mRectWidth / 4, mHeight / 2 - mRectHeigth / 2);
            leftRect.lineTo(mWidth / 2 - mRectWidth / 4, mHeight / 2 + mRectHeigth / 2);
            leftRect.lineTo(mWidth / 2 - mRectWidth / 2, mHeight / 2 + mRectHeigth / 2);
            leftRect.close();
            canvas.drawPath(leftRect, btnPaint);

            rightRect.reset();
            rightRect.moveTo(mWidth / 2 + mRectWidth / 2, mHeight / 2 - mRectHeigth / 2);
            rightRect.lineTo(mWidth / 2 + mRectWidth / 4, mHeight / 2 - mRectHeigth / 2);
            rightRect.lineTo(mWidth / 2 + mRectWidth / 4, mHeight / 2 + mRectHeigth / 2);
            rightRect.lineTo(mWidth / 2 + mRectWidth / 2, mHeight / 2 + mRectHeigth / 2);
            rightRect.close();
            canvas.drawPath(rightRect, btnPaint);
        } else {

            leftRect.reset();
            leftRect.moveTo(xLeft1, yLeft1);
            leftRect.lineTo(xLeft2, yLeft2);
            leftRect.lineTo(xLeft3, yLeft3);
            leftRect.lineTo(xLeft4, yLeft4);
            leftRect.close();

            rightRect.reset();
            rightRect.moveTo(xRight1, yRight1);
            rightRect.lineTo(xRight2, yRight2);
            rightRect.lineTo(xRight3, yRight3);
            rightRect.lineTo(xRight4, yRight4);
            rightRect.close();

            canvas.rotate((1 - mProgress) * -90, mWidth / 2, mHeight / 2);
            canvas.drawPath(leftRect, btnPaint);
            canvas.drawPath(rightRect, btnPaint);
        }
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
        startAnimation();
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    private void startAnimation() {
        ValueAnimator animator = ValueAnimator.ofFloat(isPlaying ? 0 : 1, isPlaying ? 1 : 0);
        animator.setDuration(300);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mProgress = (Float) animation.getAnimatedValue();
                invalidate();
            }
        });
        animator.start();
    }
}
