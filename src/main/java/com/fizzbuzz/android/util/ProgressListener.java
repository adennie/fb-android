package com.fizzbuzz.android.util;

public interface ProgressListener {

    /**
     * Notifies an interested party that a task is about to commence execution
     * 
     * @param message1 a 1st-level description of the work that is about to start (optional, may be null)
     * @param message2 a 2nd-level description of the work that is about to start (optional, may be null)
     */
    void onStartProgress(String message1, String message2);

    /**
     * Notifies an interested party regarding the ongoing progress of an AsyncTaskJob executed by an AsyncTaskHelper
     * 
     * @param progress the degree of progress completed
     * @param message1 a 1st-level description of the work that is in progress (optional, may be null)
     * @param message2 a 2nd-level description of the work that is in progress (optional, may be null)
     */
    void onUpdateProgress(int progress, String message1, String message2);

    /**
     * Notifies an interested party that a task has completed
     */
    void onEndProgress();

    void onUiPause();

    void onUiResume();

    boolean onDefaultListenerAvailable(ProgressListener defaultListener);

    boolean ownsDefaultListener();
}
