package com.debdroid.popularmovies.application;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Created by debashispaul on 26/02/2018.
 */

public class MyApplication extends Application {
    public void onCreate() {
        super.onCreate();
        // Enable Stetho
        Stetho.initializeWithDefaults(this);
    }
}
