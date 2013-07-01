package com.fizzbuzz.android.activity;

import org.robolectric.Robolectric;
import org.robolectric.util.ActivityController;

public class BusFragmentActivityTest extends AbstractBusActivityTest<BusFragmentActivity>{
    @Override
    protected ActivityController<BusFragmentActivity> getActivityController() {
        return Robolectric.buildActivity(BusFragmentActivity.class);
    }
}
