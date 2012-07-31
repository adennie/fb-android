package com.fizzbuzz.android.util;

/*
 * This is the interface through which an AsyncTaskJob interacts with an AsyncTaskHelper
 */
public interface AsyncTaskJobListener {
    public void publishProgress(final int progress, final String progressMessage1, final String progressMessage2);

    public boolean isTaskCancelled();
}
