package com.fizzbuzz.android.inject;

import android.content.Context;
import com.fizzbuzz.android.application.BaseAppModule.Application;

import javax.inject.Inject;

public class AppContextDependentThing {
    @Inject @Application Context mAppContext;
}
