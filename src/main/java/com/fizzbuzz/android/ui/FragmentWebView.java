package com.fizzbuzz.android.ui;


import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import com.fizzbuzz.android.fragment.BusFragment;
import com.fizzbuzz.android.fragment.FragmentEvents;
import com.squareup.otto.Subscribe;

public class FragmentWebView extends SimpleWebView {

    private FragmentActivity mActivity;
    private BusFragment mFragment;
    private boolean mFragmentDestroyed = false;

    public FragmentWebView(final Context context) {
        super(context);
    }

    public FragmentWebView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public FragmentWebView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
    }

    public void init(BusFragment fragment) {
        mFragment = fragment;
        mFragment.getFragmentBus().register(this);
    }

    @Subscribe
    public void onActivityAttached(FragmentEvents.ActivityAttachedEvent event) {
        mActivity = (FragmentActivity) event.getActivity();
    }

    @Subscribe
    public void onActivityDetached(FragmentEvents.ActivityDetachedEvent event) {
        mActivity = null;
        if (mFragmentDestroyed)
            mFragment.getFragmentBus().unregister(this);
    }

    @Subscribe
    public void onFragmentDestroyed(FragmentEvents.FragmentDestroyedEvent event) {
        destroy();
        mFragmentDestroyed = true;
    }

    @Override
    protected Activity getActivity() {
        return mActivity;
    }

}
