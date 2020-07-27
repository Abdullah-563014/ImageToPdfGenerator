package com.srteam.fsr.fastscanner;

import android.app.Application;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class FastScannerApplication extends Application {


    private static FastScannerApplication application;
    private ExecutorService executorService = Executors.newCachedThreadPool();

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public static FastScannerApplication getApplication() {
        return application;
    }
}
