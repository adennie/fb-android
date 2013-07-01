package com.fizzbuzz.android.fragment;

import android.os.Bundle;
import com.fizzbuzz.android.BusApplicationTestRunner;
import com.fizzbuzz.android.activity.BusFragmentActivity;
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
import com.squareup.otto.Subscribe;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(BusApplicationTestRunner.class)
public class BusFragmentTest {
    private BusFragmentActivity mActivity;
    private BusFragment mFragment;
    private FragmentLifecycleEventHandler mHandler;

    @Before
    public void setUp() {
        mActivity = Robolectric.buildActivity(BusFragmentActivity.class).create().start().resume().get();

        mFragment = new BusFragment();
        FragmentUtils.startFragment(mActivity, mFragment);

        mHandler = new FragmentLifecycleEventHandler();
    }

    @Test
    public void testProducersProducesAfterFragmentStartup() {

        // the activity was already attached in setUp.  Register a handler to trigger the producer.
        mFragment.getFragmentBus().register(mHandler);

        // producers should have produced an ActivityAttachedEvent, and a FragmentCreatedEvent
        assertThat(mHandler.getReceivedEvents().size()).isEqualTo(2);
        assertThat(mHandler.getReceivedEventTypes().contains(ActivityAttachedEvent.class)).isTrue();
        assertThat(mHandler.getReceivedEventTypes().contains(FragmentCreatedEvent.class)).isTrue();

    }

    @Test
    public void testActivityAttachedEventProducerDoesNotProducesAfterDetach() {

        // the activity was already attached in setUp.  Detach, then register a handler to invoke the producer.
        mFragment.onDetach();
        mFragment.getFragmentBus().register(mHandler);

        // the producer for ActivityAttachedEvent should not have produced, but the producer for FragmentCreatedEvent
        // should have
        assertThat(mHandler.getReceivedEvents().size()).isEqualTo(1);
        assertThat(mHandler.getReceivedEventTypes().contains(FragmentCreatedEvent.class)).isTrue();
    }

    @Test
    public void testFragmentCreatedEventProducerDoesNotProduceAfterDestroy() {
        // create and destroy, then register a handler
        mFragment.onDestroy();
        mFragment.getFragmentBus().register(mHandler);

        // The ActivityAttachedEvent producer should have produced, but the FragmentCreatedEvent producer method should
        // not have produced.
        assertThat(mHandler.getReceivedEvents().size()).isEqualTo(1);
        assertThat(mHandler.getReceivedEventTypes().contains(ActivityAttachedEvent.class)).isTrue();
    }

    @Test
    public void testFragmentCreatedEventProducerStillProducesIfDetachHappensWithoutDestroy() {
        // simulate fragment creation followed by activity detach, then register a handler
        mFragment.onCreate(null);
        mFragment.onDetach();
        mFragment.getFragmentBus().register(mHandler);

        //  The FragmentCreatedEvent producer method should have produced, but the ActivityAttachedEvent
        // producer should not have.
        assertThat(mHandler.getReceivedEvents().size()).isEqualTo(1);
        assertThat(mHandler.getReceivedEventTypes().contains(FragmentCreatedEvent.class)).isTrue();
    }

    @Test
    public void testHandlerReceivesActivityAttachedEventFollowingRetainedFragmentDetachAttachSequence() {

        // detach, then register handler, then attach again.
        mFragment.onCreate(null);
        mFragment.onDetach();
        mFragment.getFragmentBus().register(mHandler);
        mFragment.onAttach(mActivity);

        // register should have produced FragmentCreatedEvent; onAttach should have posted ActivityAttachedEvent
        assertThat(mHandler.getReceivedEvents().size()).isEqualTo(2);
        assertThat(mHandler.getReceivedEventTypes().contains(FragmentCreatedEvent.class)).isTrue();
        assertThat(mHandler.getReceivedEventTypes().contains(ActivityAttachedEvent.class)).isTrue();
    }

    @Test
    public void testActivityCreatedEventIsPosted() {
        mFragment.getFragmentBus().register(mHandler);
        mFragment.onActivityCreated(new Bundle());

        // register should have produced ActivityAttachedEvent and FragmentCreatedEvent; onActivityCreated should
        // have posted ActivityCreatedEvent
        assertThat(mHandler.getReceivedEvents().size()).isEqualTo(3);
        assertThat(mHandler.getReceivedEventTypes().contains(ActivityAttachedEvent.class)).isTrue();
        assertThat(mHandler.getReceivedEventTypes().contains(FragmentCreatedEvent.class)).isTrue();
        assertThat(mHandler.getReceivedEventTypes().contains(ActivityCreatedEvent.class)).isTrue();
    }

    public static class LifecycleEventHandler {
        private boolean mEventWasReceived = false;

        public boolean eventWasReceived() {
            return mEventWasReceived;
        }

        @Subscribe
        public void onActivityAttached(final ActivityAttachedEvent event) {
            mEventWasReceived = true;
        }

        @Subscribe
        public void onFragmentCreated(final FragmentCreatedEvent event) {
            mEventWasReceived = true;
        }

        @Subscribe
        public void onActivityCreated(final ActivityCreatedEvent event) {
            mEventWasReceived = true;
        }

        @Subscribe
        public void onFragmentStarted(final FragmentStartedEvent event) {
            mEventWasReceived = true;
        }

        @Subscribe
        public void onFragmentResumed(final FragmentResumedEvent event) {
            mEventWasReceived = true;
        }

        @Subscribe
        public void onFragmentPaused(final FragmentPausedEvent event) {
            mEventWasReceived = true;
        }

        @Subscribe
        public void onFragmentStopped(final FragmentStoppedEvent event) {
            mEventWasReceived = true;
        }

        @Subscribe
        public void onFragmentViewDestroyed(final FragmentViewDestroyedEvent event) {
            mEventWasReceived = true;
        }

        @Subscribe
        public void onActivityDetached(final ActivityDetachedEvent event) {
            mEventWasReceived = true;
        }

        @Subscribe
        public void onFragmentDestroyed(final FragmentDestroyedEvent event) {
            mEventWasReceived = true;
        }
    }
}
