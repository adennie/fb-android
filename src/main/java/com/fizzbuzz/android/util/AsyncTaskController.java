package com.fizzbuzz.android.util;

/*
 * This interface represents the AsyncTaskManager side of the contract between an AsyncTaskManager and an
 * AsyncTaskHelper
 */

public interface AsyncTaskController {
    public void onTaskCompleted(final AsyncTaskControllee task);
}
