package com.fizzbuzz.android.activity;

import android.app.Application;
import org.junit.runners.model.InitializationError;
import org.robolectric.RobolectricTestRunner;

public class TestBusAppRobolectricTestRunner
        extends RobolectricTestRunner {

    public TestBusAppRobolectricTestRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
    }

    @Override
    protected Application createApplication() {
        return new TestBusApplication();
    }
}