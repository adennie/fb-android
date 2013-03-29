package com.fizzbuzz.android.activity;

import com.fizzbuzz.android.async.AsyncTaskManager;
import com.fizzbuzz.android.activity.InjectingActivityModule.ActivityScoped;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;


@Module
public class AsyncActivityModule {
    @Provides
    @Singleton
    @ActivityScoped
    public AsyncTaskManager provideActivityScopedAsyncTaskManager() {
        return new AsyncTaskManager();
    }
}
