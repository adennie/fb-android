package com.fizzbuzz.android.activity;

import com.fizzbuzz.android.injection.Injector;

public interface ActivityInjector  extends Injector {
    public void setActivityModuleClass(Class<? extends BaseActivityModule> activityModuleClass);
}
