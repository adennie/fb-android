package com.fizzbuzz.android.async;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import android.app.Activity;

import com.fizzbuzz.android.fragment.FragmentLifecycleListenerBase;

/*
 * This is a helper class to keep an AsyncTaskManager object synched up with the lifecycle of a Fragment that uses it, usually via the
 * functionality embodied within one of the *FragmentLifecycleHub helper base classes.
 * For AsyncTaskManagers that are controlled by Activities, use AsyncTaskManagerActivityLifecycleListener instead.
 */

public class AsyncTaskManagerFragmentLifecycleListener
        extends FragmentLifecycleListenerBase {

    private final AsyncTaskManager mAsyncTaskManager;

    public AsyncTaskManagerFragmentLifecycleListener(
            AsyncTaskManager asyncTaskMgr) {

        checkNotNull(asyncTaskMgr, "asyncTaskMgr");

        mAsyncTaskManager = asyncTaskMgr;
    }

    @Override
    public void onAttach(Activity activity) {
        checkNotNull(activity, "activity");
        checkState(mAsyncTaskManager != null, "no AsyncTaskManager attached");

        super.onAttach(activity);

        mAsyncTaskManager.onActivityAttached(activity);
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
    public void onDetach() {
        checkState(mAsyncTaskManager != null, "no AsyncTaskManager attached");
        mAsyncTaskManager.onActivityDetached();
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        checkState(mAsyncTaskManager != null, "no AsyncTaskManager attached");
        mAsyncTaskManager.onDestroy();
        super.onDestroy();
    }

}
