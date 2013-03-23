package com.fizzbuzz.android.fragment;

import static org.fest.assertions.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import android.os.Bundle;

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

@RunWith(RobolectricTestRunner.class)
public class BusFragmentTest {

    @Test
    @SuppressWarnings({ "static-method", "unused" })
    public void testOnActivityCreatedEventIsPosted() {
        final BusFragment testFrag = new BusFragment();
        LifecycleEventHandler handler = new LifecycleEventHandler();
        testFrag.getFragmentBus().register(handler);

        final Bundle bundle = new Bundle();

        testFrag.onActivityCreated(bundle);

        assertThat(handler.eventWasReceived()).isTrue();
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
