package com.fizzbuzz.android.async;

import android.app.Activity;

public class TitleBarIndeterminateProgressHandler
        implements ProgressListener {

    private Activity mActivity;

    public TitleBarIndeterminateProgressHandler(final Activity activity) {
        mActivity = activity;
    }

    @Override
    public void onStartProgress(final String message1,
            final String message2) {
        if (mActivity != null)
            mActivity.setProgressBarIndeterminateVisibility(true);
    }

    @Override
    public void onUpdateProgress(final int progress,
            final String message1,
            final String message2) {
    }

    @Override
    public void onEndProgress() {
        if (mActivity != null)
            mActivity.setProgressBarIndeterminateVisibility(false);
    }

    @Override
    public void onUiPause() {
    }

    @Override
    public void onUiResume() {
    }

    @Override
    public void onActivityAttached(Activity activity) {
        mActivity = activity;

    }

    @Override
    public void onActivityDetached() {
        mActivity = null;
    }
}
