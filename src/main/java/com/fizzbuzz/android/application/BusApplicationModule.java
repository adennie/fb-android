package com.fizzbuzz.android.application;

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

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Module(entryPoints = {
        GuaranteedDeliveryBus.class,
        MainThreadBus.class})
public class BusApplicationModule {

    @Provides
    public Bus provideBus() {
        return new Bus(ThreadEnforcer.NONE);
    }

    @Provides
    public MainThreadBus provideMainThreadBus(Bus bus) {
        return new MainThreadBus(bus);
    }

    @Provides
    @Singleton
    @ApplicationScoped
    public Bus provideApplicationScopedBus() {
        return new Bus(ThreadEnforcer.NONE);
    }

    // technically this needn't be @Singleton, as it wraps a singleton, but it's simpler this way
    @Provides
    @Singleton
    @ApplicationScoped
    public MainThreadBus provideApplicationScopedMainThreadBus(@ApplicationScoped Bus bus) {
        return new MainThreadBus(bus);
    }

    // technically this needn't be @Singleton, as it wraps a singleton, but it's simpler this way
    @Provides
    @Singleton
    @ApplicationScopedMainThread
    public GuaranteedDeliveryBus provideApplicationScopedMainThreadGuaranteedDeliveryBus(@ApplicationScoped MainThreadBus bus) {
        return new GuaranteedDeliveryBus(bus, GuaranteedDeliveryBus.Policy.GUARANTEE_ON_DEMAND);
    }

    @Qualifier
    @Target({ FIELD, PARAMETER, METHOD })
    @Documented
    @Retention(RUNTIME)
    public @interface ApplicationScoped {
    }

    @Qualifier
    @Target({ FIELD, PARAMETER, METHOD })
    @Documented
    @Retention(RUNTIME)
    public @interface ApplicationScopedMainThread {
    }

}
