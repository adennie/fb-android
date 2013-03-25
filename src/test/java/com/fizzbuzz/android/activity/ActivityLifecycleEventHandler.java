package com.fizzbuzz.android.activity;

import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class ActivityLifecycleEventHandler {
    private List<Object> mReceivedEvents;

    public ActivityLifecycleEventHandler() {
        mReceivedEvents = new ArrayList<Object>();
    }

    public boolean eventWasReceived() {
        return mReceivedEvents.size() > 0;
    }

    public List<Object> getReceivedEvents() {
        return mReceivedEvents;
    }

    @Subscribe
    public void onCreate(final ActivityEvents.ActivityCreatedEvent event) {
        mReceivedEvents.add(event);
    }

    @Subscribe
    public void onRestart(final ActivityEvents.ActivityRestartedEvent event) {
        mReceivedEvents.add(event);
    }

    @Subscribe
    public void onStart(final ActivityEvents.ActivityStartedEvent event) {
        mReceivedEvents.add(event);
    }

    @Subscribe
    public void onRestoreInstanceState(final ActivityEvents.ActivityInstanceStateRestoredEvent event) {
        mReceivedEvents.add(event);
    }

    @Subscribe
    public void onResume(final ActivityEvents.ActivityResumedEvent event) {
        mReceivedEvents.add(event);
    }

    @Subscribe
    public void onPause(final ActivityEvents.ActivityPausedEvent event) {
        mReceivedEvents.add(event);
    }

    @Subscribe
    public void onSaveInstanceState(final ActivityEvents.ActivityInstanceStateSavedEvent event) {
        mReceivedEvents.add(event);
    }

    @Subscribe
    public void onStop(final ActivityEvents.ActivityStoppedEvent event) {
        mReceivedEvents.add(event);
    }

    @Subscribe
    public void onDestroy(final ActivityEvents.ActivityDestroyedEvent event) {
        mReceivedEvents.add(event);
    }
}