package com.sugar.record.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.sugar.record.R;
import com.sugar.record.utils.MediaRecordUtils;

/**
 * @author Sugar
 */
public class RecordService extends Service {

    private MediaRecordUtils mediaRecordUtils;
    private RemoteViews mRemoteViews;
    private NotificationCompat.Builder mBuilder;
    private NotificationManager mNotificationManager;
    private final static int NOTIFICATION_ID = 11003;
    private boolean isStart = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new RecordBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initNotification();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!intent.getBooleanExtra("status", false)) {
            startRecord();
        } else {
            stop();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void initNotification() {
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(this);
        mRemoteViews = new RemoteViews(getPackageName(), R.layout.notification_capture);
        mRemoteViews.setTextViewText(R.id.tv_title, "屏幕录制");
        mRemoteViews.setImageViewResource(R.id.img_status, R.mipmap.btn_record);
        Intent intent = new Intent(this, RecordService.class);
        intent.putExtra("status", isStart);
        PendingIntent pendingIntent = PendingIntent.getService(this, 2, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.img_status, pendingIntent);
        mBuilder.setContent(mRemoteViews);
        mBuilder.setSmallIcon(R.mipmap.icon_notification);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    private void refreshNotification() {
        if (!isStart) {
            mRemoteViews.setTextViewText(R.id.tv_title, "屏幕录制");
            mRemoteViews.setImageViewResource(R.id.img_status, R.mipmap.btn_record);
        } else {
            mRemoteViews.setTextViewText(R.id.tv_title, "屏幕录制中...");
            mRemoteViews.setImageViewResource(R.id.img_status, R.mipmap.btn_recording);
        }
        Intent intent = new Intent(this, RecordService.class);
        intent.putExtra("status", isStart);
        PendingIntent pendingIntent = PendingIntent.getService(this, 2, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.img_status, pendingIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    public void startRecord() {
        isStart = true;
        refreshNotification();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mediaRecordUtils.startRecord();
                } catch (Exception e) {

                }
            }
        }).start();
    }

    public void stop() {
        isStart = false;
        refreshNotification();
        mediaRecordUtils.stop();
    }

    public void setMediaRecordUtils(MediaRecordUtils mediaRecordUtils) {
        this.mediaRecordUtils = mediaRecordUtils;
    }

    public class RecordBinder extends Binder {
        public RecordService getService() {
            return RecordService.this;
        }
    }
}
