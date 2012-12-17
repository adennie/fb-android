package com.fizzbuzz.android.async;

import android.app.Activity;

public interface ProgressListener {

    /**
     * Notifies an interested party that a task is about to commence execution
     * 
     * @param message1 a 1st-level description of the work that is about to start (optional, may be null)
     * @param message2 a 2nd-level description of the work that is about to start (optional, may be null)
     */
    void onStartProgress(String message1,
            String message2);

    /**
     * Notifies an interested party regarding the ongoing progress of an AsyncTaskJob executed by an AsyncTaskHelper
     * 
     * @param progress the degree of progress completed
     * @param message1 a 1st-level description of the work that is in progress (optional, may be null)
     * @param message2 a 2nd-level description of the work that is in progress (optional, may be null)
     */
    void onUpdateProgress(int progress,
            String message1,
            String message2);

    /**
     * Notifies an interested party that a task has completed
     */
    void onEndProgress();

    void onUiPause();

    void onUiResume();

    /*
     * Progress Listener's may or may not have dependencies on Activity instances. Since Activities have a habit of going away (e.g. during device
     * rotations) the listeners need to be notified when that happens, so they know to release the old Activity when it goes away and use the new one
     * when it is attached. Note that since a given instance of a listener may be assigned to more than one AsyncTaskHelper, its onActivityAttached
     * and onActivityDetached method may be invoked multiple times for the same occurrence of the activity being attached or detached.
     */
    void onActivityAttached(Activity activity);

    void onActivityDetached();
}
