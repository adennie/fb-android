package com.fizzbuzz.android.service;

import com.fizzbuzz.android.application.BusApplication;
import com.fizzbuzz.android.dagger.InjectingService;
import com.fizzbuzz.ottoext.GuaranteedDeliveryBus;
import com.fizzbuzz.ottoext.GuaranteedDeliveryOttoBus;
import com.fizzbuzz.ottoext.MainThreadBus;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;
import dagger.Module;
import dagger.Provides;

import javax.inject.Inject;
import javax.inject.Qualifier;
import javax.inject.Singleton;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.List;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

abstract public class BusService
        extends InjectingService {

    @Inject @BusServiceModule.ServiceMainThread GuaranteedDeliveryBus mServiceBus;

    public final GuaranteedDeliveryOttoBus getApplicationBus() {
        return ((BusApplication) getApplication()).getApplicationBus();
    }

    public final GuaranteedDeliveryOttoBus getServiceBus() {
        return mServiceBus;
    }

    @Override
    public void onCreate() {
        super.onCreate(); // does injection

        // register with the application bus (this is really for the benefit of subclasses,
        // who may want to subscribe to events)
        getApplicationBus().register(this);
    }

    @Override
    protected List<Object> getModules() {
        List<Object> modules = super.getModules();
        modules.add(new BusServiceModule());
        return modules;
    }

    @Override
    public void onDestroy() {
        getApplicationBus().unregister(this);

        super.onDestroy();
    }

    @Module(library = true,
            injects = {BusService.class})
    public static class BusServiceModule {

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
        @InjectingServiceModule.Service
        public Bus provideServiceBus() {
            return new Bus(ThreadEnforcer.ANY);
        }

        // technically this needn't be @Singleton, as it wraps a singleton, but it's simpler this way
        @Provides
        @Singleton
        @InjectingServiceModule.Service
        public MainThreadBus provideServiceMainThreadBus(@InjectingServiceModule.Service Bus bus) {
            return new MainThreadBus(bus);
        }

        // technically this needn't be @Singleton, as it wraps a singleton, but it's simpler this way
        @Provides
        @Singleton
        @ServiceMainThread
        public GuaranteedDeliveryBus provideServiceMainThreadGuaranteedDeliveryBus(@InjectingServiceModule.Service
                                                                                           MainThreadBus bus) {
            return new GuaranteedDeliveryBus(bus, GuaranteedDeliveryBus.Policy.GUARANTEE_ON_DEMAND);
        }


        @Qualifier
        @Target({FIELD, PARAMETER, METHOD})
        @Documented
        @Retention(RUNTIME)
        public @interface ServiceMainThread {
        }
    }
}
