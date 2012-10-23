package com.fizzbuzz.android.util;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import android.app.Activity;
import android.os.Bundle;

/*
 * ActivityListenerImpl is a base class for concrete classes that implement the ActivityListener interface. Generally, a given ActivityListener will
 * only need to participate in a limited number of activity lifecycle methods, so this class provides no-op implementations of all methods in the
 * interface, allowing subclasses to only implement the ones they care about.
 */
public abstract class ActivityLifecycleListenerImpl
        implements ActivityLifecycleListener {

    private Activity mActivity;

    public ActivityLifecycleListenerImpl(Activity activity) {
        checkNotNull(activity, "activity");
        mActivity = activity;
    }

    protected Activity getActivity() {
        return mActivity;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
    }

    @Override
    public void onDestroy() {
        checkState(mActivity != null, "no activity attached");
        mActivity = null; // prevent activity leaks
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
    }

    @Override
    public void onStart() {
        checkState(mActivity != null, "no activity attached");
    }

    @Override
    public void onRestart() {
        checkState(mActivity != null, "no activity attached");
    }

    @Override
    public void onResume() {
        checkState(mActivity != null, "no activity attached");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        checkState(mActivity != null, "no activity attached");
    }

    @Override
    public void onPause() {
        checkState(mActivity != null, "no activity attached");
    }

    @Override
    public void onStop() {
        checkState(mActivity != null, "no activity attached");
    }

}
