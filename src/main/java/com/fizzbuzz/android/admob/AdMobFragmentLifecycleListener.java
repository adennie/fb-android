package com.fizzbuzz.android.admob;

import static com.google.common.base.Preconditions.checkNotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.view.ViewGroup;

import com.fizzbuzz.android.fragment.AbstractFragmentLifecycleListener;
import com.google.ads.AdView;

/*
 * This helper class listens to an activity's lifecycle events and interacts with the AdMob API appropriately.
 * In particular, it calls destroy() on the AdView object to clean it up.
 * WARNING: do not use this helper class if the AdView is not tied to the lifecycle of the activity. If it is tied to an instanced-retained Fragment,
 * for example, then you wouldn't want to destroy the AdView when the Activity goes away.
 */

/* Activities using this class MUST override onDestroy and call the corresponding method on this class */
public class AdMobFragmentLifecycleListener
        extends AbstractFragmentLifecycleListener
{
    private final Logger mLogger = LoggerFactory.getLogger(LoggingManager.TAG);
    private AdView mAdView;

    public AdMobFragmentLifecycleListener(
            AdView adView) {
        mAdView = adView;
    }

    // This class assumes that the creation of the AdView happens in fragment-specific code, so it isn't done here. However,
    // cleaning up the Adview when the Activity is detached is generic, and can be handled here.

    @Override
    public void onDetach() {
        checkNotNull(mAdView, "mAdView");
        try {
            mLogger.info("AdMobFragmentLifecycleListener.onDetach: destroying AdView");
            mAdView.destroy();
            ViewGroup parent = (ViewGroup) mAdView.getParent();
            parent.removeAllViews();
        }
        catch (RuntimeException e) {
            mLogger.error(
                    "AdMobFragmentLifecycleListener.onDetach: caught Exception (sometimes happens in WebView).  Squelching to avoid problems.",
                    e);
        }
        finally {
            mAdView = null;
        }
        super.onDetach();
    }

}
