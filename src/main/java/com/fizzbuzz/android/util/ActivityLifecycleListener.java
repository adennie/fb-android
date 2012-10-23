package com.fizzbuzz.android.util;

import android.os.Bundle;

public interface ActivityLifecycleListener {

    public void onCreate(final Bundle savedInstanceState);

    public void onDestroy();

    public void onSaveInstanceState(Bundle outState);

    public void onRestoreInstanceState(Bundle savedInstanceState);

    public void onStart();

    public void onStop();

    public void onRestart();

    public void onPause();

    public void onResume();

}
