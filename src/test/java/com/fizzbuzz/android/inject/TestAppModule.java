package com.fizzbuzz.android.inject;

import android.content.Context;
import com.fizzbuzz.android.application.BaseAppModule;
import com.fizzbuzz.android.application.InjectingApplication;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

// Note: this module can't include BaseAppModule.class, because inclusion requires a no-arg constructor.
// As a result, it's necessary to explicitly specify entry points (e.g. Context.class) provided by BaseAppModule
// that are used (directly or indirectly) by this module.
@Module(entryPoints = {Context.class,
        AppContextDependentThing.class,
        TestInjectingApplication.class})
public class TestAppModule
        extends BaseAppModule {

    public TestAppModule(InjectingApplication app) {
        super(app);
    }

    @Override
    @Provides
    @Singleton
    @Application
    public Context provideAppContext() {
        return super.provideAppContext();
    }
}
