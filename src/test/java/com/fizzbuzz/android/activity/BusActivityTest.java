package com.fizzbuzz.android.activity;

import org.robolectric.Robolectric;
import org.robolectric.util.ActivityController;

public class BusActivityTest extends AbstractBusActivityTest<BusActivity> {
    @Override
    protected ActivityController<BusActivity> getActivityController() {
        return Robolectric.buildActivity(BusActivity.class);
    }

}
