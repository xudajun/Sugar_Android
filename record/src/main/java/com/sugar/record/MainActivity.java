package com.sugar.record;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.sugar.record.service.FloatService;
import com.sugar.record.service.RecordService;
import com.sugar.record.utils.FileUtils;
import com.sugar.record.utils.ImageUtils;
import com.sugar.record.utils.MediaRecordUtils;

public class MainActivity extends AppCompatActivity {

    private Button btn_start;
    private Button btn_start_media;
    private Button start;
    private Button stop;
    private ImageView img_screen;
    private MediaProjectionManager projectionManager;
    private Display display;
    private int mScreenDensity;
    private ImageReader mImageReader;
    private FloatService floatService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        img_screen = (ImageView) findViewById(R.id.img_screen);
        btn_start = (Button) findViewById(R.id.btn_start);
        btn_start_media = (Button) findViewById(R.id.btn_start_media);
        start = (Button) findViewById(R.id.start);
        stop = (Button) findViewById(R.id.stop);
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCapture();
                bindService(new Intent(MainActivity.this, FloatService.class), connection, Context.BIND_AUTO_CREATE);
            }
        });
        btn_start_media.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bindService(new Intent(MainActivity.this, RecordService.class), recordConnection, Context.BIND_AUTO_CREATE);
                startActivityForResult(projectionManager.createScreenCaptureIntent(), 101);
            }
        });
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordService.startRecord();
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordService.stop();
            }
        });
    }

    private void init() {
        projectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
        display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetric = new DisplayMetrics();
        display.getMetrics(outMetric);
        mScreenDensity = (int) outMetric.density;
    }

    private void startCapture() {
        startActivityForResult(projectionManager.createScreenCaptureIntent(), 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case 100:
                mImageReader = ImageReader.newInstance(display.getWidth(),
                        display.getHeight(), PixelFormat.RGBA_8888, 1);
                MediaProjection mediaProjection = projectionManager.getMediaProjection(resultCode, data);
                mediaProjection.createVirtualDisplay("screen_capture",
                        display.getWidth(),
                        display.getHeight(),
                        mScreenDensity,
                        DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                        mImageReader.getSurface(), null, null);
                break;
            case 101:
                try {
                    MediaRecordUtils mediaRecordUtils = new MediaRecordUtils();
                    mediaRecordUtils.init(display.getWidth(), display.getHeight());
                    MediaProjection mMediaProjection = projectionManager.getMediaProjection(resultCode, data);
                    mMediaProjection.createVirtualDisplay("-display",
                            display.getWidth(),
                            display.getHeight(),
                            mScreenDensity,
                            DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC,
                            mediaRecordUtils.getSurface(), null, null);
                    recordService.setMediaRecordUtils(mediaRecordUtils);
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
        }
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            floatService = ((FloatService.MessageBinder) service).getService();
            floatService.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    floatService.hideFloatingWindow();
                    handler.sendEmptyMessageDelayed(0, 1000);
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private RecordService recordService;

    private ServiceConnection recordConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            recordService = ((RecordService.RecordBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bitmap bitmap = ImageUtils.getBitmap(mImageReader);
            img_screen.setImageBitmap(bitmap);
            FileUtils.saveBitmap(bitmap);
            floatService.showFloatingWindow();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }
}
