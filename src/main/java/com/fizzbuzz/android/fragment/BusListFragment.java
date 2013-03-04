package com.fizzbuzz.android.fragment;

import java.util.Set;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;

import com.fizzbuzz.android.application.DaggerApplication;
import com.fizzbuzz.android.application.DaggerModule.GlobalMainThread;
import com.fizzbuzz.android.fragment.FragmentEvents.ActivityAttachedEvent;
import com.fizzbuzz.android.fragment.FragmentEvents.ActivityCreatedEvent;
import com.fizzbuzz.android.fragment.FragmentEvents.ActivityDetachedEvent;
import com.fizzbuzz.android.fragment.FragmentEvents.FragmentCreatedEvent;
import com.fizzbuzz.android.fragment.FragmentEvents.FragmentDestroyedEvent;
import com.fizzbuzz.android.fragment.FragmentEvents.FragmentPausedEvent;
import com.fizzbuzz.android.fragment.FragmentEvents.FragmentResumedEvent;
import com.fizzbuzz.android.fragment.FragmentEvents.FragmentStartedEvent;
import com.fizzbuzz.android.fragment.FragmentEvents.FragmentStoppedEvent;
import com.fizzbuzz.android.fragment.FragmentEvents.FragmentViewDestroyedEvent;
import com.fizzbuzz.ottoext.GuaranteedDeliveryBus;
import com.fizzbuzz.ottoext.ScopedBus;
import com.squareup.otto.Bus;
import com.squareup.otto.OttoBus;
import com.squareup.otto.Produce;

/*
 * BusListFragment is a base class for ListFragments that want to (a) subscribe to events on a global bus and (b) post
 * Fragment lifecycle events to a local Fragment-specific bus. The existence of this Fragment-specific bus makes it easy
 * to implement helper classes that encapsulate interactions with certain components or APIs and participate in the
 * Fragment lifecycle, while avoiding tight coupling with the Fragment (i.e. the Fragment class need not override
 * lifecycle methods and make "relay" calls to the helper class).
 */
public class BusListFragment
        extends ListFragment {
    // the global bus is used for application-wide events. It's a MainThreadBus, so all events posted to it get
    // delivered on the main thread.
    @Inject @GlobalMainThread GuaranteedDeliveryBus mGlobalBus;

    private final Logger mLogger = LoggerFactory.getLogger(LoggingManager.TAG);

    // the visible-scoped bus wraps the global bus, but is activated/deactivated as the Fragment gets resumed/paused,
    // so that registered objects don't receive events while the Fragment is not visible.
    private ScopedBus mVisibleScopedBus;

    // the fragment bus is a separate bus maintained by this Fragment that allows objects to post, produce, and
    // subscribe to events that are specific to this Fragment (e.g., lifecycle events posted by this class)
    private final ScopedBus mFragmentBus;

    private final FragmentCreatedEventProducer mFragmentCreatedEventProducer;

    private boolean mIsCreated = false;

    public BusListFragment() {
        mFragmentBus = new ScopedBus(new Bus());
        mFragmentBus.activate();
        mFragmentCreatedEventProducer = new FragmentCreatedEventProducer();
    }

    public final GuaranteedDeliveryBus getGlobalBus() {
        return mGlobalBus;
    }

    public final OttoBus getVisibleScopedGlobalBus() {
        return mVisibleScopedBus;
    }

    public final OttoBus getFragmentBus() {
        return mFragmentBus;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mFragmentBus.post(new ActivityAttachedEvent(this));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // inject ourselves
        DaggerApplication.getObjectGraph().inject(this);

        mVisibleScopedBus = new ScopedBus(mGlobalBus);

        // register this Fragment with the global bus
        mGlobalBus.register(this);

        // post a FragmentCreatedEvent to the Fragment-specific bus for the benefit of current subscribers to that event
        mFragmentBus.post(new FragmentCreatedEvent(this));

        // register a FragmentCreatedEventProducer with the Fragment-specific bus for the benefit of future subscribers
        // to FragmentCreatedEvent
        mFragmentBus.register(mFragmentCreatedEventProducer);

        mIsCreated = true;
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFragmentBus.post(new ActivityCreatedEvent(this, savedInstanceState));
    }

    @Override
    public void onStart() {
        super.onStart();
        mFragmentBus.post(new FragmentStartedEvent(this));
    }

    @Override
    public void onResume() {
        super.onResume();
        mVisibleScopedBus.activate();
        mFragmentBus.post(new FragmentResumedEvent(this));
    }

    @Override
    public void onPause() {
        mVisibleScopedBus.deactivate();
        mFragmentBus.post(new FragmentPausedEvent(this));
        super.onPause();
    }

    @Override
    public void onStop() {
        mFragmentBus.post(new FragmentStoppedEvent(this));
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        mFragmentBus.post(new FragmentViewDestroyedEvent(this));
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        mFragmentBus.post(new FragmentDestroyedEvent(this));
        mFragmentBus.unregister(mFragmentCreatedEventProducer);

        // at this point, all objects should be deregistered from the Fragment bus. If not, log an error to facilitate
        // investigation.
        Set<Object> registeredObjects = mFragmentBus.getRegistrations();
        if (registeredObjects.size() != 0) {
            mLogger.warn(
                    "BusListFragment.onDestroy: mFragmentBus should not have any registered objects at this point, but it does: {}",
                    registeredObjects);
            mFragmentBus.deactivate(); // to prevent memory leaks
        }

        mGlobalBus.unregister(this);
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        mFragmentBus.post(new ActivityDetachedEvent(this));
        super.onDetach();
    }

    private class FragmentCreatedEventProducer {
        // This "producer" method is needed so that event handlers subscribed to FragmentCreatedEvent receive the event
        // even if they are registered with the bus AFTER super.onCreate() executes, which is normally the case.
        @Produce
        public FragmentCreatedEvent produceFragmentCreated() {
            FragmentCreatedEvent result = null;
            if (mIsCreated)
                result = new FragmentCreatedEvent(BusListFragment.this);
            return result;
        }
    }
}
