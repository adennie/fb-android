package com.fizzbuzz.android.gcm;

import com.fizzbuzz.android.application.BusApplicationModule;

import dagger.Module;

@Module(includes = BusApplicationModule.class, entryPoints = GcmBusIntentService.class)
public class GcmModule {
}
