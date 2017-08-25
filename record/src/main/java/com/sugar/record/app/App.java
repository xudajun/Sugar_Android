package com.sugar.record.app;

import android.app.Application;

import com.sugar.record.utils.FileUtils;

/**
 * @author Suagr
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FileUtils.init();
    }
}
