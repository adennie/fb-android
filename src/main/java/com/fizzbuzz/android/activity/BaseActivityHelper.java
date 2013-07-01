package com.fizzbuzz.android.activity;

import android.app.Activity;
import android.content.Intent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Just a little helper class to contain common code used by the various Base*Activity classes
public class BaseActivityHelper {

    public static void finishIfLaunchedOverNonRootTask(Activity activity) {
        Logger mLogger = LoggerFactory.getLogger(LoggingManager.TAG);
        // Possible work around for market launches. See http://code.google.com/p/android/issues/detail?id=2373
        // for more details. Essentially, the market launches the main activity on top of other activities.
        // we never want this to happen. Instead, we check if we are the root and if not, we finish.
        if (!activity.isTaskRoot()) {
            final Intent intent = activity.getIntent();
            final String intentAction = intent.getAction();
            if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && intentAction != null
                    && intentAction.equals(Intent.ACTION_MAIN)) {
                mLogger.warn("Main Activity is not the root.  Finishing Main Activity instead of launching.");
                activity.finish();
                return;
            }
        }
    }
}
