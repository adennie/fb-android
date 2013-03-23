package com.fizzbuzz.android.application;

import android.content.Context;
import dagger.Module;
import dagger.Provides;

import javax.inject.Qualifier;
import javax.inject.Singleton;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

// Note: deriving modules can't include this module, because inclusion requires a no-arg constructor.
// As a result, it's necessary for those deriving modules to explicitly specify entry points (e.g. Context.class)
// provided by this module that are used (directly or indirectly) by the deriving module.

@Module
public class BaseAppModule {
    InjectingApplication mApp;

    public BaseAppModule(InjectingApplication app) {
        mApp = app;
    }

    // workaround for https://github.com/square/dagger/issues/188 - override in derived class and call super
//    @Provides
//    @Singleton
//    @Application
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

    @Qualifier
    @Target({FIELD, PARAMETER, METHOD})
    @Documented
    @Retention(RUNTIME)
    public @interface Application {
    }
}
