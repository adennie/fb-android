package com.fizzbuzz.android.activity;

import com.fizzbuzz.android.application.BusApplication;

import java.util.List;

public class TestBusApplication
        extends BusApplication {

    @Override
    protected List<Object> getModules() {
        List<Object> modules = super.getModules();
        modules.add(new TestAppModule(this));
        return modules;
    }
}