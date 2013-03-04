package com.fizzbuzz.android.fragment;

import javax.inject.Inject;

import android.os.Bundle;

import com.fizzbuzz.android.async.AsyncTaskManager;

public class AsyncListFragment
        extends BusListFragment {

    @Inject AsyncTaskManager mAsyncTaskManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // this sets up the fragment lifecycle bus

        mAsyncTaskManager.connectToLifecycleBus(getFragmentBus());
    }

    public AsyncTaskManager getAsyncTaskManager() {
        return mAsyncTaskManager;
    }
}
