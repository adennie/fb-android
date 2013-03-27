package com.fizzbuzz.android.fragment;

import com.fizzbuzz.android.activity.BusActivityModule;
import com.fizzbuzz.android.fragment.InjectingFragmentModule.FragmentScoped;
import com.fizzbuzz.ottoext.GuaranteedDeliveryBus;
import com.fizzbuzz.ottoext.MainThreadBus;
import com.fizzbuzz.ottoext.ScopedGuaranteedDeliveryBus;
import com.squareup.otto.Bus;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module(includes = BusActivityModule.class,
        entryPoints = {BusFragment.class,
                BusFragmentHelper.class,
                BusListFragment.class})
public class BusFragmentModule {

    @Singleton
    @Provides
    @FragmentScoped
    public MainThreadBus provideMainThreadBus() {
        return new MainThreadBus(new Bus());
    }

    @Singleton
    @Provides
    @FragmentScoped
    public GuaranteedDeliveryBus provideGuaranteedDeliveryBus(@FragmentScoped MainThreadBus bus) {
        return new GuaranteedDeliveryBus(bus, GuaranteedDeliveryBus.Policy.GUARANTEE_ON_DEMAND);
    }

    @Singleton
    @Provides
    @FragmentScoped
    public ScopedGuaranteedDeliveryBus provideFragmentBus(@FragmentScoped GuaranteedDeliveryBus bus) {
        return new ScopedGuaranteedDeliveryBus(bus);
    }
}
