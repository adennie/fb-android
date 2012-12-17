package com.fizzbuzz.android.async;

import android.app.Activity;

/*
 * This is the callback interface invoked after an AsyncTaskHelper's task completes. Note that an Activity is pass to the callbacks because the
 * activity context in place at the time the result processor was created may not be the one that's in place when it is invoked (e.g. if the device
 * was rotated while the AsyncTask was running).
 */
public interface AsyncTaskResultProcessor<Result> {
    void onActivityAttached(Activity activity);

    void onActivityDetached();

    void processException(Exception e);

    void processResult(Result result);
}
