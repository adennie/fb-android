package com.fizzbuzz.android.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import com.fizzbuzz.android.fragment.FragmentEvents.ActivityAttachedEvent;
import com.fizzbuzz.android.fragment.FragmentEvents.ActivityCreatedEvent;
import com.fizzbuzz.android.fragment.FragmentEvents.ActivityDetachedEvent;
import com.fizzbuzz.android.fragment.FragmentEvents.FragmentCreatedEvent;
import com.fizzbuzz.android.fragment.FragmentEvents.FragmentDestroyedEvent;
import com.fizzbuzz.android.fragment.FragmentEvents.FragmentPausedEvent;
import com.fizzbuzz.android.fragment.FragmentEvents.FragmentResumedEvent;
import com.fizzbuzz.android.fragment.FragmentEvents.FragmentStartedEvent;
import com.fizzbuzz.android.fragment.FragmentEvents.FragmentStoppedEvent;
import com.fizzbuzz.android.fragment.FragmentEvents.FragmentViewCreatedEvent;
import com.fizzbuzz.android.fragment.FragmentEvents.FragmentViewDestroyedEvent;
import com.fizzbuzz.android.dagger.InjectingFragmentModule;
import com.fizzbuzz.android.dagger.Injector;
import com.fizzbuzz.ottoext.GuaranteedDeliveryOttoBus;
import com.fizzbuzz.ottoext.ScopedGuaranteedDeliveryBus;
import com.squareup.otto.Produce;
import dagger.ObjectGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Set;

import static com.google.common.base.Preconditions.checkState;

public class BusFragmentHelper {

    private final Logger mLogger = LoggerFactory.getLogger(LoggingManager.TAG);
    // mFragmentBus is a fragment-scope-singleton, activation-scoped, guaranteed delivery, main thread bus.
    // The activation scope is the time between this Fragment's onCreate and its onDestroy methods. This is sort of an
    // insurance policy that makes sure all objects registered to the fragment bus get unregistered in onDestroy,
    // thereby eliminating potential extraneous references to the Fragment that would keep it from being GC'ed.
    @Inject @InjectingFragmentModule.Fragment ScopedGuaranteedDeliveryBus mFragmentBus;
    private android.support.v4.app.Fragment mFragment;
    private boolean mIsFirstAttach = true;
    private Activity mActivity;
    private boolean mIsCreated = false; // indicates whether we've been through onCreate() yet.

    final GuaranteedDeliveryOttoBus getFragmentBus() {
        return mFragmentBus;
    }

    public void onAttach(android.support.v4.app.Fragment fragment, Activity activity) {
        if (mIsFirstAttach) {
            mFragment = fragment;

            // inject ourselves using the fragment-scope object graph
            ObjectGraph fragmentObjectGraph = ((Injector) mFragment).getObjectGraph();
            checkState(fragmentObjectGraph != null,
                    "InjectingFragment's object graph must be initialized prior to calling onCreate");
            fragmentObjectGraph.inject(this);

            // activate and egister with the Fragment-specific bus so that this class' producer methods get called
            // when subscribers register
            mFragmentBus.activate();
            mFragmentBus.register(this);

            mIsFirstAttach = false;
        }

        mActivity = activity; // enable ActivityAttachedEvent producer method

        mFragmentBus.post(new ActivityAttachedEvent(mFragment, activity));

    }

    public void onCreate() {
        mFragmentBus.post(new FragmentCreatedEvent(mFragment));

        mIsCreated = true; // enable FragmentCreatedEvent producer method
    }

    public void onViewCreated(final View view) {
        mFragmentBus.post(new FragmentViewCreatedEvent(mFragment, view));
    }

    public void onActivityCreated(final Bundle savedInstanceState) {
        mFragmentBus.post(new ActivityCreatedEvent(mFragment, savedInstanceState));
    }

    public void onStart() {
        mFragmentBus.post(new FragmentStartedEvent(mFragment));
    }

    public void onResume() {
        mFragmentBus.post(new FragmentResumedEvent(mFragment));
    }

    public void onPause() {
        mFragmentBus.post(new FragmentPausedEvent(mFragment));
    }

    public void onStop() {
        mFragmentBus.post(new FragmentStoppedEvent(mFragment));
    }

    public void onDestroyView() {
        mFragmentBus.post(new FragmentViewDestroyedEvent(mFragment));
    }

    public void onDestroy() {
        mFragmentBus.post(new FragmentDestroyedEvent(mFragment));
        mIsCreated = false; // disable the FragmentCreatedEvent producer method
    }

    public void onDetach() {
        mFragmentBus.post(new ActivityDetachedEvent(mFragment));

        // If we've been through onDestroy already, we're on the way out and should do some cleanup.
        // If not, it's a retrained fragment and the activity is just detaching.
        if (!mIsCreated) {
            mFragmentBus.unregister(this);  // so that producer methods are no longer called

            // at this point, the fragment bus should have no registered objects. If it does, log an error to facilitate
            // investigation.
            Set<Object> registeredObjects = mFragmentBus.getRegistrants();
            if (registeredObjects.size() != 0) {
                mLogger.warn(
                        "BusFragmentHelper.onDetach: mFragmentBus should not have any registered objects at this point, but it does: {}",
                        registeredObjects);
                mFragmentBus.deactivate(); // unregister any laggards
            }
        }

        mActivity = null;
    }

    // This "producer" method is needed so that event handlers subscribed to FragmentCreatedEvent receive the event
    // even if they are registered with the bus AFTER onCreate() executes, which is normally the case.
    @Produce
    public FragmentCreatedEvent produceFragmentCreated() {
        FragmentCreatedEvent result = null;
        if (mIsCreated)
            result = new FragmentCreatedEvent(mFragment);
        return result;
    }

    // This "producer" method is needed so that event handlers subscribed to ActivityAttachedEvent receive the event
    // even if they are registered with the bus AFTER onAttach() executes, which is often the case.
    @Produce
    public ActivityAttachedEvent produceActivityAttached() {
        ActivityAttachedEvent result = null;
        if (mActivity != null)
            result = new ActivityAttachedEvent(mFragment, mActivity);
        return result;
    }
}
