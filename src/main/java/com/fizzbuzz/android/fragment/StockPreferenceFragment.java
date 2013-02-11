package com.fizzbuzz.android.fragment;

/***
 * Copyright (c) 2008-2012 CommonsWare, LLC
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
 * by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 * 
 * From _The Busy Coder's Guide to Android Development_
 * http://commonsware.com/Android
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceFragment;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class StockPreferenceFragment
        extends PreferenceFragment {
    private final Logger mLogger = LoggerFactory.getLogger(LoggingManager.TAG);

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLogger.info("StockPreferenceFragment.onCreate: in fragment: {}", this);

        String fullyQualifiedResourceName = getArguments().getString("resource");
        int res = getActivity().getResources().getIdentifier(fullyQualifiedResourceName, null, null);
        addPreferencesFromResource(res);
    }
}
