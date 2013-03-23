package com.fizzbuzz.android.inject;

import com.fizzbuzz.android.application.InjectingApplication;
import com.fizzbuzz.android.eventbus.TestAppModule;

import java.util.List;

public class TestInjectingApplication
        extends InjectingApplication {

    @Override
    protected List<Object> getModules() {
        List<Object> modules = super.getModules();
        modules.add(new TestAppModule(this));
        return modules;
    }
}