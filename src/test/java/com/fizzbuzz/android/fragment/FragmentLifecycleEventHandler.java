package com.fizzbuzz.android.fragment;

import com.fizzbuzz.android.fragment.FragmentEvents.*;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class FragmentLifecycleEventHandler {
    private List<FragmentLifecycleEvent> mReceivedEvents;
    private List<Class<?>> mReceivedEventTypes;
    
    public FragmentLifecycleEventHandler() {
        mReceivedEvents = new ArrayList<FragmentLifecycleEvent>();
        mReceivedEventTypes = new ArrayList<Class<?>>();
    }

    public boolean eventWasReceived() {
        return mReceivedEvents.size() > 0;
    }

    public List<FragmentLifecycleEvent> getReceivedEvents() {
        return mReceivedEvents;
    }
    
    public List<Class<?>> getReceivedEventTypes() {
        return mReceivedEventTypes;
    }

    public void clearEvents(){
        mReceivedEvents.clear();
        mReceivedEventTypes.clear();
    }
    
    private void addEvent(FragmentLifecycleEvent event) {
        mReceivedEvents.add(event);
        mReceivedEventTypes.add(event.getClass());
    }

    @Subscribe
    public void onAttach(final ActivityAttachedEvent event) {
        addEvent(event);
    }

    @Subscribe
    public void onCreate(final FragmentCreatedEvent event) {
        addEvent(event);
    }

    @Subscribe
    public void onCreateView(final FragmentViewCreatedEvent event) {
        addEvent(event);
    }

    @Subscribe
    public void onActivityCreated(final ActivityCreatedEvent event) {
        addEvent(event);
    }

    @Subscribe
    public void onStart(final FragmentStartedEvent event) {
        addEvent(event);
    }

    @Subscribe
    public void onResume(final FragmentResumedEvent event) {
        addEvent(event);
    }

    @Subscribe
    public void onPause(final FragmentPausedEvent event) {
        addEvent(event);
    }

    @Subscribe
    public void onStop(final FragmentStoppedEvent event) {
        addEvent(event);
    }

    @Subscribe
    public void onDestroyView(final FragmentViewDestroyedEvent event) {
        addEvent(event);
    }

    @Subscribe
    public void onDestroy(final FragmentDestroyedEvent event) {
        addEvent(event);
    }

    @Subscribe
    public void onDetached(final ActivityDetachedEvent event) {
        addEvent(event);
    }
}