package com.fizzbuzz.android.util;

import android.app.Activity;

import com.google.ads.AdView;

/* Activities using this class MUST override onDestroy and call the corresponding method on this class */
public class AdMobHelper
        extends ActivityLifecycleListenerImpl
{
    private AdView mAdView;

    public AdMobHelper(Activity activity,
            AdView adView) {
        super(activity);
        mAdView = adView;
    }

    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
            mAdView = null;
        }
        super.onDestroy();
    }

}
