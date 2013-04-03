package com.fizzbuzz.android.async;

import android.support.v4.app.Fragment;
import com.fizzbuzz.android.fragment.BusManagingFragment;
import com.fizzbuzz.android.fragment.FragmentEvents;
import com.squareup.otto.OttoBus;
import com.squareup.otto.Subscribe;

public class TitleBarIndeterminateProgressHandler
        implements ProgressListener {

    private Fragment mFragment;
    private OttoBus mBus;
    private boolean mInProgress = false;
    private boolean mFragmentDestroyed = false;

    public TitleBarIndeterminateProgressHandler(final BusManagingFragment fragment) {
        mFragment = (Fragment)fragment;
        mBus = fragment.getFragmentBus();
        mBus.register(this);
    }

    @Override
    public void onStartProgress(final String message1,
            final String message2) {
        mInProgress = true;
        mFragment.getActivity().setProgressBarIndeterminateVisibility(true);
    }

    @Override
    public void onUpdateProgress(final int progress,
            final String message1,
            final String message2) {
    }

    @Override
    public void onEndProgress() {
        mInProgress = false;
        mFragment.getActivity().setProgressBarIndeterminateVisibility(false);
    }


    @Subscribe
    public void onActivityAttached(FragmentEvents.ActivityAttachedEvent event) {
        if (mInProgress)
            mFragment.getActivity().setProgressBarIndeterminateVisibility(true);
    }

    @Subscribe
    public void onActivityDetached(FragmentEvents.ActivityDetachedEvent event) {
        if (mInProgress)
            mFragment.getActivity().setProgressBarIndeterminateVisibility(false);
        if (mFragmentDestroyed)
            mBus.unregister(this);
    }

    @Subscribe
    public void onFragmentDestroyed(FragmentEvents.FragmentDestroyedEvent event) {
        mFragmentDestroyed = true;
    }
}
