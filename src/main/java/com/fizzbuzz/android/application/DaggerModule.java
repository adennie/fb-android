package com.fizzbuzz.android.application;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.inject.Qualifier;
import javax.inject.Singleton;

import com.fizzbuzz.android.activity.BusActivity;
import com.fizzbuzz.android.activity.BusFragmentActivity;
import com.fizzbuzz.android.activity.BusPreferenceActivity;
import com.fizzbuzz.android.async.AsyncTaskManager;
import com.fizzbuzz.android.fragment.BusFragment;
import com.fizzbuzz.android.fragment.BusListFragment;
import com.fizzbuzz.android.gcm.GcmBusIntentService;
import com.fizzbuzz.android.socialize.SocializeGcmEventHandler;
import com.fizzbuzz.ottoext.GuaranteedDeliveryBus;
import com.fizzbuzz.ottoext.MainThreadBus;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import dagger.Module;
import dagger.Provides;

@Module(entryPoints = {
        AsyncTaskManager.class,
        BaseApplication.class,
        BusActivity.class,
        BusFragment.class,
        BusFragmentActivity.class,
        BusListFragment.class,
        BusPreferenceActivity.class,
        GcmBusIntentService.class,
        GuaranteedDeliveryBus.class,
        MainThreadBus.class,
        SocializeGcmEventHandler.class })
public class DaggerModule {
    @Provides
    @Singleton
    @Global
    MainThreadBus provideGlobalMainThreadBus() {
        return new MainThreadBus(new Bus(ThreadEnforcer.NONE));
    }

    @Provides
    @Singleton
    @GlobalMainThread
    GuaranteedDeliveryBus provideGlobalMainThreadGuaranteedDeliveryBus(@Global MainThreadBus globalMainThreadBus) {
        return new GuaranteedDeliveryBus(globalMainThreadBus);
    }

    @Qualifier
    @Target({ FIELD, PARAMETER, METHOD })
    @Documented
    @Retention(RUNTIME)
    public @interface Global {
    }

    @Qualifier
    @Target({ FIELD, PARAMETER, METHOD })
    @Documented
    @Retention(RUNTIME)
    public @interface GlobalMainThread {
    }

}
