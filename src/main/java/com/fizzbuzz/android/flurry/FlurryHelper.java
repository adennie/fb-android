package com.fizzbuzz.android.flurry;

import com.fizzbuzz.android.activity.ActivityEvents.ActivityDestroyedEvent;
import com.fizzbuzz.android.activity.ActivityEvents.ActivityStartedEvent;
import com.fizzbuzz.android.activity.ActivityEvents.ActivityStoppedEvent;
import com.flurry.android.FlurryAgent;
import com.squareup.otto.OttoBus;
import com.squareup.otto.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * A helper class for Flurry integration. Call init() at application startup time, and in each activity's onCreate(),
 * instantiate an EventHandler and register it with the activity bus.
 */

public class FlurryHelper
{
    private static String mApiKey;

    // call this once at application startup time
    public static void init(final String flurryApiKey,
            final String appVersionName,
            final boolean handleUncaughtExceptions) {
        mApiKey = flurryApiKey;

        if (appVersionName != null) {
            FlurryAgent.setVersionName(appVersionName);
        }

        FlurryAgent.setCaptureUncaughtExceptions(handleUncaughtExceptions);
        FlurryAgent.setUseHttps(true);
    }

    public static class ActivityEventHandler {
        private final Logger mLogger = LoggerFactory.getLogger(LoggingManager.TAG);
        private final OttoBus mBus;

        @SuppressWarnings("unused")
        public static void registerWithBus(final OttoBus bus) {
            // no need to hold a reference; the bus will hold one until the handler auto-deregisters itself later
            new ActivityEventHandler(bus);
        }

        private ActivityEventHandler(final OttoBus bus) {
            mBus = bus;
            mBus.register(this);
        }

        @Subscribe
        public void onActivityStarted(ActivityStartedEvent event) {
            mLogger.debug("FlurryHelper$ActivityEventHandler.onActivityStarted: for activity {}", event.getActivity());
            FlurryAgent.onStartSession(event.getActivity(), mApiKey);
        }

        @Subscribe
        public void onActivityStopped(ActivityStoppedEvent event) {
            mLogger.debug("FlurryHelper$ActivityEventHandler.onActivityStopped: for activity {}", event.getActivity());
            FlurryAgent.onEndSession(event.getActivity());
        }

        @Subscribe
        public void onActivityDestroyed(ActivityDestroyedEvent event) {
            mLogger.debug("FlurryHelper$ActivityEventHandler.onActivityDestroyed: for activity {}", event.getActivity());
            mBus.unregister(this);
        }
    }
}
