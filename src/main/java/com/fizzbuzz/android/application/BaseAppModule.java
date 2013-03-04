package com.fizzbuzz.android.application;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.inject.Qualifier;
import javax.inject.Singleton;

import android.content.Context;
import dagger.Module;
import dagger.Provides;

@Module(entryPoints = Context.class)
public class BaseAppModule {
    final Context mAppContext;

    public BaseAppModule(final Context appContext) {
        mAppContext = appContext;
    }

    @Provides
    @Singleton
    @Application
    protected Context provideAppContext() {
        return mAppContext;
    }

    @Qualifier
    @Target({ FIELD, PARAMETER, METHOD })
    @Documented
    @Retention(RUNTIME)
    public @interface Application {
    }
}
