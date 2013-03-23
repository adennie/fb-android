package com.fizzbuzz.android.eventbus;


import com.fizzbuzz.android.activity.BusActivity;
import com.fizzbuzz.android.activity.BusActivityModule;
import com.fizzbuzz.android.activity.BusFragmentActivity;
import com.fizzbuzz.android.activity.BusPreferenceActivity;
import dagger.Module;

@Module(complete = false,
        includes = BusActivityModule.class,
        overrides = true,
        entryPoints = {
                BusActivity.class,
                BusFragmentActivity.class,
                BusPreferenceActivity.class})
public class TestBusActivityModule
        extends BusActivityModule {

}
