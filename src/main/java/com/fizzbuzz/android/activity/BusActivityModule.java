package com.fizzbuzz.android.activity;

import com.fizzbuzz.android.application.BusApplicationModule;
import com.fizzbuzz.android.dagger.InjectingActivityModule;
import com.fizzbuzz.ottoext.GuaranteedDeliveryBus;
import com.fizzbuzz.ottoext.MainThreadBus;
import com.fizzbuzz.ottoext.ScopedGuaranteedDeliveryBus;
import com.squareup.otto.Bus;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module(includes= BusApplicationModule.class,
        complete=false, // completed by BaseApplicationModule, which is added by BaseApplication at runtime
        injects = {BusActivity.class,
                BusActivityHelper.class,
                BusFragmentActivity.class,
                BusPreferenceActivity.class})
public class BusActivityModule  {

    @Singleton
    @Provides
    @InjectingActivityModule.Activity
    public MainThreadBus provideMainThreadBus() {
        return new MainThreadBus(new Bus());
    }

    @Singleton
    @Provides
    @InjectingActivityModule.Activity
    public GuaranteedDeliveryBus provideGuaranteedDeliveryBus(@InjectingActivityModule.Activity MainThreadBus bus) {
        return new GuaranteedDeliveryBus(bus, GuaranteedDeliveryBus.Policy.GUARANTEE_ON_DEMAND);
    }

    @Singleton
    @Provides
    @InjectingActivityModule.Activity
    public ScopedGuaranteedDeliveryBus provideActivityBus(@InjectingActivityModule.Activity GuaranteedDeliveryBus bus) {
        return new ScopedGuaranteedDeliveryBus(bus);
    }
}
