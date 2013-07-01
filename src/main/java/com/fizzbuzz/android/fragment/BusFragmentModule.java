package com.fizzbuzz.android.fragment;

import com.fizzbuzz.android.activity.BusActivityModule;
import com.fizzbuzz.android.dagger.InjectingFragmentModule;
import com.fizzbuzz.ottoext.GuaranteedDeliveryBus;
import com.fizzbuzz.ottoext.MainThreadBus;
import com.fizzbuzz.ottoext.ScopedGuaranteedDeliveryBus;
import com.squareup.otto.Bus;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module(includes = BusActivityModule.class,
        complete=false,
        injects = {BusFragment.class,
                BusFragmentHelper.class,
                BusListFragment.class})
public class BusFragmentModule {

    @Singleton
    @Provides
    @InjectingFragmentModule.Fragment
    public MainThreadBus provideMainThreadBus() {
        return new MainThreadBus(new Bus());
    }

    @Singleton
    @Provides
    @InjectingFragmentModule.Fragment
    public GuaranteedDeliveryBus provideGuaranteedDeliveryBus(@InjectingFragmentModule.Fragment MainThreadBus bus) {
        return new GuaranteedDeliveryBus(bus, GuaranteedDeliveryBus.Policy.GUARANTEE_ON_DEMAND);
    }

    @Singleton
    @Provides
    @InjectingFragmentModule.Fragment
    public ScopedGuaranteedDeliveryBus provideFragmentBus(@InjectingFragmentModule.Fragment GuaranteedDeliveryBus bus) {
        return new ScopedGuaranteedDeliveryBus(bus);
    }
}
