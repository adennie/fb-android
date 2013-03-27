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

@Module
public class InjectingApplicationModule {
    InjectingApplication mApp;

    public InjectingApplicationModule(InjectingApplication app) {
        mApp = app;
    }

    @Provides
    @Singleton
    @Application
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
