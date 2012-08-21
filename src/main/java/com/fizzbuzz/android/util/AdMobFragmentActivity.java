package com.fizzbuzz.android.util;

import android.support.v4.app.FragmentActivity;

import com.google.ads.AdView;

public class AdMobFragmentActivity
        extends FragmentActivity
{
    private AdView mAdView;

    protected void setAdView(AdView adView) {
        mAdView = adView;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAdView != null) {
            mAdView.destroy();
            mAdView = null;
        }
    }

}
