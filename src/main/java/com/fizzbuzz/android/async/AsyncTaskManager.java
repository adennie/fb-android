package com.fizzbuzz.android.async;

import android.app.Activity;
import com.fizzbuzz.android.activity.ActivityEvents;
import com.fizzbuzz.android.fragment.FragmentEvents;
import com.squareup.otto.OttoBus;
import com.squareup.otto.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/*
 * Manages async tasks on behalf of a given controlling object. The controlling object should create an AsyncTaskManager
 * and hold onto it, registering any AsyncTaskHelper objects it creates with the AsyncTaskManager via the manage()
 * method, prior to calling execute() on the AsyncTaskHelper.
 * As Activities detach/reattach (e.g. due to device rotations) from/to an AsyncTaskManager, it
 * relays those events to the AsyncTaskHelper objects it manages, so that they never access an Activity that is no
 * longer around.
 * An AsyncTaskManager mediates progress updates between the async tasks and the default progress listener interface
 * (optionally) provided by the controlling object, by automatically connecting the listener interface to tasks as
 * they are registered via manage() (if it's not currently in use), and then by moving the connection to the next
 * running task when a previous one completes.
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
 * Adherence to these rules can be accomplished by having the controlling Activity or Fragment inherit from
 * Bus*Activity or Bus*Fragment and calling connectToLifecycleBus.
 */
public class AsyncTaskManager
        implements AsyncTaskController {
    private final Logger mLogger = LoggerFactory.getLogger(LoggingManager.TAG);
    private final Map<AsyncTaskControllee, Boolean> mManagedTasks = new HashMap<AsyncTaskControllee, Boolean>();
    private ProgressListener mDefaultProgressListener;
    private boolean mDefaultProgressListenerInUse = false;
    private OttoBus mLifecycleBus;

    @Inject
    public AsyncTaskManager() {
    }

    // we can't just get the lifecycle bus via constructor parameter injection, because an AsyncTaskManager
    // can work with both fragments and activities.  So it needs to be connected explicitly by the controlling
    // activity or fragment.
    public void connectToLifecycleBus(OttoBus lifecycleBus) {
        mLifecycleBus = lifecycleBus;
        mLifecycleBus.register(this);
    }

    public void setDefaultProgressListener(ProgressListener newDefaultListener) {
        // run through the currently managed tasks to see if any are using the previous default listener. If so, swap it
        // out for the new one.
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
    public void execute(final AsyncTaskHelper<?> task) {
        checkNotNull(task, "task");
        checkState(!mManagedTasks.containsKey(task), "attempt to manage AsyncTaskHelper that is already being managed");

        mManagedTasks.put(task, true);

        synchronized (this) {
            if (mDefaultProgressListener != null && !mDefaultProgressListenerInUse) {
                task.setProgressListener(mDefaultProgressListener);
                mDefaultProgressListenerInUse = true;
            }
        }
        task.setController(this);
        task.execute();
    }

    // use this variation when you want the managed task to use a non-default progress listener, or no listener
    public void execute(final AsyncTaskHelper<?> task,
                        final ProgressListener progressListener) {
        checkNotNull(task, "task");
        checkState(!mManagedTasks.containsKey(task), "attempt to manage AsyncTaskHelper that is already being managed");

        mManagedTasks.put(task, false);

        if (progressListener != null)
            task.setProgressListener(progressListener);
        task.setController(this);
        task.execute();
    }

    @Override
    public void onTaskCompleted(final AsyncTaskControllee task) {
        checkNotNull(task, "task");

        // find the task in the task list and remove it
        mManagedTasks.remove(task);

        // if there is a default progress listener and that task was using it, connect the default listener to the next
        // task that wants to use it
        if (mDefaultProgressListener != null && task.getProgressListener() == mDefaultProgressListener) {
            synchronized (this) {
                mDefaultProgressListenerInUse = false;
                // assign the default listener to the first task encountered that requested to use it
                for (AsyncTaskControllee t : mManagedTasks.keySet()) {
                    // if whomever submitted the task to be managed asked that it use the default progress listener, and
                    // the task is currently running
                    if (mManagedTasks.get(t) && task.isRunning()) {
                        t.setProgressListener(mDefaultProgressListener);
                        mDefaultProgressListenerInUse = true;
                        break;
                    }
                }
            }
        }
    }

    @Subscribe
    public void onActivityDestroyed(final ActivityEvents.ActivityDestroyedEvent event) {
        onDestroy();
        mLifecycleBus.unregister(this);
    }

    // handle Fragment lifecycle events
    @Subscribe
    public void onActivityAttached(final FragmentEvents.ActivityAttachedEvent event) {
        onActivityAttached(event.getFragment().getActivity());
    }

    @Subscribe
    public void onActivityDetached(final FragmentEvents.ActivityDetachedEvent event) {
        onActivityDetached();
    }

    @Subscribe
    public void onFragmentDestroyed(final FragmentEvents.FragmentDestroyedEvent event) {
        onDestroy();
        mLifecycleBus.unregister(this);
    }

    private void onActivityAttached(Activity activity) {
        for (AsyncTaskControllee task : mManagedTasks.keySet()) {
            task.onActivityAttached(activity);
        }
    }

    private void onActivityDetached() {
        for (AsyncTaskControllee task : mManagedTasks.keySet()) {
            // Note: each task is responsible for calling onActivityDetached on its progress listener, if it has one.
            task.onActivityDetached();
        }
    }

    private void onDestroy() {
        cancelAsyncTasks();
    }

    private void cancelAsyncTasks() {
        for (AsyncTaskControllee task : mManagedTasks.keySet()) {
            task.cancelTask();
        }
        mManagedTasks.clear();
    }
}
