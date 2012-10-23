package com.fizzbuzz.android.util;

import java.util.ArrayList;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class PreferenceActivityLifecycleHub
        extends PreferenceActivity
{
    private final ArrayList<ActivityLifecycleListener> mListeners = new ArrayList<ActivityLifecycleListener>();

    public PreferenceActivityLifecycleHub() {
    }

    public void addLifecycleListener(ActivityLifecycleListener listener) {
        mListeners.add(listener);
    }

    @Override
    protected void onDestroy() {
        for (ActivityLifecycleListener listener : mListeners) {
            listener.onDestroy();
        }
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        for (ActivityLifecycleListener listener : mListeners) {
            listener.onSaveInstanceState(outState);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        for (ActivityLifecycleListener listener : mListeners) {
            listener.onRestoreInstanceState(savedInstanceState);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        for (ActivityLifecycleListener listener : mListeners) {
            listener.onStart();
        }
    }

    @Override
    protected void onStop() {
        for (ActivityLifecycleListener listener : mListeners) {
            listener.onStop();
        }
        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        for (ActivityLifecycleListener listener : mListeners) {
            listener.onRestart();
        }
    }

    @Override
    protected void onPause() {
        for (ActivityLifecycleListener listener : mListeners) {
            listener.onPause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        for (ActivityLifecycleListener listener : mListeners) {
            listener.onResume();
        }
    }
}
