package com.fizzbuzz.android.fragment;

import static com.google.common.base.Preconditions.checkNotNull;
import android.app.Activity;
import android.os.Bundle;

/*
 * AbstractFragmentLifecycleListener is a base class for concrete classes that implement the FragmentLifecycleListener interface. Generally, a given
 * FragmentLifecycleListener will only need to participate in a limited number of fragment lifecycle methods, so this class provides no-op
 * implementations of all methods in the interface, allowing subclasses to only implement the ones they care about.
 */
public abstract class AbstractFragmentLifecycleListener
        implements FragmentLifecycleListener {

    public AbstractFragmentLifecycleListener() {
    }

    @Override
    public void onAttach(Activity activity) {
        checkNotNull(activity, "activity");
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
    }

    @Override
    public void onStart() {
    }

    @Override
    public void onResume() {
    }

    @Override
    public void onPause() {
    }

    @Override
    public void onStop() {
    }

    @Override
    public void onDestroyView() {
    }

    @Override
    public void onDetach() {
    }

    @Override
    public void onDestroy() {
    }

}
