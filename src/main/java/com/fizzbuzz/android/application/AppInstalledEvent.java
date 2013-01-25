package com.fizzbuzz.android.application;

public class AppInstalledEvent {

    private final int mAppVersionCode;

    public AppInstalledEvent(final int appVersionCode) {
        mAppVersionCode = appVersionCode;
    }

    public int getAppVersionCode() {
        return mAppVersionCode;
    }
}
