package com.fizzbuzz.android.activity;

import com.fizzbuzz.android.async.AsyncTaskManager;
import com.fizzbuzz.android.dagger.InjectingActivityModule;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;


@Module(library=true)
public class AsyncActivityModule {
    @Provides
    @Singleton
    @InjectingActivityModule.Activity
    public AsyncTaskManager provideActivityScopedAsyncTaskManager() {
        return new AsyncTaskManager();
    }
}
