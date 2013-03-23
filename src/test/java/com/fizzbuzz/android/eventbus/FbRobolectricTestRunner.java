package com.fizzbuzz.android.eventbus;

import android.app.Application;
import org.junit.runners.model.InitializationError;
import org.robolectric.RobolectricTestRunner;

public class FbRobolectricTestRunner
        extends RobolectricTestRunner {

    public FbRobolectricTestRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
    }

    @Override
    protected Application createApplication() {
        return new TestBusApplication();
    }
}