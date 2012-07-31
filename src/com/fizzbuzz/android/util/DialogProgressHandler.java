package com.fizzbuzz.android.util;

import android.app.ProgressDialog;
import android.content.Context;

public class DialogProgressHandler
        extends BaseProgressListener
{

    private final ProgressListener mNextListener;
    private final ProgressDialog mProgressDialog;

    // for an indeterminate progress dialog with no next listener
    public DialogProgressHandler(final Context context) {
        this(context, null);
    }

    // for an indeterminate progress dialog with a next listener
    public DialogProgressHandler(final Context context, final ProgressListener nextListener) {
        this(context, true, 0, nextListener);
    }

    // for a horizontal or vertical bar style progress dialog with no next listener
    public DialogProgressHandler(final Context context, final int style) {
        this(context, false, style, null);
    }

    // for a horizontal or vertical bar style progress dialog with a next listener
    public DialogProgressHandler(final Context context, final int style, final ProgressListener nextListener) {
        this(context, false, style, nextListener);
    }

    public DialogProgressHandler(final Context context, final boolean indeterminate, final int style,
            final ProgressListener nextListener) {
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setIndeterminate(indeterminate);
        if (!indeterminate)
            mProgressDialog.setProgressStyle(style);
        mNextListener = nextListener;
    }

    @Override
    public void onStartProgress(final String title, final String message) {
        if (title != null)
            mProgressDialog.setTitle(title);
        if (message != null)
            mProgressDialog.setMessage(message);
        mProgressDialog.show();

        if (mNextListener != null)
            mNextListener.onStartProgress(title, message);
    }

    @Override
    public void onUpdateProgress(final int progress, final String title, final String message) {
        if (title != null)
            mProgressDialog.setTitle(title);
        if (message != null)
            mProgressDialog.setMessage(message);
        mProgressDialog.setProgress(progress);

        if (mNextListener != null)
            mNextListener.onUpdateProgress(progress, title, message);
    }

    @Override
    public void onEndProgress() {
        mProgressDialog.dismiss();
        if (mNextListener != null)
            mNextListener.onEndProgress();
    }

    @Override
    public void onUiPause() {
        mProgressDialog.hide();
        if (mNextListener != null)
            mNextListener.onUiPause();
    }

    @Override
    public void onUiResume() {
        mProgressDialog.show();
        if (mNextListener != null)
            mNextListener.onUiResume();
    }

}
