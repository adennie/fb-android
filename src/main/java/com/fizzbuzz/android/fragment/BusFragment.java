package com.fizzbuzz.android.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import com.fizzbuzz.android.application.BusApplicationModule;
import com.fizzbuzz.android.fragment.FragmentEvents.*;
import com.fizzbuzz.android.injection.Injector;
import com.fizzbuzz.ottoext.GuaranteedDeliveryBus;
import com.fizzbuzz.ottoext.ScopedBus;
import com.squareup.otto.Bus;
import com.squareup.otto.OttoBus;
import com.squareup.otto.Produce;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Set;

/*
 * BusFragment is a base class for Fragments that want to (a) post and/or subscribe to events on an application-wide bus
 * and (b) take advantage of a built-in Fragment-specific event bus that automatically distributes Fragment lifecycle
 * events. The Fragment-specific bus can also be used by application components to post and subscribe to
 * events that are related to a particular fragment.
 */
public class BusFragment
        extends Fragment {
    // the global bus is used for application-wide events. It's a MainThreadBus, so all events posted to it get
    // delivered on the main thread.
    @Inject @BusApplicationModule.ApplicationScopedMainThread
    GuaranteedDeliveryBus mGlobalBus;

    private final Logger mLogger = LoggerFactory.getLogger(LoggingManager.TAG);

    // the visible-scoped bus wraps the global bus, but is activated/deactivated as the Fragment gets resumed/paused,
    // so that registered objects don't receive events while the Fragment is not visible.
    private ScopedBus mVisibleScopedBus;

    // the fragment bus is a separate bus maintained by this Fragment that allows objects to post, produce, and
    // subscribe to events that are specific to this Fragment (e.g., lifecycle events posted by this class)
    private final ScopedBus mFragmentBus;

    private final FragmentCreatedEventProducer mFragmentCreatedEventProducer;

    private boolean mIsCreated = false;

    public BusFragment() {
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
        ((Injector) getActivity()).getObjectGraph().inject(this);

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
        Set<Object> registeredObjects = mFragmentBus.getRegistrants();
        if (registeredObjects.size() != 0) {
            mLogger.warn(
                    "BusFragment.onDestroy: mFragmentBus should not have any registered objects at this point, but it does: {}",
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
                result = new FragmentCreatedEvent(BusFragment.this);
            return result;
        }
    }
}
