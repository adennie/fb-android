package com.fizzbuzz.android.application;

import android.app.Application;

import com.fizzbuzz.android.util.VersionedStrictModeWrapper;

public class ApplicationBase
        extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // initialize strict mode
        VersionedStrictModeWrapper.getInstance().init(this);

        // make sure AsyncTask's static members get initialized on a UI thread (http://code.google.com/p/android/issues/detail?id=20915)
        try {
            Class.forName("android.os.AsyncTask");
        }
        catch (ClassNotFoundException e) {
        }
    }

    // convenience method, analogous to getString
    public final int getInteger(final int resId) {
        return getResources().getInteger(resId);
    }

}
