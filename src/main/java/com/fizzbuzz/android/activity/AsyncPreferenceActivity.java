package com.fizzbuzz.android.activity;

import javax.inject.Inject;

import android.os.Bundle;

import com.fizzbuzz.android.async.AsyncTaskManager;

public class AsyncPreferenceActivity
        extends BusPreferenceActivity {

    @Inject AsyncTaskManager mAsyncTaskManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // this sets up the activity lifecycle bus

        mAsyncTaskManager.connectToLifecycleBus(getActivityBus());
    }

    public AsyncTaskManager getAsyncTaskManager() {
        return mAsyncTaskManager;
    }

}
