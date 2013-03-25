package com.fizzbuzz.android.activity;

import android.content.Context;
import com.fizzbuzz.android.application.AppModule;
import com.fizzbuzz.android.application.BaseApplication;
import com.fizzbuzz.android.application.BusApplicationModule;
import com.fizzbuzz.android.application.InjectingApplication;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module(overrides = true,
        includes= BusApplicationModule.class,
        entryPoints = {TestBusActivityModule.class,
                TestBusApplication.class})
public class TestAppModule {
    private TestBusApplication mApp;

    public TestAppModule(TestBusApplication app) {
        mApp = app;
    }

    // override providers from AppModule

    @Provides
    @Singleton
    @AppModule.Application
    public Context provideAppContext() {
        return mApp;
    }

    @Provides
    @Singleton
    public BaseApplication provideBaseApplication() {
        return mApp;
    }

    @Provides
    @Singleton
    public InjectingApplication provideInjectingApplication() {
        return mApp;
    }
}
