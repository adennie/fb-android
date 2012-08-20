package com.fizzbuzz.android.util;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;

import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdRequest;
import com.google.ads.AdRequest.ErrorCode;
import com.google.ads.AdView;

// this class is based on http://googleadsdeveloper.blogspot.com/2012/06/show-custom-image-when-no-admob-ad-is_21.html

public class AdMobFragmentActivity
        extends FragmentActivity
        implements AdListener
{
    private AdView mAdView;
    private boolean mFirstAdReceived = false;
    private final Handler refreshHandler = new Handler();
    private final Runnable refreshRunnable = new RefreshRunnable();

    protected void setAdView(AdView adView) {
        mAdView = adView;
    }

    protected void onCreate(Bundle savedInstanceState,
            int adViewId) {
        super.onCreate(savedInstanceState);
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

    @Override
    public void onReceiveAd(Ad ad) {
        mFirstAdReceived = true;
    }

    @Override
    public void onFailedToReceiveAd(Ad ad,
            ErrorCode code) {
        if (!mFirstAdReceived) {
            // Schedule an ad refresh.
            refreshHandler.removeCallbacks(refreshRunnable);
            refreshHandler.postDelayed(refreshRunnable, 15 * 1000);
        }
    }

    @Override
    public void onDismissScreen(Ad arg0) {
    }

    @Override
    public void onLeaveApplication(Ad arg0) {
    }

    @Override
    public void onPresentScreen(Ad arg0) {
    }

    private class RefreshRunnable
            implements Runnable {
        @Override
        public void run() {
            // Load an ad with an ad request.
            if (mAdView != null)
                mAdView.loadAd(new AdRequest());
        }
    }
}
