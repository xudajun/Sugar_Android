package com.sugar.record.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.WindowManager;

import com.sugar.record.utils.FloatingUtils;
import com.sugar.record.widget.FloatingView;

/**
 * @author ArJun
 */
public class FloatService extends Service {

    private WindowManager windowManager;
    private FloatingView floatingView;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MessageBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        floatingView = FloatingUtils.createFloatingWindow(this);
    }

    public class MessageBinder extends Binder {
        public FloatService getService() {
            return FloatService.this;
        }
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        floatingView.setActionListener(onClickListener);
    }

    public void hideFloatingWindow() {
        floatingView.setVisibility(View.GONE);
    }

    public void showFloatingWindow() {
        floatingView.setVisibility(View.VISIBLE);
    }
}
