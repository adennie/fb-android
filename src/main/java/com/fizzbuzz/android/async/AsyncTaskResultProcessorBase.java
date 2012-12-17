package com.fizzbuzz.android.async;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Activity;

public abstract class AsyncTaskResultProcessorBase<Result>
        implements AsyncTaskResultProcessor<Result> {
    private final Logger mLogger = LoggerFactory.getLogger(LoggingManager.TAG);
    private Activity mActivity;

    public AsyncTaskResultProcessorBase(Activity activity) {
        mActivity = activity;
    }

    @Override
    public void onActivityAttached(Activity activity) {
        mActivity = activity;
    };

    @Override
    public void onActivityDetached() {
        mActivity = null;
    }

    protected Activity getActivity() {
        return mActivity;
    }

    @Override
    public void processException(Exception e) {
        // default implementation - log and squelch
        mLogger.error("AsyncTaskResultProcessorBase:processException: squelching exception", e);
    }
}
