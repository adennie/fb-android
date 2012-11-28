package com.fizzbuzz.android.socialize;

import static com.google.common.base.Preconditions.checkNotNull;
import android.app.Activity;
import android.os.Bundle;

import com.fizzbuzz.android.util.ActivityLifecycleListenerImpl;
import com.socialize.Socialize;

/*
 * Activities using this class should instantiate it in their onCreate method, and call onDestroy, onPause, and onResume from the corresponding
 * Activity methods
 */

public class SocializeActivityLifecycleListener
        extends ActivityLifecycleListenerImpl {

    public SocializeActivityLifecycleListener(Activity activity,
            Bundle savedInstanceState) {
        super(activity);

        // validate input
        checkNotNull(activity, "activity");

        Socialize.onCreate(activity, savedInstanceState);

    }

    @Override
    public void onDestroy() {
        Socialize.onDestroy(getActivity());
        super.onDestroy();
    }

    @Override
    public void onPause() {
        Socialize.onPause(getActivity());
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        Socialize.onResume(getActivity());
    }

}
