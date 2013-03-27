package com.fizzbuzz.android.activity;

import android.app.Activity;
import android.os.Bundle;
import com.fizzbuzz.android.activity.ActivityEvents.*;
import com.fizzbuzz.android.activity.InjectingActivityModule.ActivityScoped;
import com.fizzbuzz.android.application.BusApplication;
import com.fizzbuzz.android.injection.Injector;
import com.fizzbuzz.ottoext.GuaranteedDeliveryBus;
import com.fizzbuzz.ottoext.GuaranteedDeliveryOttoBus;
import com.fizzbuzz.ottoext.ScopedGuaranteedDeliveryBus;
import com.squareup.otto.Produce;
import dagger.ObjectGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Set;

import static com.google.common.base.Preconditions.checkState;

public class BusActivityHelper {
    private final Logger mLogger = LoggerFactory.getLogger(LoggingManager.TAG);
    private final ActivityCreatedEventProducer mActivityCreatedEventProducer;

    // mActivityBus is an activity-scope-singleton, activation-scoped, guaranteed delivery, main thread bus.
    // The activation scope is the time between this Activity's onCreate and its onDestroy methods. This is sort of an
    // insurance policy that makes sure all objects registered to the activity bus get unregistered in onDestroy,
    // thereby eliminating potential extraneous references to the Activity that would keep it from being GC'ed.
    @Inject @ActivityScoped ScopedGuaranteedDeliveryBus mActivityBus;

    private Activity mActivity;

    // mVisibilityScopedApplicationBus wraps the application-scoped bus, but is activated/deactivated as the
    // Activity gets resumed/paused, so that registered objects don't receive events posted to the app-scope bus
    // while the Activity is not visible.
    private ScopedGuaranteedDeliveryBus mVisibilityScopedApplicationBus;

    private boolean mIsCreated = false; // indicates whether we've been through onCreate() yet.

    @Inject
    BusActivityHelper() {
        mActivityCreatedEventProducer = new ActivityCreatedEventProducer();
    }

    final GuaranteedDeliveryOttoBus getVisibilityScopedApplicationBus() {
        return mVisibilityScopedApplicationBus;
    }

    final GuaranteedDeliveryOttoBus getActivityBus() {
        return mActivityBus;
    }

    void onCreate(final Activity activity) {
        mActivity = activity;

        // inject mActivityBus using the activity-scope object graph
        ObjectGraph activityObjectGraph = ((Injector)mActivity).getObjectGraph();
        checkState(activityObjectGraph != null,
                "InjectingActivity's object graph must be initialized prior to calling onCreate");
        activityObjectGraph.inject(this);

        mActivityBus.activate();

        mVisibilityScopedApplicationBus = new ScopedGuaranteedDeliveryBus(getApplicationBus());

        // post an ActivityCreatedEvent to the Activity bus for the benefit of current subscribers to that event
        mActivityBus.post(new ActivityCreatedEvent(mActivity));

        // register a ActivityCreatedEventProducer with the Activity bus for the benefit of future subscribers
        // to ActivityCreatedEvent
        mActivityBus.register(mActivityCreatedEventProducer);

        mIsCreated = true;
    }

    void onRestart() {
        mActivityBus.post(new ActivityRestartedEvent(mActivity));
    }

    void onStart() {
        mActivityBus.post(new ActivityStartedEvent(mActivity));
    }

    void onRestoreInstanceState(Bundle savedInstanceState) {
        mActivityBus.post(new ActivityInstanceStateRestoredEvent(mActivity, savedInstanceState));
    }

    void onResume() {
        mVisibilityScopedApplicationBus.activate();
        mActivityBus.post(new ActivityResumedEvent(mActivity));
    }

    void onPause() {
        mActivityBus.post(new ActivityPausedEvent(mActivity));
        mVisibilityScopedApplicationBus.deactivate();
    }

    void onSaveInstanceState(Bundle outState) {
        mActivityBus.post(new ActivityInstanceStateSavedEvent(mActivity, outState));
    }

    void onStop() {
        mActivityBus.post(new ActivityStoppedEvent(mActivity));
    }

    void onDestroy() {
        mActivityBus.post(new ActivityDestroyedEvent(mActivity));
        mActivityBus.unregister(mActivityCreatedEventProducer);

        // at this point, all objects should be deregistered from the Activity bus. If not, log an error to facilitate
        // investigation.
        Set<Object> registeredObjects = mActivityBus.getRegistrants();
        if (registeredObjects.size() != 0) {
            mLogger.warn(
                    "BusActivity.onDestroy: mLifecycleScopedActivityBus should not have any registered objects at this point, but it does: {}",
                    registeredObjects);
            mActivityBus.deactivate(); // to prevent memory leaks
        }

        getApplicationBus().unregister(this);

        mIsCreated = false; // disable the ActivityCreatedEventProducer
    }

    private GuaranteedDeliveryBus getApplicationBus() {
        return ((BusApplication) mActivity.getApplication()).getApplicationBus();
    }

    private class ActivityCreatedEventProducer {

        // This "producer" method is needed so that event handlers subscribed to ActivityCreatedEvent receive the event
        // even if they are registered with the bus AFTER the Activity's onCreate() executes, which is normally the
        // case.
        @Produce
        public ActivityCreatedEvent produceActivityCreated() {
            ActivityCreatedEvent result = null;
            if (mIsCreated)
                result = new ActivityCreatedEvent(mActivity);
            return result;
        }
    }
}
