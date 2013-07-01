package com.fizzbuzz.android.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import com.fizzbuzz.android.dagger.InjectingDialogFragment;
import com.fizzbuzz.android.persist.SharedPrefHelper;
import com.fizzbuzz.android.util.R;
import dagger.Module;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Calendar;
import java.util.List;

/*
 * A general-purpose AlertDialog-based DialogFragment that shows a message and an OK button
 */
public class AlertDialogFragment
        extends InjectingDialogFragment {

    @Inject SharedPrefHelper mSharedPrefHelper;

    private final Logger mLogger = LoggerFactory.getLogger(LoggingManager.TAG);
    private String mMessage;
    private OnDismissListener mOnDismissListener;


    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLogger.info("AlertDialogFragment.onCreate: in fragment: {}", this);

        setRetainInstance(true);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mLogger.info("AlertDialogFragment.onCreateDialog: in fragment: {}", this);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(mMessage)
                .setCancelable(false)
                .setPositiveButton(getActivity().getString(R.string.dialog_ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog,
                                    final int id) {
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

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mOnDismissListener != null)
            mOnDismissListener.onDismiss(dialog);
    }

    public void setOnDismissListener(final OnDismissListener onDismissListener) {
        mOnDismissListener = onDismissListener;
    }

    @Override
    protected List<Object> getModules() {
        List<Object> modules = super.getModules();
        modules.add(new AlertDialogFragmentModule());
        return modules;
    }

    private void setMessage(String message) {
        mMessage = message;
    }

    @Module(complete=false,
            injects = AlertDialogFragment.class)
    public static class AlertDialogFragmentModule {
    }

    public static class AlertDialogFragmentHelper {

        @Inject SharedPrefHelper mSharedPrefHelper;

        // for use with a message coming from a String resource
        public AlertDialogFragment newInstance(final FragmentActivity activity,
                                                      final int messageResourceId) {
            return newInstance(activity.getString(messageResourceId));
        }

        // same, but with an OnDismissListener provided
        public AlertDialogFragment newInstance(final FragmentActivity activity,
                                                      final int messageResourceId,
                                                      final OnDismissListener onDismissListener) {
            return newInstance(activity.getString(messageResourceId), onDismissListener);
        }

        // for use with a plain message String
        public AlertDialogFragment newInstance(String message) {
            return newInstance(message, null);
        }

        // same, but with an OnDismissListener provided
        public  AlertDialogFragment newInstance(String message,
                                                      OnDismissListener onDismissListener) {
            AlertDialogFragment frag = new AlertDialogFragment();
            frag.setMessage(message);
            frag.setOnDismissListener(onDismissListener);
            return frag;
        }

        /*
         * Show an AlertDialogFragment only if it has never been shown before. The supplied "tag" argument must be unique to
         * the message being shown; it is used as a both a shared preference key and as the fragment tag provided to the
         * FragmentManager.
         */
        public void showOnce(FragmentActivity activity,
                                    String tag,
                                    int messageResourceId) {
            if (!mSharedPrefHelper.getBooleanNoStrict(activity, tag, false)) {

                AlertDialogFragment alert = newInstance(activity, messageResourceId);
                alert.show(activity.getSupportFragmentManager(), tag);

                mSharedPrefHelper.setBooleanNoStrict(activity, tag, true);// remember not to show this again
            }
        }

        /*
         * Show an AlertDialogFragment only if a specified amount time has passed since the last time it was shown. The supplied "tag" argument must be
         * unique to the message being shown; it is used as a both a shared preference key and as the fragment tag provided to the
         * FragmentManager.
         */
        public void showOncePer(FragmentActivity activity,
                                       String tag,
                                       int messageResourceId,
                                       long millis) {
            long lastShown = mSharedPrefHelper.getLongNoStrict(activity, tag, 0);
            long now = Calendar.getInstance().getTimeInMillis();
            if (lastShown == 0 || now - lastShown > millis) {

                AlertDialogFragment alert = newInstance(activity, messageResourceId);
                alert.show(activity.getSupportFragmentManager(), tag);

                mSharedPrefHelper.setLongNoStrict(activity, tag, now); // remember when we showed this
            }
        }
    }
}
