package com.fizzbuzz.android.activity;

import android.app.Activity;
import com.fizzbuzz.android.BusApplicationTestRunner;
import com.fizzbuzz.android.dagger.Injector;
import com.squareup.otto.OttoBus;
import com.squareup.otto.Subscribe;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.util.ActivityController;

import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(BusApplicationTestRunner.class)
public abstract class AbstractBusActivityTest<T extends Activity & Injector & BusManagingActivity> {

    private T mActivity1;
    private T mActivity2;

    @Before
    public void setUp() {
        mActivity1 = getActivityController().create().get();
        mActivity2 = getActivityController().create().get();
    }

    @Test
    public void testDistinctBusActivitiesGetInjectedWithDistinctActivityBuses() {
        assertThat(mActivity1.getActivityBus()).isNotEqualTo(mActivity2.getActivityBus());
    }

    @Test
    public void testDistinctBusActivitiesPostEventsToTheirOwnDistinctLowestLevelBuses() {


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
    public void testDistinctBusActivitiesShareACommonApplicationBus() {
        assertThat(mActivity1.getApplicationBus()).isEqualTo(mActivity2.getApplicationBus());
    }

    @Test
    public void testVisibilityScopedAppBusAutoRegistersAndDeregistersProperly() {

        OttoBus visScopedAppBus = mActivity1.getVisibilityScopedApplicationBus();
        TestEventHandler handler = new TestEventHandler();
        visScopedAppBus.register(handler);

        // The visibility-scoped application bus should not pass events to subscribers unless the activity
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

    abstract protected ActivityController<T> getActivityController();

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
