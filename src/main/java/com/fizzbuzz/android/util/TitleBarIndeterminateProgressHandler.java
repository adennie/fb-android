package com.fizzbuzz.android.util;

import android.app.Activity;

public class TitleBarIndeterminateProgressHandler
        extends BaseProgressListener {

    private final Activity mActivity;

    public TitleBarIndeterminateProgressHandler(final Activity activity) {
        mActivity = activity;
    }

    @Override
    public void onStartProgress(final String message1, final String message2) {
        mActivity.setProgressBarIndeterminateVisibility(true);
    }

    @Override
    public void onUpdateProgress(final int progress, final String message1, final String message2) {
    }

    @Override
    public void onEndProgress() {
        mActivity.setProgressBarIndeterminateVisibility(false);
    }

    @Override
    public void onUiPause() {
    }

    @Override
    public void onUiResume() {
    }

}
