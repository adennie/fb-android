package com.fizzbuzz.android.admob;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.fizzbuzz.android.fragment.FragmentEvents.FragmentViewDestroyedEvent;
import com.fizzbuzz.android.util.NetworkHelper;
import com.fizzbuzz.android.util.StrictModeWrapper;
import com.google.ads.AdView;
import com.squareup.otto.OttoBus;
import com.squareup.otto.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/*
 * This helper class listens to a Fragment's lifecycle events and interacts with the AdMob API appropriately.
 * In particular, it calls destroy() on the AdView object to clean it up when the associated Fragment's activity gets
 * detached.
 */
public class AdMobHelper {

    @Inject StrictModeWrapper mStrictMode;

    public void createAdView(final Context context,
            final ViewGroup viewGroup,
            final LayoutInflater inflater,
            final int layoutResId,
            final OttoBus bus) {

        // if we have a network connection
        if (NetworkHelper.isConnected(context)) {
            // create an AdView
            if (viewGroup != null) {
                AdView adView = (AdView) mStrictMode.inflateWithStrictModeOverride(inflater,
                        layoutResId, null, false);
                viewGroup.addView(adView);

                // register an event handler to coordinate Fragment lifecycle events with the AdView
                FragmentEventHandler.registerWithBus(bus, adView);
            }
        }
    }

    private static class FragmentEventHandler
    {
        private final Logger mLogger = LoggerFactory.getLogger(LoggingManager.TAG);
        private final OttoBus mBus;
        private AdView mAdView;

        @SuppressWarnings("unused")
        public static void registerWithBus(final OttoBus bus,
                final AdView adView) {
            // no need to hold a reference; the bus will hold one until the handler auto-deregisters itself later
            new FragmentEventHandler(bus, adView);
        }

        private FragmentEventHandler(final OttoBus bus,
                AdView adView) {
            mBus = bus;
            mBus.register(this);
            mAdView = adView;

        }

        // This class assumes that the creation of the AdView happens in fragment-specific code, so it isn't done here.
        // However, cleaning up the Adview when the Fragment's View is destroyed is generic, and can be handled here.

        @Subscribe
        public void onFragmentViewDestroyed(@SuppressWarnings("unused") FragmentViewDestroyedEvent event) {
            checkNotNull(mAdView, "mAdView");
            mLogger.debug("AdMobFragmentEventHandler.onFragmentViewDestroyed: destroying AdView");
            try {
                mAdView.destroy();
                ViewGroup parent = (ViewGroup) mAdView.getParent();
                parent.removeAllViews();

                // now that the view is destroyed, we can unregister this handler
                mBus.unregister(this);
            } catch (RuntimeException e) {
                mLogger.error(
                        "AdMobFragmentEventHandler.onFragmentViewDestroyed: caught Exception (sometimes happens in WebView).  Squelching to avoid problems.",
                        e);
            } finally {
                mAdView = null;
            }
        }
    }
}
