package com.sugar.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

import java.util.List;

/**
 * Android 滚轮控件
 *
 * @author Sugar
 */
public class WheelView extends ViewGroup {

    private List<String> data;

    public WheelView(Context context) {
        super(context);
    }

    public WheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WheelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
