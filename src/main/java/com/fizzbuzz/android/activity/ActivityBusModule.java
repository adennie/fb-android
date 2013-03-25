package com.fizzbuzz.android.activity;

import com.fizzbuzz.android.application.BusApplicationModule;
import com.fizzbuzz.ottoext.GuaranteedDeliveryBus;
import com.fizzbuzz.ottoext.MainThreadBus;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module(includes= BusApplicationModule.class)
public class ActivityBusModule {

    @Singleton
    @Provides
    @ActivityModule.ActivityScoped
    public GuaranteedDeliveryBus provideActivityBus(MainThreadBus bus) {
        return new GuaranteedDeliveryBus(bus, GuaranteedDeliveryBus.Policy.GUARANTEE_ON_DEMAND);
    }
}
