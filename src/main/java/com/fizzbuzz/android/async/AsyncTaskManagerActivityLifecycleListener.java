package com.fizzbuzz.android.async;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import android.app.Activity;

import com.fizzbuzz.android.activity.ActivityLifecycleListenerBase;

/*
 * This is a helper class to keep an AsyncTaskManager object synched up with the lifecycle of an Activity that (directly) uses it, usually via the
 * functionality embodied within one of the *ActivityLifecycleHub helper base classes.
 * For AsyncTaskManagers that are controlled by Fragments, use AsyncTaskManagerFragmentLifecycleListener instead.
 */

public class AsyncTaskManagerActivityLifecycleListener
        extends ActivityLifecycleListenerBase {

    private final AsyncTaskManager mAsyncTaskManager;

    public AsyncTaskManagerActivityLifecycleListener(Activity activity,
            AsyncTaskManager asyncTaskMgr) {
        super(activity);

        // validate input
        checkNotNull(activity, "activity");
        checkNotNull(asyncTaskMgr, "asyncTaskMgr");

        mAsyncTaskManager = asyncTaskMgr;
    }

    @Override
    public void onResume() {
        checkState(mAsyncTaskManager != null, "no AsyncTaskManager attached");
        super.onResume();
        mAsyncTaskManager.onUiResume();
    }

    @Override
    public void onPause() {
        checkState(mAsyncTaskManager != null, "no AsyncTaskManager attached");
        mAsyncTaskManager.onUiPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        checkState(mAsyncTaskManager != null, "no AsyncTaskManager attached");
        mAsyncTaskManager.onDestroy();
        super.onDestroy();
    }

}
