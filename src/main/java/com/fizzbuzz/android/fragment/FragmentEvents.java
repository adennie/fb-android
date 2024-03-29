package com.fizzbuzz.android.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

public class FragmentEvents {
    public static class FragmentLifecycleEvent {
        private final Fragment mFragment;

        public FragmentLifecycleEvent(final Fragment fragment) {
            mFragment = fragment;
        }

        public Fragment getFragment() {
            return mFragment;
        }
    }

    public static class ActivityAttachedEvent
            extends FragmentLifecycleEvent {
        private Activity mActivity;

        public ActivityAttachedEvent(final Fragment fragment, final Activity activity) {
            super(fragment);
            mActivity = activity;
        }

        public Activity getActivity() {
            return mActivity;
        }
    }

    public static class FragmentCreatedEvent
            extends FragmentLifecycleEvent {
        public FragmentCreatedEvent(final Fragment fragment) {
            super(fragment);
        }
    }

    public static class FragmentViewCreatedEvent
        extends FragmentLifecycleEvent {
        private View mView;

        public FragmentViewCreatedEvent(final Fragment fragment, final View view ) {
            super(fragment);
            mView = view;
        }

        public View getView() {
            return mView;
        }
    }

    public static class ActivityCreatedEvent
            extends FragmentLifecycleEvent {
        private final Bundle mSavedInstanceState;

        public ActivityCreatedEvent(final Fragment fragment,
                final Bundle savedInstanceState) {
            super(fragment);
            mSavedInstanceState = savedInstanceState;
        }

        public Bundle getSavedInstanceState() {
            return mSavedInstanceState;
        }
    }

    public static class FragmentStartedEvent
            extends FragmentLifecycleEvent {
        public FragmentStartedEvent(final Fragment fragment) {
            super(fragment);
        }
    }

    public static class FragmentResumedEvent
            extends FragmentLifecycleEvent {
        public FragmentResumedEvent(final Fragment fragment) {
            super(fragment);
        }
    }

    public static class FragmentPausedEvent
            extends FragmentLifecycleEvent {
        public FragmentPausedEvent(final Fragment fragment) {
            super(fragment);
        }
    }

    public static class FragmentStoppedEvent
            extends FragmentLifecycleEvent {
        public FragmentStoppedEvent(final Fragment fragment) {
            super(fragment);
        }
    }

    public static class FragmentViewDestroyedEvent
            extends FragmentLifecycleEvent {
        public FragmentViewDestroyedEvent(final Fragment fragment) {
            super(fragment);
        }
    }

    public static class FragmentDestroyedEvent
            extends FragmentLifecycleEvent {
        public FragmentDestroyedEvent(final Fragment fragment) {
            super(fragment);
        }
    }

    public static class ActivityDetachedEvent
            extends FragmentLifecycleEvent {
        public ActivityDetachedEvent(final Fragment fragment) {
            super(fragment);
        }
    }

}
