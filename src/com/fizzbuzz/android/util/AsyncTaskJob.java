package com.fizzbuzz.android.util;

/*
 * This interface must be implemented by jobs as part of their contract with AsyncTaskHelper
 */
public interface AsyncTaskJob<Result> {
    public Result doJob(AsyncTaskJobListener jobListener);
}