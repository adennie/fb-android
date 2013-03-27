package com.fizzbuzz.android.activity;

import com.fizzbuzz.android.application.BusApplicationModule;
import com.fizzbuzz.ottoext.GuaranteedDeliveryBus;
import com.fizzbuzz.ottoext.MainThreadBus;
import com.fizzbuzz.ottoext.ScopedGuaranteedDeliveryBus;
import com.squareup.otto.Bus;
import com.fizzbuzz.android.activity.InjectingActivityModule.ActivityScoped;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module(includes= BusApplicationModule.class,
        entryPoints = {BusActivity.class,
                BusActivityHelper.class,
                BusFragmentActivity.class,
                BusPreferenceActivity.class})
public class BusActivityModule  {

    @Singleton
    @Provides
    @ActivityScoped
    public MainThreadBus provideMainThreadBus() {
        return new MainThreadBus(new Bus());
    }

    @Singleton
    @Provides
    @ActivityScoped
    public GuaranteedDeliveryBus provideGuaranteedDeliveryBus(@ActivityScoped MainThreadBus bus) {
        return new GuaranteedDeliveryBus(bus, GuaranteedDeliveryBus.Policy.GUARANTEE_ON_DEMAND);
    }

    @Singleton
    @Provides
    @ActivityScoped
    public ScopedGuaranteedDeliveryBus provideActivityBus(@ActivityScoped GuaranteedDeliveryBus bus) {
        return new ScopedGuaranteedDeliveryBus(bus);
    }
}
