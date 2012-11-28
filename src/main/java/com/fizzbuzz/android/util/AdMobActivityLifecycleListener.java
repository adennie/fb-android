package com.fizzbuzz.android.util;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import com.google.ads.AdView;

/*
 * This helper class listens to an activity's lifecycle events and interacts with the AdMob API appropriately.
 * In particular, it calls destroy() on the AdView object to clean it up.
 * WARNING: do not use this helper class if the AdView is not tied to the lifecycle of the activity. If it is tied to an instanced-retained Fragment,
 * for example, then you wouldn't want to destroy the AdView when the Activity goes away.
 */

/* Activities using this class MUST override onDestroy and call the corresponding method on this class */
public class AdMobActivityLifecycleListener
        extends ActivityLifecycleListenerImpl
{
    private final Logger mLogger = LoggerFactory.getLogger(LoggingManager.TAG);
    private AdView mAdView;

    public AdMobActivityLifecycleListener(Activity activity,
            View adView) {
        super(activity);

        // validate input
        checkNotNull(activity, "activity");
        checkArgument(adView instanceof AdView, "adView must be instance of com.google.ads.AdView");

        mAdView = (AdView) adView;
    }

    // This class assumes that the creation of the AdView happens in activity-specific or fragment-specific code, so it isn't done here. However,
    // cleaning up the Adview when the Activity is stopped is generic, and can be handled here.

    @Override
    public void onStop() {
        if (mAdView != null) {
            try {
                mLogger.info("AdMobHelper.onStop: destroying AdView");
                mAdView.destroy();
                ViewGroup parent = (ViewGroup) mAdView.getParent();
                parent.removeAllViews();
            }
            catch (RuntimeException e) {
                mLogger.error("AdMobHelper.onStop: caught Exception (sometimes happens in WebView).  Squelching to avoid problems.", e);

            }
            finally {
                mAdView = null;
            }
        }
        super.onStop();
    }

}
