package com.sugar.record.widget;

import android.content.Context;
import android.graphics.PixelFormat;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.sugar.record.R;

import java.lang.reflect.Field;

/**
 * custom floating window
 *
 * @author Sugar
 */
public class FloatingView extends LinearLayout {

    private WindowManager windowManager;
    private WindowManager.LayoutParams mParams;
    private int mWidth;
    private int mHeight;
    private ImageView btn_record;
    private int statusBarHeight;
    private View content;

    public FloatingView(Context context) {
        this(context, null);
    }

    public FloatingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        mParams = new WindowManager.LayoutParams();
        mParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        mParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        LayoutInflater.from(getContext()).inflate(R.layout.window_floating, this);
        mParams.gravity = Gravity.RIGHT | Gravity.BOTTOM;
        mParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.format = PixelFormat.TRANSPARENT;
        btn_record = (ImageView) findViewById(R.id.btn_record);
        getStatusBarHeight();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (content == null) {
            content = getChildAt(0);
        }
        mWidth = content.getMeasuredWidth();
        mHeight = content.getMeasuredHeight();
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                updateFloatingPosition(event.getRawX() - event.getX(), event.getRawY() - statusBarHeight - event.getY());
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * @param mParams
     */
    public void setWindowManagerParams(WindowManager.LayoutParams mParams) {
        this.mParams = mParams;
    }

    public WindowManager.LayoutParams getWindowManagerParams() {
        return mParams;
    }

    /**
     * 更新悬浮窗位置
     *
     * @param x
     * @param y
     */
    public void updateFloatingPosition(float x, float y) {
        mParams.x = (int) x;
        mParams.y = (int) y;
        windowManager.updateViewLayout(this, mParams);
    }

    /**
     * 用于获取状态栏的高度。
     *
     * @return 返回状态栏高度的像素值。
     */
    private int getStatusBarHeight() {
        if (statusBarHeight == 0) {
            try {
                Class<?> c = Class.forName("com.android.internal.R$dimen");
                Object o = c.newInstance();
                Field field = c.getField("status_bar_height");
                int x = (Integer) field.get(o);
                statusBarHeight = getResources().getDimensionPixelSize(x);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return statusBarHeight;
    }

    public void setActionListener(OnClickListener actionListener) {
        btn_record.setOnClickListener(actionListener);
    }
}
