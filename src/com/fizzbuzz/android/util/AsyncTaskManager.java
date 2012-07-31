package com.fizzbuzz.android.util;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;

/*
 * Manages async tasks on behalf of a given controlling object. The controlling object should create an AsyncTaskManager
 * and hold onto it, registering any AsyncTaskHelper objects it creates with the AsyncTaskManager via the manage()
 * method, prior to calling execute() on the AsyncTaskHelper.
 * The AsyncTaskManager mediates progress updates between the async tasks and the progress listener interface
 * (optionally) implemented by the controlling object, by automatically connecting the listener interface to tasks as
 * they are registered via manage(), and then by moving the connection to the next running task when a previous one
 * completes.
 * In addition, the AsyncTaskManager provides methods for checking to see if any async tasks are still running, and for
 * canceling all of them.
 */
public class AsyncTaskManager
        implements AsyncTaskController {

    private ArrayList<AsyncTaskControllee> mTasks;
    private ProgressListener mDefaultProgressListener;
    private boolean mDefaultProgressListenerInUse;

    public AsyncTaskManager() {
        mDefaultProgressListener = null;
        init();
    }

    public AsyncTaskManager(final ProgressListener defaultProgressListener) {
        checkNotNull(defaultProgressListener, "default progress listener");
        mDefaultProgressListener = defaultProgressListener;
        init();
    }

    private void init() {
        mDefaultProgressListenerInUse = false;
        mTasks = new ArrayList<AsyncTaskControllee>();
    }

    // use this variation when you want to use the manager's default progress listener
    public void manage(final AsyncTaskControllee task) {
        checkNotNull(task, "task");
        manage(task, new DefaultProgressListenerProxy());
    }

    // use this variation when you want to use a non-default progress listener, or no listener (null)
    public void manage(final AsyncTaskControllee task, final ProgressListener progressListener) {
        checkNotNull(task, "task");
        if (progressListener != null) {
            task.setProgressListener(progressListener);

            // if there is a default progress listener assigned to this manager and nobody is using it, offer it to the
            // new task
            if (mDefaultProgressListener != null && !mDefaultProgressListenerInUse) {
                if (progressListener.onDefaultListenerAvailable(mDefaultProgressListener))
                    mDefaultProgressListenerInUse = true;
            }
        }

        task.setController(this);
        mTasks.add(task);
    }

    public ProgressListener getDefaultProgressListener() {
        return mDefaultProgressListener;
    }

    // Note: as currently implemented, setting a new default progress listener over the top of an old one doesn't affect
    // tasks that might still be using the old one, and won't be offered to new tasks until the task using the old one
    // has finished. However, if there was no previously assigned default progress listener, the new one will be offered
    // to currently running tasks, if it is non-null.
    public void setDefaultProgressListener(final ProgressListener newListener) {
        ProgressListener previousListener = mDefaultProgressListener;
        mDefaultProgressListener = newListener;
        if (previousListener == null)
            offerDefaultProgressListenerToTasks();
    }

    @Override
    public void onTaskCompleted(final AsyncTaskControllee task) {
        checkNotNull(task, "task");

        // find the task in the task list and remove it
        mTasks.remove(task);

        // if that task was using the default progress listener, connect the default listener to the next
        // task that wants to use it
        if (task.getProgressListener().ownsDefaultListener()) {
            mDefaultProgressListenerInUse = false;
            offerDefaultProgressListenerToTasks();
        }
    }

    public void cancelAsyncTasks() {
        for (AsyncTaskControllee task : mTasks) {
            task.cancelTask();
        }
        mTasks.clear();
    }

    public boolean isAnyAsyncTaskRunning() {
        for (AsyncTaskControllee task : mTasks) {
            if (task.isRunning())
                return true;
        }
        return false;
    }

    public void onUiPause() {
        for (AsyncTaskControllee task : mTasks) {
            task.onUiPause();
        }
    }

    public void onUiResume() {
        for (AsyncTaskControllee task : mTasks) {
            task.onUiResume();
        }
    }

    private void offerDefaultProgressListenerToTasks() {
        // if there is a default progress listener configured, connect it to the first running task that wants it
        if (mDefaultProgressListener != null) {
            for (AsyncTaskControllee task : mTasks) {
                ProgressListener listener = task.getProgressListener();
                if (listener != null && listener.onDefaultListenerAvailable(mDefaultProgressListener))
                    mDefaultProgressListenerInUse = true;
                break;
            }
        }
    }
}
