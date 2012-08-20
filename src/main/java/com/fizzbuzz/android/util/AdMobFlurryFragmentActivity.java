package com.fizzbuzz.android.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.os.Bundle;
import android.os.Handler;

import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdRequest;
import com.google.ads.AdRequest.ErrorCode;
import com.google.ads.AdView;

// this class is based on http://googleadsdeveloper.blogspot.com/2012/06/show-custom-image-when-no-admob-ad-is_21.html

public class AdMobFlurryFragmentActivity
        extends FlurryFragmentActivity
        implements AdListener
{
    private final Logger mLogger = LoggerFactory.getLogger(LoggingManager.TAG);

    private AdView mAdView;
    private boolean mFirstAdReceived = false;
    private final Handler refreshHandler = new Handler();
    private final Runnable refreshRunnable = new RefreshRunnable();

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

    // implement AdListener interface

    @Override
    public void onReceiveAd(Ad ad) {
        mFirstAdReceived = true;
    }

    @Override
    public void onFailedToReceiveAd(Ad ad,
            ErrorCode code) {
        if (!mFirstAdReceived) {
            mLogger.debug("AdMobFlurryFragmentActivity:onFailedToReceiveAd: have not received activity's 1st ad yet.  Scheduling an ad refresh");

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
            mLogger.debug("AdMobFlurryFragmentActivity:RefreshRunnable:run: loading ad");
            if (mAdView != null)
                mAdView.loadAd(new AdRequest());
        }
    }

}
