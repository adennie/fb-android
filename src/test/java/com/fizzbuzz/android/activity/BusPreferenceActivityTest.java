package com.fizzbuzz.android.activity;

import org.robolectric.Robolectric;
import org.robolectric.util.ActivityController;

public class BusPreferenceActivityTest extends AbstractBusActivityTest<BusPreferenceActivity>{
    @Override
    protected ActivityController<BusPreferenceActivity> getActivityController() {
        return Robolectric.buildActivity(BusPreferenceActivity.class);
    }}
