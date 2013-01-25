package com.fizzbuzz.android.fragment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/*
 * A Fragment for showing the content of an individual news item. On smaller screens, this fragment is hosted within a
 * NewsItemActivity; on larger ones, it is hosted by a NewsItemsActivity and appears alongside the NewsItemsFragment
 * also hosted by that activity.
 */
public class ProgressDialogFragment
        extends DialogFragment {
    private final Logger mLogger = LoggerFactory.getLogger(LoggingManager.TAG);
    private ProgressDialog mProgressDialog;
    private boolean mIndeterminate;
    private int mStyle;

    public static String ARG_INDETERMINATE = "com.fizzbuzz.android.util.ProgressDialogFragment.indeterminate";
    public static String ARG_STYLE = "com.fizzbuzz.android.util.ProgressDialogFragment.style";

    public static ProgressDialogFragment newInstance(final boolean indeterminate,
            final int style) {
        ProgressDialogFragment frag = new ProgressDialogFragment();

        Bundle args = new Bundle();
        args.putBoolean(ARG_INDETERMINATE, indeterminate);
        args.putInt(ARG_STYLE, style);
        frag.setArguments(args);

        return frag;
    }

    // Empty constructor required for DialogFragment
    public ProgressDialogFragment() {
        mIndeterminate = true;
        mStyle = 0;
    }

    @Override
    public void onAttach(final Activity activity) {
        mLogger.info("ProgressDialogFragment.onAttach: in fragment: {}", this);
        super.onAttach(activity);

    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        mLogger.info("ProgressDialogFragment.onCreateDialog: in fragment: {}", this);
        if (savedInstanceState != null)
            initStateFromBundle(savedInstanceState);
        else
            initStateFromBundle(getArguments());

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setIndeterminate(mIndeterminate);
        if (!mIndeterminate)
            mProgressDialog.setProgressStyle(mStyle);
        return mProgressDialog;
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mLogger.info("ProgressDialogFragment.onActivityCreated: in fragment: {}", this);

    }

    @Override
    public void onDestroyView() {
        mLogger.info("ProgressDialogFragment.onDestroyView: in fragment: {}", this);

        mProgressDialog.cancel();
        mProgressDialog = null; // facilitate GC

        super.onDestroyView();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveStateToBundle(outState);
    }

    @Override
    public void onDestroy() {
        mLogger.info("ProgressDialogFragment.onDestroy: in fragment: {}", this);
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        mLogger.info("ProgressDialogFragment.onDetach: in fragment: {}", this);
        super.onDetach();
    }

    public void onStartProgress(String title,
            String message) {
        if (mProgressDialog != null) {
            if (title != null)
                mProgressDialog.setTitle(title);
            if (message != null)
                mProgressDialog.setMessage(message);
            mProgressDialog.show();
        }
    }

    public void onUpdateProgress(int progress,
            String title,
            String message) {
        if (mProgressDialog != null) {
            if (title != null)
                mProgressDialog.setTitle(title);
            if (message != null)
                mProgressDialog.setMessage(message);
            mProgressDialog.setProgress(progress);
        }

    }

    public void onEndProgress() {
        if (mProgressDialog != null)
            mProgressDialog.cancel();
    }

    private void initStateFromBundle(final Bundle args) {
        mIndeterminate = args.getBoolean(ARG_INDETERMINATE);
        mStyle = args.getInt(ARG_STYLE);
    }

    private void saveStateToBundle(final Bundle outState) {
        outState.putBoolean(ARG_INDETERMINATE, mIndeterminate);
        outState.putInt(ARG_STYLE, mStyle);
    }
}
