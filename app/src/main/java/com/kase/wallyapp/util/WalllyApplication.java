package com.kase.wallyapp.util;

import android.app.Application;

import com.unsplash.pickerandroid.photopicker.UnsplashPhotoPicker;

public class WalllyApplication extends Application {

    private Config config;

    @Override
    public void onCreate() {
        super.onCreate();
        config = new Config();
        UnsplashPhotoPicker.INSTANCE.init(this, config.getAccessKey(), config.getSecretKey(), 10);
    }
}
