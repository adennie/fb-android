package com.fizzbuzz.android.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;

/*
 * A general-purpose AlertDialog-based DialogFragment that shows a message and an OK button
 */
public class AlertDialogFragment
        extends DialogFragment {
    private final Logger mLogger = LoggerFactory.getLogger(LoggingManager.TAG);
    private String mMessage;

    public static AlertDialogFragment newInstance(String message) {
        AlertDialogFragment frag = new AlertDialogFragment();
        frag.setMessage(message);
        return frag;
    }

    /*
     * Show an AlertDialogFragment only if it has never been shown before. The supplied "tag" argument must be unique to
     * the message being shown; it is used as a both a shared preference key and as the fragment tag provided to the
     * FragmentManager.
     */
    public static void showOnce(FragmentActivity activity, String tag, int messageResourceId) {
        if (!SharedPreferencesUtils.getBooleanNoStrict(activity, tag, false)) {

            AlertDialogFragment alert = AlertDialogFragment
                    .newInstance(activity.getString(messageResourceId));

            alert.show(activity.getSupportFragmentManager(), tag);

            SharedPreferencesUtils.setBooleanNoStrict(activity, tag, true);// remember not to show this again
        }
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mLogger.info("AlertDialogFragment.onActivityCreated: in fragment: {}", this);
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLogger.info("AlertDialogFragment.onCreate: in fragment: {}", this);

        setRetainInstance(true);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(mMessage)
                .setCancelable(false)
                .setPositiveButton(getActivity().getString(R.string.dialog_ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog, final int id) {
                            }
                        });
        AlertDialog alert = builder.create();
        return alert;
    }

    // Workaround for Issue 17423: DialogFragment dismissed on orientation change when setRetainInstance(true) is set
    // (compatibility library)
    // see http://code.google.com/p/android/issues/detail?id=17423
    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance())
            getDialog().setDismissMessage(null);
        super.onDestroyView();
    }

    private void setMessage(String message) {
        mMessage = message;
    }

}
