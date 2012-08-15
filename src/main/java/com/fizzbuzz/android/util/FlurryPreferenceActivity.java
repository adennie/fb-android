package com.fizzbuzz.android.util;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class FlurryPreferenceActivity
        extends PreferenceActivity {

    private FlurryHelper mFlurryHelper;

    protected void onCreate(Bundle savedInstanceState,
            String flurryApiKey,
            boolean handleUncaughtExceptions) {
        super.onCreate(savedInstanceState);
        mFlurryHelper = new FlurryHelper(this, flurryApiKey, handleUncaughtExceptions);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFlurryHelper.detachActivity();
    }

    @Override
    protected void onStart() {
        mFlurryHelper.startSession();
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mFlurryHelper.endSession();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mFlurryHelper.startSession();
    }

}
