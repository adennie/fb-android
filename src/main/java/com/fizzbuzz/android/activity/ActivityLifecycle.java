package com.fizzbuzz.android.activity;

import android.os.Bundle;

// This interface exposes Activity methods
public interface ActivityLifecycle {

    public void onCreate(Bundle savedInstanceState);

    public void onRestart();

    public void onStart();

    public void onRestoreInstanceState(Bundle savedInstanceState);

    public void onResume();

    public void onPause();

    public void onSaveInstanceState(Bundle outState);

    public void onStop();

    public void onDestroy();

}
