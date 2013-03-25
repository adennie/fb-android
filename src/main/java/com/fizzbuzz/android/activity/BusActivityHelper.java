package com.fizzbuzz.android.activity;

import android.app.Activity;
import android.os.Bundle;
import com.fizzbuzz.android.activity.ActivityEvents.*;
import com.fizzbuzz.android.activity.ActivityModule.ActivityScoped;
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
    // mActivityBus is an injected GuaranteedDeliveryBus that wraps a MainThreadBus, and is a singleton within the
    // context of this activity's object graph. It lets objects post, produce, and subscribe to events that are
    // specific to this Activity (e.g., activity lifecycle events posted by this class).
    @Inject @ActivityScoped GuaranteedDeliveryBus mActivityBus;

    private Activity mActivity;

    // the visibility-scoped application bus wraps the application-scoped bus, but is activated/deactivated as the
    // Activity gets resumed/paused, so that registered objects don't receive events posted to the app-scope bus
    // while the Activity is not visible.
    private ScopedGuaranteedDeliveryBus mVisibilityScopedApplicationBus;
    // the lifecycle-scoped activity bus wraps mActivityBus with a ScopedBus -- the scope in this case is the time
    // between this Activity's onCreate and its onDestroy methods. This is sort of an insurance policy that makes sure
    // all objects registered to the activity bus get unregistered in onDestroy, thereby eliminating potential
    // extraneous references to the Activity that would keep it from being GC'ed.
    private ScopedGuaranteedDeliveryBus mLifecycleScopedActivityBus;
    private boolean mIsCreated = false; // indicates whether we've been through onCreate() yet.

    @Inject
    BusActivityHelper() {
        mActivityCreatedEventProducer = new ActivityCreatedEventProducer();
    }

    final GuaranteedDeliveryOttoBus getVisibilityScopedApplicationBus() {
        return mVisibilityScopedApplicationBus;
    }

    final GuaranteedDeliveryOttoBus getActivityBus() {
        return mLifecycleScopedActivityBus;
    }

    void onCreate(final Activity activity) {
        mActivity = activity;

        // inject mActivityBus using the activity-scope object graph
        ObjectGraph activityObjectGraph = ((Injector) mActivity).getObjectGraph();
        checkState(activityObjectGraph != null,
                "BusActivityHelper's object graph must be assigned prior to calling onCreate");
        activityObjectGraph.inject(this);

        mLifecycleScopedActivityBus = new ScopedGuaranteedDeliveryBus(mActivityBus);
        mLifecycleScopedActivityBus.activate();

        mVisibilityScopedApplicationBus = new ScopedGuaranteedDeliveryBus(getApplicationBus());

        // post an ActivityCreatedEvent to the Activity bus for the benefit of current subscribers to that event
        mLifecycleScopedActivityBus.post(new ActivityCreatedEvent(mActivity));

        // register a ActivityCreatedEventProducer with the Activity bus for the benefit of future subscribers
        // to ActivityCreatedEvent
        mLifecycleScopedActivityBus.register(mActivityCreatedEventProducer);

        mIsCreated = true;
    }

    void onRestart() {
        mLifecycleScopedActivityBus.post(new ActivityRestartedEvent(mActivity));
    }

    void onStart() {
        mLifecycleScopedActivityBus.post(new ActivityStartedEvent(mActivity));
    }

    void onRestoreInstanceState(Bundle savedInstanceState) {
        mLifecycleScopedActivityBus.post(new ActivityInstanceStateRestoredEvent(mActivity, savedInstanceState));
    }

    void onResume() {
        mVisibilityScopedApplicationBus.activate();
        mLifecycleScopedActivityBus.post(new ActivityResumedEvent(mActivity));
    }

    void onPause() {
        mLifecycleScopedActivityBus.post(new ActivityPausedEvent(mActivity));
        mVisibilityScopedApplicationBus.deactivate();
    }

    void onSaveInstanceState(Bundle outState) {
        mLifecycleScopedActivityBus.post(new ActivityInstanceStateSavedEvent(mActivity, outState));
    }

    void onStop() {
        mLifecycleScopedActivityBus.post(new ActivityStoppedEvent(mActivity));
    }

    void onDestroy() {
        mLifecycleScopedActivityBus.post(new ActivityDestroyedEvent(mActivity));
        mLifecycleScopedActivityBus.unregister(mActivityCreatedEventProducer);

        // at this point, all objects should be deregistered from the Activity bus. If not, log an error to facilitate
        // investigation.
        Set<Object> registeredObjects = mLifecycleScopedActivityBus.getRegistrants();
        if (registeredObjects.size() != 0) {
            mLogger.warn(
                    "BusActivity.onDestroy: mLifecycleScopedActivityBus should not have any registered objects at this point, but it does: {}",
                    registeredObjects);
            mLifecycleScopedActivityBus.deactivate(); // to prevent memory leaks
        }

        getApplicationBus().unregister(this);
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
