package com.fizzbuzz.android.async;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;

/*
 * Manages async tasks on behalf of a given controlling object. The controlling object should create an AsyncTaskManager
 * and hold onto it, registering any AsyncTaskHelper objects it creates with the AsyncTaskManager via the manage()
 * method, prior to calling execute() on the AsyncTaskHelper.
 * As Activities detach and subsequently reattach from the (e.g. due to device rotations)
 * The AsyncTaskManager mediates progress updates between the async tasks and the default progress listener interface
 * (optionally) provided by the controlling object, by automatically connecting the listener interface to tasks as
 * they are registered via manage() (if it's not currently in use), and then by moving the connection to the next running task when a previous one
 * completes.
 * The rules for using an AsyncTaskManager:
 * If the controlling object is an Activity,
 * - call onUiResume from the activity's onResume
 * - call onUiPause from the actvitity's onPause
 * - call onDestroy from the activity's onDestroy
 * If the controlling object is a non-retained Fragment
 * - call onUiResume from the fragment's onResume
 * - call onUiPause from the fragment's onPause
 * - call onDestroy from the fragment's onDestroy
 * If the controlling object is a retained Fragment, in addition to the above,
 * - call onActivityAttached from the fragment's onAttach
 * - call onActivityDetached from the fragment's onDetach
 */
public class AsyncTaskManager
        implements AsyncTaskController {
    private final Map<AsyncTaskControllee, Boolean> mManagedTasks = new HashMap<AsyncTaskControllee, Boolean>();
    private ProgressListener mDefaultProgressListener;
    private boolean mDefaultProgressListenerInUse = false;

    public AsyncTaskManager() {
        this(null);
    }

    public AsyncTaskManager(final ProgressListener defaultProgressListener) {
        mDefaultProgressListener = defaultProgressListener;
    }

    public void setDefaultProgressListener(ProgressListener newDefaultListener) {
        // run through the currently managed tasks to see if any are using the previous default listener. If so, swap it out for the new one.
        if (mDefaultProgressListener != null && mDefaultProgressListenerInUse) {
            for (AsyncTaskControllee task : mManagedTasks.keySet()) {
                if (task.getProgressListener() == mDefaultProgressListener) {
                    if (newDefaultListener != null)
                        task.setProgressListener(newDefaultListener);
                    else
                        mDefaultProgressListenerInUse = false;
                    break;
                }
            }
        }
        mDefaultProgressListener = newDefaultListener;
    }

    // use this variation when you want the managed task to use the manager's default progress listener
    public void manage(final AsyncTaskControllee task) {
        checkNotNull(task, "task");
        checkState(!mManagedTasks.containsKey(task), "attempt to manage AsyncTaskHelper that is already being managed");

        mManagedTasks.put(task, true);

        if (mDefaultProgressListener != null && !mDefaultProgressListenerInUse) {
            task.setProgressListener(mDefaultProgressListener);
            mDefaultProgressListenerInUse = true;
        }

        task.setController(this);
    }

    // use this variation when you want the managed task to use a non-default progress listener, or no listener
    public void manage(final AsyncTaskControllee task,
            final ProgressListener progressListener) {
        checkNotNull(task, "task");
        checkState(!mManagedTasks.containsKey(task), "attempt to manage AsyncTaskHelper that is already being managed");

        mManagedTasks.put(task, false);

        if (progressListener != null)
            task.setProgressListener(progressListener);
        task.setController(this);
    }

    // use this variation when you want the managed task to use the manager's default progress listener
    public void execute(final AsyncTaskHelper<?> task) {
        checkNotNull(task, "task");
        manage(task);
        task.execute();
    }

    // use this variation when you want the managed task to use a non-default progress listener, or no listener
    public void execute(final AsyncTaskHelper<?> task,
            final ProgressListener progressListener) {
        checkNotNull(task, "task");
        manage(task, progressListener);
        task.execute();
    }

    public ProgressListener getDefaultProgressListener() {
        return mDefaultProgressListener;
    }

    @Override
    public void onTaskCompleted(final AsyncTaskControllee task) {
        checkNotNull(task, "task");

        // find the task in the task list and remove it
        mManagedTasks.remove(task);

        // if there is a default progress listener and that task was using it, connect the default listener to the next
        // task that wants to use it
        if (mDefaultProgressListener != null && task.getProgressListener() == mDefaultProgressListener) {
            mDefaultProgressListenerInUse = false;
            assignDefaultProgressListenerToTask();
        }
    }

    // coordinate with the Activity lifecycle

    public void onActivityAttached(Activity activity) {
        if (mDefaultProgressListener != null)
            mDefaultProgressListener.onActivityAttached(activity);

        for (AsyncTaskControllee task : mManagedTasks.keySet()) {
            task.onActivityAttached(activity);
        }

    }

    public void onUiResume() {
        for (AsyncTaskControllee task : mManagedTasks.keySet()) {
            task.onUiResume();
        }
    }

    public void onUiPause() {
        for (AsyncTaskControllee task : mManagedTasks.keySet()) {
            task.onUiPause();
        }
    }

    public void onActivityDetached() {
        for (AsyncTaskControllee task : mManagedTasks.keySet()) {
            // Note: each task is responsible for calling onActivityDetached on its progress listener, if it has one.
            task.onActivityDetached();
        }

        if (mDefaultProgressListener != null)
            mDefaultProgressListener.onActivityDetached();
    }

    public void onDestroy() {
        cancelAsyncTasks();
    }

    private void assignDefaultProgressListenerToTask() {
        // if there is a default progress listener configured and it's not in use
        if (mDefaultProgressListener != null && !mDefaultProgressListenerInUse) {
            // iterate over the managed tasks
            for (AsyncTaskControllee task : mManagedTasks.keySet()) {
                // if whomever submitted the task to be managed asked that it used the default progress listener, and the task is currently running
                if (mManagedTasks.get(task) && task.isRunning()) {
                    task.setProgressListener(mDefaultProgressListener);
                    mDefaultProgressListenerInUse = true;
                    break;
                }
            }
        }
    }

    private void cancelAsyncTasks() {
        for (AsyncTaskControllee task : mManagedTasks.keySet()) {
            task.cancelTask();
        }
        mManagedTasks.clear();
    }
}
