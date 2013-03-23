package com.fizzbuzz.android.eventbus;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import com.fizzbuzz.android.activity.ActivityLifecycle;
import com.fizzbuzz.android.activity.ActivityInjector;
import com.fizzbuzz.android.activity.BusManagingActivity;
import com.squareup.otto.OttoBus;
import com.squareup.otto.Subscribe;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;

import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(FbRobolectricTestRunner.class)
public abstract class AbstractBusActivityTest<T extends Activity & ActivityInjector & BusManagingActivity> {

    private T mActivity1;
    private T mActivity2;

    @Before
    public void setUp() {
        Application app = Robolectric.application;
        app.onCreate();

        Intent intent = new Intent();
        intent.setClassName("com.fizzbuzz.android", "com.fizzbuzz.android.BusActivity");

        mActivity1 = createTestActivity();
        mActivity1.setIntent(intent);

        mActivity2 = createTestActivity();
        mActivity2.setIntent(intent);

    }

    @Test
    public void testDistinctBusActivitiesGetInjectedWithDistinctActivityBuses() {
        // initialize two BusActivities
        mActivity1.setActivityModuleClass(TestBusActivityModule.class);
        ((ActivityLifecycle)mActivity1).onCreate(null);
        mActivity2.setActivityModuleClass(TestBusActivityModule.class);
        ((ActivityLifecycle)mActivity2).onCreate(null);

        assertThat(mActivity1.getActivityBus()).isNotEqualTo(mActivity2.getActivityBus());
    }

    @Test
    public void testDistinctBusActivitiesPostEventsToTheirOwnDistinctLowestLevelBuses() {

        // initialize two BusActivities
        mActivity1.setActivityModuleClass(TestBusActivityModule.class);
        ((ActivityLifecycle)mActivity1).onCreate(null);
        mActivity2.setActivityModuleClass(TestBusActivityModule.class);
        ((ActivityLifecycle)mActivity2).onCreate(null);

        // create and register two event handlers, one for each activity's bus
        ActivityLifecycleEventHandler handler1 = new ActivityLifecycleEventHandler();
        mActivity1.getActivityBus().register(handler1);
        ActivityLifecycleEventHandler handler2 = new ActivityLifecycleEventHandler();
        mActivity2.getActivityBus().register(handler2);

        // verify that each handler's onCreate subscriber method was invoked once (this should come from the
        // BusActivityHelper's producer), and that the events received on the two buses are distinct
        List<Object> receivedEvents1 = handler1.getReceivedEvents();
        List<Object> receivedEvents2 = handler2.getReceivedEvents();

        assertThat(receivedEvents1.size()).isEqualTo(1);
        assertThat(receivedEvents2.size()).isEqualTo(1);
        assertThat(receivedEvents1.get(0)).isNotEqualTo(receivedEvents2.get(0));
    }

    @Test
    public void testDistinctBusActivitiesShareACommonGlobalBus() {
        // initialize two BusActivities
        mActivity1.setActivityModuleClass(TestBusActivityModule.class);
        ((ActivityLifecycle)mActivity1).onCreate(null);
        mActivity2.setActivityModuleClass(TestBusActivityModule.class);
        ((ActivityLifecycle)mActivity2).onCreate(null);

        assertThat(mActivity1.getApplicationBus()).isEqualTo(mActivity2.getApplicationBus());
    }

    @Test
    public void testVisibilityScopedAppBusAutoRegistersAndDeregistersProperly() {
        mActivity1.setActivityModuleClass(TestBusActivityModule.class);
        ((ActivityLifecycle)mActivity1).onCreate(null);

        OttoBus visScopedAppBus = mActivity1.getVisibilityScopedApplicationBus();
        TestEventHandler handler = new TestEventHandler();
        visScopedAppBus.register(handler);

        // OK, the visibility-scoped application bus should not pass events to subscribers unless the activity
        // is visible (post-onResume, pre-onPause)
        visScopedAppBus.post(new TestEvent());
        assertThat(handler.eventWasReceived()).isFalse();

        ((ActivityLifecycle)mActivity1).onStart();
        visScopedAppBus.post(new TestEvent());
        assertThat(handler.eventWasReceived()).isFalse();

        ((ActivityLifecycle)mActivity1).onResume();
        visScopedAppBus.post(new TestEvent());
        assertThat(handler.eventWasReceived()).isTrue();

        handler.clearEvents();

        ((ActivityLifecycle)mActivity1).onPause();
        visScopedAppBus.post(new TestEvent());
        assertThat(handler.eventWasReceived()).isFalse();

        ((ActivityLifecycle)mActivity1).onStop();
        visScopedAppBus.post(new TestEvent());
        assertThat(handler.eventWasReceived()).isFalse();

        ((ActivityLifecycle)mActivity1).onRestart();
        visScopedAppBus.post(new TestEvent());
        assertThat(handler.eventWasReceived()).isFalse();

        ((ActivityLifecycle)mActivity1).onStart();
        visScopedAppBus.post(new TestEvent());
        assertThat(handler.eventWasReceived()).isFalse();

        ((ActivityLifecycle)mActivity1).onResume();
        visScopedAppBus.post(new TestEvent());
        assertThat(handler.eventWasReceived()).isTrue();

        handler.clearEvents();

        ((ActivityLifecycle)mActivity1).onPause();
        visScopedAppBus.post(new TestEvent());
        assertThat(handler.eventWasReceived()).isFalse();

        ((ActivityLifecycle)mActivity1).onStop();
        visScopedAppBus.post(new TestEvent());
        assertThat(handler.eventWasReceived()).isFalse();

        ((ActivityLifecycle)mActivity1).onDestroy();
        visScopedAppBus.post(new TestEvent());
        assertThat(handler.eventWasReceived()).isFalse();
    }

    abstract protected T createTestActivity();

    public static class TestEvent {
    }

    public static class TestEventHandler {
        private List<Object> mReceivedEvents;

        public TestEventHandler() {
            mReceivedEvents = new ArrayList<Object>();
        }

        public boolean eventWasReceived() {
            return mReceivedEvents.size() > 0;
        }

        public List<Object> getReceivedEvents() {
            return mReceivedEvents;
        }

        public void clearEvents(){
            mReceivedEvents.clear();
        }

        @Subscribe
        public void onTestEvent(final TestEvent event) {
            mReceivedEvents.add(event);
        }

    }

}
