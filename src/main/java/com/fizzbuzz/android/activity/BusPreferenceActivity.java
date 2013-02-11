package com.fizzbuzz.android.activity;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.os.Bundle;

import com.fizzbuzz.android.activity.ActivityEvents.ActivityCreatedEvent;
import com.fizzbuzz.android.activity.ActivityEvents.ActivityDestroyedEvent;
import com.fizzbuzz.android.activity.ActivityEvents.ActivityInstanceStateRestoredEvent;
import com.fizzbuzz.android.activity.ActivityEvents.ActivityInstanceStateSavedEvent;
import com.fizzbuzz.android.activity.ActivityEvents.ActivityPausedEvent;
import com.fizzbuzz.android.activity.ActivityEvents.ActivityRestartedEvent;
import com.fizzbuzz.android.activity.ActivityEvents.ActivityResumedEvent;
import com.fizzbuzz.android.activity.ActivityEvents.ActivityStartedEvent;
import com.fizzbuzz.android.activity.ActivityEvents.ActivityStoppedEvent;
import com.fizzbuzz.ottoext.BusProvider;
import com.fizzbuzz.ottoext.MainThreadBus;
import com.fizzbuzz.ottoext.ScopedBus;
import com.squareup.otto.Bus;
import com.squareup.otto.OttoBus;
import com.squareup.otto.Produce;

public class BusPreferenceActivity
        extends BasePreferenceActivity {
    private final Logger mLogger = LoggerFactory.getLogger(LoggingManager.TAG);

    // the global bus is used for application-wide events. It's a MainThreadBus, so all events posted to it get
    // delivered on the main thread.
    private final OttoBus mGlobalBus = new MainThreadBus(BusProvider.getInstance());

    // the visible-scoped bus wraps the global bus, but is activated/deactivated as the Activity gets resumed/paused,
    // so that registered objects don't receive events while the Activity is not visible.
    private final ScopedBus mVisibleScopedBus = new ScopedBus(mGlobalBus);

    // the activity bus is a separate bus that allows objects to post, produce, and subscribe to events that are
    // specific to this Activity (e.g., lifecycle events posted by this class)
    private final ScopedBus mActivityBus = new ScopedBus(new Bus());

    private boolean mIsCreated = false; // indicates whether we've been through onCreate() yet.

    public final OttoBus getGlobalBus() {
        return mGlobalBus;
    }

    public final OttoBus getVisibleScopedBus() {
        return mVisibleScopedBus;
    }

    public final OttoBus getActivityBus() {
        return mActivityBus;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIsCreated = true;

        // register this Activity with the global bus
        mGlobalBus.register(this);

        // post a ActivityCreatedEvent to the Activity-specific bus for the benefit of current subscribers to that event
        mActivityBus.post(new ActivityCreatedEvent(this));

        // register a ActivityCreatedEventProducer with the Activity-specific bus for the benefit of future subscribers
        // to ActivityCreatedEven
        mActivityBus.register(new ActivityCreatedEventProducer());

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mActivityBus.post(new ActivityRestartedEvent(this));
    }

    @Override
    protected void onStart() {
        super.onStart();
        mVisibleScopedBus.activate();
        mActivityBus.post(new ActivityStartedEvent(this));
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mActivityBus.post(new ActivityInstanceStateRestoredEvent(this, savedInstanceState));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mVisibleScopedBus.activate();
        mActivityBus.post(new ActivityResumedEvent(this));
    }

    @Override
    protected void onPause() {
        mActivityBus.post(new ActivityPausedEvent(this));
        mVisibleScopedBus.deactivate();
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        mActivityBus.post(new ActivityInstanceStateSavedEvent(this, outState));
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStop() {
        mActivityBus.post(new ActivityStoppedEvent(this));
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mActivityBus.post(new ActivityDestroyedEvent(this));

        // at this point, all objects should be deregistered from the Fragment bus. If not, log an error to facilitate
        // investigation.
        Set<Object> registeredObjects = mActivityBus.getRegistrations();
        if (registeredObjects.size() != 0) {
            mLogger.warn(
                    "BusActivity.onDestroy: mActivityBus should not have any registered objects at this point, but it does: {}",
                    registeredObjects);
            mActivityBus.deactivate(); // to prevent memory leaks
        }
        super.onDestroy();
    }

    private class ActivityCreatedEventProducer {

        // This "producer" method is needed so that event handlers subscribed to ActivityCreatedEvent receive the event
        // even if they are registered with the bus AFTER the Activity's onCreate() executes, which is normally the
        // case.
        @Produce
        public ActivityCreatedEvent produceActivityCreated() {
            ActivityCreatedEvent result = null;
            if (mIsCreated)
                result = new ActivityCreatedEvent(BusPreferenceActivity.this);
            return result;
        }
    }
}
