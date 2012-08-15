package com.fizzbuzz.android.util;

import static com.google.common.base.Preconditions.checkState;
import android.app.Activity;

import com.flurry.android.FlurryAgent;

public class FlurryHelper {
    private Activity mActivity;
    private final String mApiKey;
    private final boolean mHandleUncaughtExceptions;
    private final String mAppVersionName;

    public FlurryHelper(final Activity activity,
            final String apiKey,
            final boolean handleUncaughtExceptions,
            final String appVersionName /* null OK */) {
        mActivity = activity;
        mApiKey = apiKey;
        mHandleUncaughtExceptions = handleUncaughtExceptions;
        mAppVersionName = appVersionName;

        FlurryAgent.setCaptureUncaughtExceptions(mHandleUncaughtExceptions);

        if (mAppVersionName != null) {
            FlurryAgent.setVersionName(mAppVersionName);
        }

        FlurryAgent.setUseHttps(true);

    }

    public FlurryHelper(final Activity activity,
            final String apiKey,
            final boolean handleUncaughtExceptions) {
        this(activity, apiKey, handleUncaughtExceptions, null);
    }

    public FlurryHelper(final Activity activity,
            final String apiKey) {
        this(activity, apiKey, false);
    }

    public void attachActivity(final Activity activity) {
        mActivity = activity;
    }

    public void detachActivity() {
        mActivity = null;
    }

    public void startSession() {
        checkState(mActivity != null, "no activity attached");
        FlurryAgent.onStartSession(mActivity, mApiKey);
    }

    public void endSession() {
        checkState(mActivity != null, "no activity attached");
        FlurryAgent.onEndSession(mActivity);
    }

}
