package com.fizzbuzz.android.activity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class BasePreferenceActivity
        extends PreferenceActivity {
    private final Logger mLogger = LoggerFactory.getLogger(LoggingManager.TAG);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        // Possible work around for market launches. See http://code.google.com/p/android/issues/detail?id=2373
        // for more details. Essentially, the market launches the main activity on top of other activities.
        // we never want this to happen. Instead, we check if we are the root and if not, we finish.
        if (!isTaskRoot()) {
            final Intent intent = getIntent();
            final String intentAction = intent.getAction();
            if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && intentAction != null && intentAction.equals(Intent.ACTION_MAIN)) {
                mLogger.warn("Main Activity is not the root.  Finishing Main Activity instead of launching.");
                finish();
                return;
            }
        }
    }
}
