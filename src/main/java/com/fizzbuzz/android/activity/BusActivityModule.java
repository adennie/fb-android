package com.fizzbuzz.android.activity;

import com.fizzbuzz.android.application.BusApplicationModule;
import com.fizzbuzz.ottoext.GuaranteedDeliveryBus;
import com.fizzbuzz.ottoext.MainThreadBus;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;
// Note: no need to list BusActivity.class, BusFragmentActivity.class, BusPreferenceActivity.class, etc. as
// entry points here, as they are not the actual concrete classes that get injected -- their derived classes are.
@Module(complete = false,
        includes = BusApplicationModule.class,
        entryPoints = {BusActivityHelper.class})
public class BusActivityModule
        extends BaseActivityModule {

    @Singleton
    @Provides
    @ActivityScoped
    public GuaranteedDeliveryBus provideActivityBus(MainThreadBus bus) {
        return new GuaranteedDeliveryBus(bus, GuaranteedDeliveryBus.Policy.GUARANTEE_ON_DEMAND);
    }
}
