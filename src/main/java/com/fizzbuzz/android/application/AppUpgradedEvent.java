package com.fizzbuzz.android.application;

public class AppUpgradedEvent {

    private final int mCurrentAppVersionCode;
    private final int mPreviousAppVersionCode;

    public AppUpgradedEvent(final int currentAppVersionCode,
            final int previousAppVersionCode) {
        mCurrentAppVersionCode = currentAppVersionCode;
        mPreviousAppVersionCode = previousAppVersionCode;
    }

    public int getCurrentAppVersionCode() {
        return mCurrentAppVersionCode;
    }

    public int getPreviousAppVersionCode() {
        return mPreviousAppVersionCode;
    }
}
