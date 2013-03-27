package com.fizzbuzz.android.fragment;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import com.fizzbuzz.android.activity.BusActivity;
import com.fizzbuzz.android.application.BusApplication;
import com.fizzbuzz.android.fragment.FragmentEvents.*;
import com.squareup.otto.Subscribe;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.model.InitializationError;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(BusFragmentTest.LocalTestRunner.class)
public class BusFragmentTest {
    private BusActivity mActivity;
    private BusFragment mFragment;
    private FragmentLifecycleEventHandler mHandler;

    @Before
    public void setUp() {
        Application app = Robolectric.application;
        app.onCreate();

        Intent intent = new Intent();
        intent.setClassName("com.fizzbuzz.android", "com.fizzbuzz.android.BusActivity");

        mActivity = new BusActivity();
        mActivity.setIntent(intent);
        mActivity.onCreate(null);

        mFragment = new BusFragment();
        mFragment.onAttach(mActivity);

        mHandler = new FragmentLifecycleEventHandler();
    }

    @Test
    public void testActivityAttachedEventProducerProducesAfterInitialAttach() {

        // the activity was already attached in setUp.  Register a handler to trigger the producer.
        mFragment.getFragmentBus().register(mHandler);

        // producer should have produced the ActivityAttachedEvent, and nothing else
        assertThat(mHandler.getReceivedEvents().size()).isEqualTo(1);
        assertThat(mHandler.getReceivedEventTypes().contains(ActivityAttachedEvent.class)).isTrue();
    }

    @Test
    public void testActivityAttachedEventProducerDoesNotProducesAfterDetach() {

        // the activity was already attached in setUp.  Detach, then register a handler to invoke the producer.
        mFragment.onDetach();
        mFragment.getFragmentBus().register(mHandler);

        // producer should not have produced
        assertThat(mHandler.getReceivedEvents().size()).isEqualTo(0);
    }

    @Test
    public void testFragmentCreatedEventProducerProducesAfterInitialCreate() {
        // subsequent to onCreate, register a handler
        mFragment.onCreate(null);
        mFragment.getFragmentBus().register(mHandler);

        // The ActivityAttachedEvent producer AND the FragmentCreatedEvent producer method should
        // both produce.
        assertThat(mHandler.getReceivedEvents().size()).isEqualTo(2);
        assertThat(mHandler.getReceivedEventTypes().contains(ActivityAttachedEvent.class)).isTrue();
        assertThat(mHandler.getReceivedEventTypes().contains(FragmentCreatedEvent.class)).isTrue();
    }

    @Test
    public void testFragmentCreatedEventProducerDoesNotProduceAfterDestroy() {
        // create and destroy, then register a handler
        mFragment.onCreate(null);
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
    public void testOnActivityCreatedEventIsPosted() {
        mFragment.getFragmentBus().register(mHandler);
        mFragment.onActivityCreated(new Bundle());

        // register should have produced ActivityAttachedEvent; onActivityCreated should have posted ActivityCreatedEvent
        assertThat(mHandler.getReceivedEvents().size()).isEqualTo(2);
        assertThat(mHandler.getReceivedEventTypes().contains(ActivityAttachedEvent.class)).isTrue();
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

    public static class LocalTestRunner
            extends RobolectricTestRunner {

        public LocalTestRunner(Class<?> testClass) throws InitializationError {
            super(testClass);
        }

        @Override
        protected Application createApplication() {
            return new BusApplication();
        }
    }
}
