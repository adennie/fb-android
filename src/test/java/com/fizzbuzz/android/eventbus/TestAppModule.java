package com.fizzbuzz.android.eventbus;

import android.content.Context;
import com.fizzbuzz.android.application.BaseAppModule;
import com.fizzbuzz.android.application.BusApplicationModule;
import com.fizzbuzz.android.application.InjectingApplication;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

// Note: this module can't include BaseAppModule.class, because inclusion requires a no-arg constructor.
// As a result, it's necessary to explicitly specify entry points (e.g. Context.class) provided by BaseAppModule
// that are used (directly or indirectly) by this module.
@Module(includes = BusApplicationModule.class,
        overrides = true,
        entryPoints = {Context.class,
                TestBusActivityModule.class,
                TestBusApplication.class})
public class TestAppModule
        extends BaseAppModule {

    public TestAppModule(InjectingApplication app) {
        super(app);
    }

    // each app's graph needs to implement module providers corresponding to each Injecting*Activity-derived Activity
    @Provides
    @Singleton
    public TestBusActivityModule provideTestBusActivityModule() {
        return new TestBusActivityModule();
    }
}
