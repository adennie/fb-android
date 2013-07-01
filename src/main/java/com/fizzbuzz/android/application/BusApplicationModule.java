package com.fizzbuzz.android.application;

import com.fizzbuzz.android.dagger.InjectingApplication.InjectingApplicationModule.Application;
import com.fizzbuzz.ottoext.GuaranteedDeliveryBus;
import com.fizzbuzz.ottoext.MainThreadBus;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;
import dagger.Module;
import dagger.Provides;

import javax.inject.Qualifier;
import javax.inject.Singleton;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Module(library=true,
        complete=false,  // will be completed by BaseApplicationModule, which is added to the graph by BaseApplication
        injects = {BusApplication.class})
public class BusApplicationModule {

    @Provides
    public Bus provideBus() {
        return new Bus(ThreadEnforcer.ANY);
    }

    @Provides
    public MainThreadBus provideMainThreadBus(Bus bus) {
        return new MainThreadBus(bus);
    }

    @Provides
    @Singleton
    @Application
    public Bus provideApplicationScopedBus() {
        return new Bus(ThreadEnforcer.ANY);
    }

    // technically this needn't be @Singleton, as it wraps a singleton, but it's simpler this way
    @Provides
    @Singleton
    @Application
    public MainThreadBus provideApplicationMainThreadBus(@Application Bus bus) {
        return new MainThreadBus(bus);
    }

    // technically this needn't be @Singleton, as it wraps a singleton, but it's simpler this way
    @Provides
    @Singleton
    @ApplicationMainThread
    public GuaranteedDeliveryBus provideApplicationMainThreadGuaranteedDeliveryBus(@Application MainThreadBus bus) {
        return new GuaranteedDeliveryBus(bus, GuaranteedDeliveryBus.Policy.GUARANTEE_ON_DEMAND);
    }


    @Qualifier
    @Target({ FIELD, PARAMETER, METHOD })
    @Documented
    @Retention(RUNTIME)
    public @interface ApplicationMainThread {
    }

}
