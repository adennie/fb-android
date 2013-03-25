package com.fizzbuzz.android.activity;

import android.app.Activity;

import javax.inject.Qualifier;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

public interface ActivityModule {

    public void setActivity(final Activity activity);
    public Activity provideActivity();

    @Qualifier
    @Target({FIELD, PARAMETER, METHOD})
    @Documented
    @Retention(RUNTIME)
    public @interface ActivityScoped {
    }
}
