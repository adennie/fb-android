package com.fizzbuzz.android.async;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fizzbuzz.android.activity.ActivityEvents.ActivityDestroyedEvent;
import com.fizzbuzz.android.activity.ActivityEvents.ActivityPausedEvent;
import com.fizzbuzz.android.activity.ActivityEvents.ActivityResumedEvent;
import com.fizzbuzz.android.fragment.FragmentEvents.ActivityAttachedEvent;
import com.fizzbuzz.android.fragment.FragmentEvents.ActivityDetachedEvent;
import com.fizzbuzz.android.fragment.FragmentEvents.FragmentDestroyedEvent;
import com.fizzbuzz.android.fragment.FragmentEvents.FragmentPausedEvent;
import com.fizzbuzz.android.fragment.FragmentEvents.FragmentResumedEvent;
import com.squareup.otto.OttoBus;
import com.squareup.otto.Subscribe;

/*
 * This is a helper class to keep an AsyncTaskManager object synched up with the lifecycle of an Activity that uses
 * it.
 */
public class AsyncTaskManagerHelper {

    public static class ActivityLifecycleEventHandler {
        private final Logger mLogger = LoggerFactory.getLogger(LoggingManager.TAG);
        private final OttoBus mBus;
        private final AsyncTaskManager mAsyncTaskManager;

        @SuppressWarnings("unused")
        public static void registerWithBus(final OttoBus bus,
                final AsyncTaskManager asyncTaskMgr) {
            // no need to hold a reference; the bus will hold one until the handler auto-deregisters itself later
            new ActivityLifecycleEventHandler(bus, asyncTaskMgr);
        }

        private ActivityLifecycleEventHandler(OttoBus bus,
                AsyncTaskManager asyncTaskMgr) {

            // validate input
            mAsyncTaskManager = checkNotNull(asyncTaskMgr, "asyncTaskMgr");
            mBus = checkNotNull(bus, "bus");
        }

        @Subscribe
        public void onActivityResumed(final ActivityResumedEvent event) {
            mLogger.debug("AsyncTaskManager$ActivityEventHandler.onActivityResumed: for activity {}",
                    event.getActivity());
            checkState(mAsyncTaskManager != null, "no AsyncTaskManager attached");
            mAsyncTaskManager.onUiResume();
        }

        @Subscribe
        public void onActivityPaused(final ActivityPausedEvent event) {
            mLogger.debug("AsyncTaskManager$ActivityEventHandler.onActivityPaused: for activity {}",
                    event.getActivity());
            checkState(mAsyncTaskManager != null, "no AsyncTaskManager attached");
            mAsyncTaskManager.onUiPause();
        }

        @Subscribe
        public void onActivityDestroyed(final ActivityDestroyedEvent event) {
            mLogger.debug("AsyncTaskManager$ActivityEventHandler.onActivityDestroyed: for activity {}",
                    event.getActivity());
            checkState(mAsyncTaskManager != null, "no AsyncTaskManager attached");
            mAsyncTaskManager.onDestroy();
            mBus.unregister(this);
        }
    }

    /*
     * This is a helper class to keep an AsyncTaskManager object synched up with the lifecycle of a Fragment that uses
     * it.
     */

    public static class FragmentLifecycleEventHandler {
        private final Logger mLogger = LoggerFactory.getLogger(LoggingManager.TAG);
        private final AsyncTaskManager mAsyncTaskManager;
        private final OttoBus mBus;

        @SuppressWarnings("unused")
        public static void registerWithBus(final OttoBus bus,
                final AsyncTaskManager asyncTaskMgr) {
            // no need to hold a reference; the bus will hold one until the handler auto-deregisters itself later
            new FragmentLifecycleEventHandler(bus, asyncTaskMgr);
        }

        private FragmentLifecycleEventHandler(OttoBus bus,
                AsyncTaskManager asyncTaskMgr) {
            mAsyncTaskManager = checkNotNull(asyncTaskMgr, "asyncTaskMgr");
            mBus = checkNotNull(bus, "bus");
        }

        @Subscribe
        public void onActivityAttached(final ActivityAttachedEvent event) {
            mLogger.debug("AsyncTaskManager$FragmentEventHandler.onActivityAttached: for fragment {}",
                    event.getFragment());
            checkState(mAsyncTaskManager != null, "no AsyncTaskManager attached");
            mAsyncTaskManager.onActivityAttached(event.getFragment().getActivity());
        }

        @Subscribe
        public void onFragmentResumed(final FragmentResumedEvent event) {
            mLogger.debug("AsyncTaskManager$FragmentEventHandler.onFragmentResumed: for fragment {}",
                    event.getFragment());
            checkState(mAsyncTaskManager != null, "no AsyncTaskManager attached");
            mAsyncTaskManager.onUiResume();
        }

        @Subscribe
        public void onFragmentPaused(final FragmentPausedEvent event) {
            mLogger.debug("AsyncTaskManager$FragmentEventHandler.onFragmentPaused: for fragment {}",
                    event.getFragment());
            checkState(mAsyncTaskManager != null, "no AsyncTaskManager attached");
            mAsyncTaskManager.onUiPause();
        }

        @Subscribe
        public void onActivityDetached(final ActivityDetachedEvent event) {
            mLogger.debug("AsyncTaskManager$FragmentEventHandler.onActivityDetached: for fragment {}",
                    event.getFragment());
            checkState(mAsyncTaskManager != null, "no AsyncTaskManager attached");
            mAsyncTaskManager.onActivityDetached();
        }

        @Subscribe
        public void onFragmentDestroyed(final FragmentDestroyedEvent event) {
            mLogger.debug("AsyncTaskManager$FragmentEventHandler.onFragmentDestroyed: for fragment {}",
                    event.getFragment());
            checkState(mAsyncTaskManager != null, "no AsyncTaskManager attached");
            mAsyncTaskManager.onDestroy();
            mBus.unregister(this);
        }
    }

}
