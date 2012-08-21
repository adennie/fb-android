package com.fizzbuzz.android.util;

import android.os.Bundle;

import com.google.ads.AdView;

// this class is based on http://googleadsdeveloper.blogspot.com/2012/06/show-custom-image-when-no-admob-ad-is_21.html

public class AdMobFlurryFragmentActivity
        extends FlurryFragmentActivity
{
    private AdView mAdView;

    // derived classes should call setAdView after inflation or dynamic creation of the AdView
    protected void setAdView(AdView adView) {
        mAdView = adView;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState,
            String flurryApiKey,
            boolean handleUncaughtExceptions) {
        super.onCreate(savedInstanceState, flurryApiKey, handleUncaughtExceptions);
        mAdView = null;
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
