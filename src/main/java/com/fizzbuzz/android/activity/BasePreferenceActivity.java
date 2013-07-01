package com.fizzbuzz.android.activity;

import android.os.Bundle;
import com.fizzbuzz.android.dagger.InjectingPreferenceActivity;

public class BasePreferenceActivity
        extends InjectingPreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        BaseActivityHelper.finishIfLaunchedOverNonRootTask(this);
    }
}
