package com.fizzbuzz.android.flurry;

import android.app.Activity;

import com.fizzbuzz.android.activity.ActivityLifecycleListenerBase;
import com.flurry.android.FlurryAgent;

/* Activities using this class MUST override onStart, onStop, onRestart, AND onDestroy and call the corresponding methods on this class */
public class FlurryHelper
        extends ActivityLifecycleListenerBase {
    private static String mApiKey;

    public FlurryHelper(Activity activity) {
        super(activity);

    }

    // call this once at application startup time
    public static void initApp(String flurryApiKey,
            final String appVersionName,
            boolean handleUncaughtExceptions) {
        mApiKey = flurryApiKey;

        if (appVersionName != null) {
            FlurryAgent.setVersionName(appVersionName);
        }

        FlurryAgent.setCaptureUncaughtExceptions(handleUncaughtExceptions);
        FlurryAgent.setUseHttps(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        FlurryAgent.onStartSession(getActivity(), mApiKey);
    }

    @Override
    public void onStop() {
        FlurryAgent.onEndSession(getActivity());
        super.onStop();
    }

}
