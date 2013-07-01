package com.fizzbuzz.android.activity;

import android.os.Bundle;
import com.fizzbuzz.android.dagger.InjectingActivity;

public class BaseActivity
        extends InjectingActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        BaseActivityHelper.finishIfLaunchedOverNonRootTask(this);
    }
}
