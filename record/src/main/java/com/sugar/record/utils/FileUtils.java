package com.sugar.record.utils;

import android.graphics.Bitmap;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;

/**
 * @author Sugar
 */
public class FileUtils {

    /**
     * sd卡的缓存路径
     */
    public static final String CACHE_IMAGE_PATH = "/record/image/";
    public static final String CACHE_MEDIA_PATH = "/record/media/";

    /**
     * 获取缓存路径
     *
     * @return
     */
    public static String getRootCachePath() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return Environment.getExternalStorageDirectory().getAbsolutePath(); // filePath:  /sdcard/
        } else {
            return Environment.getDataDirectory().getAbsolutePath() + "data"; // filePath:  /data/data/
        }
    }

    public static void init() {
        String mRootDir = getRootCachePath();
        String mAppRootDir = mRootDir + "/" + CACHE_IMAGE_PATH;
        File appDir = new File(mAppRootDir);
        if (!appDir.exists()) {
            appDir.mkdirs();
        }
        String mMediaRootDir = mRootDir + "/" + CACHE_MEDIA_PATH;
        File mediaDir = new File(mMediaRootDir);
        if (!mediaDir.exists()) {
            mediaDir.mkdirs();
        }
    }

    public static void saveBitmap(Bitmap mBitmap) {
        try {
            String name = "capture" + System.currentTimeMillis();
            File file = new File(Environment.getExternalStorageDirectory().getPath()
                    + CACHE_IMAGE_PATH + name + ".jpg");
            FileOutputStream out = new FileOutputStream(file);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取缓存路径
     *
     * @return
     */
    public static String getMediaCachePath() {
        return Environment.getExternalStorageDirectory().getPath()
                + CACHE_MEDIA_PATH;
    }
}
