package com.fizzbuzz.android.async;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;

import com.fizzbuzz.android.fragment.ProgressDialogFragment;

// This class mediates between an object that reports its progress via the ProgressListener interface and a DialogProgressFragment that displays
// the progress.
public class DialogProgressHandler
        implements ProgressListener
{
    private FragmentActivity mActivity;
    private final ProgressDialogFragment mProgressDialogFragment;

    // for an indeterminate progress dialog
    public DialogProgressHandler(final FragmentActivity activity) {
        this(activity, true, 0);
    }

    // for a horizontal or vertical bar style progress dialog
    public DialogProgressHandler(final FragmentActivity activity,
            final int style) {
        this(activity, false, style);
    }

    public DialogProgressHandler(final FragmentActivity activity,
            final boolean indeterminate,
            final int style) {
        mActivity = activity;
        mProgressDialogFragment = ProgressDialogFragment.newInstance(indeterminate, style);
        mProgressDialogFragment.show(mActivity.getSupportFragmentManager(), "progress");
    }

    @Override
    public void onStartProgress(final String title,
            final String message) {
        mProgressDialogFragment.onStartProgress(title, message);
    }

    @Override
    public void onUpdateProgress(final int progress,
            final String title,
            final String message) {
        mProgressDialogFragment.onUpdateProgress(progress, title, message);
    }

    @Override
    public void onEndProgress() {
        mProgressDialogFragment.onEndProgress();
        mProgressDialogFragment.dismiss();
    }

    @Override
    public void onUiPause() {
    }

    @Override
    public void onUiResume() {
    }

    @Override
    public void onActivityAttached(Activity activity) {
        mActivity = (FragmentActivity) activity;
    }

    @Override
    public void onActivityDetached() {
        mActivity = null;
    }

}
