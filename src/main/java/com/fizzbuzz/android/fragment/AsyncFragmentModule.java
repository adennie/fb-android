package com.fizzbuzz.android.fragment;

import com.fizzbuzz.android.async.AsyncTaskManager;
import com.fizzbuzz.android.dagger.InjectingFragmentModule;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;


@Module(library=true)
public class AsyncFragmentModule {
    @Provides
    @Singleton
    @InjectingFragmentModule.Fragment
    public AsyncTaskManager provideFragmentScopedAsyncTaskManager() {
        return new AsyncTaskManager();
    }
}
