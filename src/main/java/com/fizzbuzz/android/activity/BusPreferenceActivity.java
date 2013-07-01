package com.fizzbuzz.android.activity;

import android.os.Bundle;
import com.fizzbuzz.android.application.BusApplication;
import com.fizzbuzz.ottoext.GuaranteedDeliveryOttoBus;

import javax.inject.Inject;
import java.util.List;

// This class needs to derive from PreferenceActivity in order to override its lifecycle methods, but it delegates
// pretty much everything to BusActivityHelper, which encapsulates common implementation logic for reuse by other
// subclasses of Activity

public class BusPreferenceActivity
        extends BasePreferenceActivity
        implements ActivityLifecycle, BusManagingActivity {
    @Inject
    BusActivityHelper mBusHelper;

    public final GuaranteedDeliveryOttoBus getApplicationBus() {
        return ((BusApplication) getApplication()).getApplicationBus();
    }

    public final GuaranteedDeliveryOttoBus getVisibilityScopedApplicationBus() {
        return mBusHelper.getVisibilityScopedApplicationBus();
    }

    public final GuaranteedDeliveryOttoBus getActivityBus() {
        return mBusHelper.getActivityBus();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // does injection

        mBusHelper.onCreate(this);

        // register the global bus (this is really for the benefit of subclasses, who may want to subscribe to events)
        getApplicationBus().register(this);
    }

    @Override
    public void onRestart() {
        super.onRestart();
        mBusHelper.onRestart();
    }

    @Override
    public void onStart() {
        super.onStart();
        mBusHelper.onStart();
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mBusHelper.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mBusHelper.onResume();
    }

    @Override
    public void onPause() {
        mBusHelper.onPause();
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        mBusHelper.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStop() {
        mBusHelper.onStop();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        mBusHelper.onDestroy();
        getApplicationBus().unregister(this);
        super.onDestroy();
    }
    
    @Override
    protected List<Object> getModules() {
        List<Object> modules = super.getModules();
        modules.add(new BusActivityModule());
        return modules;
    }
}
