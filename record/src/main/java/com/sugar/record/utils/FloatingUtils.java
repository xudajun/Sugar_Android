package com.sugar.record.utils;

import android.content.Context;
import android.view.WindowManager;

import com.sugar.record.widget.FloatingView;

/**
 * float window manager
 *
 * @author Sugar
 */
public class FloatingUtils {

    private static WindowManager mWindowManager;
    private static FloatingView floatingView;

    public static FloatingView createFloatingWindow(Context context) {
        mWindowManager = getWindowManager(context);
        floatingView = new FloatingView(context);
        WindowManager.LayoutParams params = floatingView.getWindowManagerParams();
        mWindowManager.addView(floatingView, params);
        return floatingView;
    }

    /**
     * 如果WindowManager还未创建，则创建一个新的WindowManager返回。否则返回当前已创建的WindowManager。
     *
     * @param context 必须为应用程序的Context.
     * @return WindowManager的实例，用于控制在屏幕上添加或移除悬浮窗。
     */
    private static WindowManager getWindowManager(Context context) {
        if (mWindowManager == null) {
            mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        }
        return mWindowManager;
    }
}
