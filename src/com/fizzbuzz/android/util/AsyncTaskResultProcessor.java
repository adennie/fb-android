package com.fizzbuzz.android.util;

/*
 * This is the callback interface invoked after an AsyncTaskHelper's task completes
 */
public interface AsyncTaskResultProcessor<Result> {
    void processException(Exception e);

    void processResult(Result result);
}
