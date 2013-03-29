package com.fizzbuzz.android.activity;

import android.os.Bundle;
import com.fizzbuzz.android.activity.InjectingActivityModule.ActivityScoped;
import com.fizzbuzz.android.async.AsyncTaskManager;

import javax.inject.Inject;

public class AsyncPreferenceActivity
        extends BusPreferenceActivity {

    @Inject @ActivityScoped AsyncTaskManager mAsyncTaskManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // this sets up the activity lifecycle bus

        mAsyncTaskManager.connectToLifecycleBus(getActivityBus());
    }

    public AsyncTaskManager getAsyncTaskManager() {
        return mAsyncTaskManager;
    }

}
