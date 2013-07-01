package com.fizzbuzz.android;

import com.fizzbuzz.android.application.BusApplication;
import org.junit.runners.model.InitializationError;
import org.robolectric.AndroidManifest;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.TestLifecycle;

import java.lang.reflect.Method;

public class BusApplicationTestRunner extends RobolectricTestRunner {

    public BusApplicationTestRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
    }

    @Override
    protected Class<? extends TestLifecycle> getTestLifecycleClass() {
        return BusApplicationLifecycle.class;
    }

    public static class BusApplicationLifecycle implements TestLifecycle<BusApplication> {

        @Override
        public BusApplication createApplication(Method method, AndroidManifest appManifest) {
            return new BusApplication();
        }

        @Override
        public void beforeTest(Method method) {
        }

        @Override
        public void prepareTest(Object test) {
        }

        @Override
        public void afterTest(Method method) {
        }
    }

}