package com.fizzbuzz.android.fragment;

import com.fizzbuzz.android.async.AsyncTaskManager;
import com.fizzbuzz.android.fragment.InjectingFragmentModule.FragmentScoped;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;


@Module
public class AsyncFragmentModule {
    @Provides
    @Singleton
    @FragmentScoped
    public AsyncTaskManager provideFragmentScopedAsyncTaskManager() {
        return new AsyncTaskManager();
    }
}
