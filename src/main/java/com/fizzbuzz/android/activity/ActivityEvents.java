package com.fizzbuzz.android.activity;

import android.app.Activity;
import android.os.Bundle;

public class ActivityEvents {
    private static class ActivityLifecycleEvent {
        private final Activity mActivity;

        public ActivityLifecycleEvent(final Activity activity) {
            mActivity = activity;
        }

        public Activity getActivity() {
            return mActivity;
        }
    }

    public static class ActivityCreatedEvent
            extends ActivityLifecycleEvent {
        public ActivityCreatedEvent(final Activity activity) {
            super(activity);
        }
    }

    public static class ActivityRestartedEvent
            extends ActivityLifecycleEvent {
        public ActivityRestartedEvent(final Activity activity) {
            super(activity);
        }
    }

    public static class ActivityDestroyedEvent
            extends ActivityLifecycleEvent {
        public ActivityDestroyedEvent(final Activity activity) {
            super(activity);
        }
    }

    public static class ActivityInstanceStateRestoredEvent
            extends ActivityLifecycleEvent {
        private final Bundle mSavedInstanceState;

        public ActivityInstanceStateRestoredEvent(final Activity activity,
                final Bundle savedInstanceState) {
            super(activity);
            mSavedInstanceState = savedInstanceState;
        }

        public Bundle getSavedInstanceState() {
            return mSavedInstanceState;
        }
    }

    public static class ActivityInstanceStateSavedEvent
            extends ActivityLifecycleEvent {
        private final Bundle mOutState;

        public ActivityInstanceStateSavedEvent(final Activity activity,
                Bundle outState) {
            super(activity);
            mOutState = outState;
        }

        public Bundle getOutState() {
            return mOutState;
        }
    }

    public static class ActivityPausedEvent
            extends ActivityLifecycleEvent {
        public ActivityPausedEvent(final Activity activity) {
            super(activity);
        }
    }

    public static class ActivityResumedEvent
            extends ActivityLifecycleEvent {
        public ActivityResumedEvent(final Activity activity) {
            super(activity);
        }
    }

    public static class ActivityStartedEvent
            extends ActivityLifecycleEvent {
        public ActivityStartedEvent(final Activity activity) {
            super(activity);
        }
    }

    public static class ActivityStoppedEvent
            extends ActivityLifecycleEvent {
        public ActivityStoppedEvent(final Activity activity) {
            super(activity);
        }
    }
}
