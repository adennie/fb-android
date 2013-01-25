package com.fizzbuzz.android.util;

import java.util.Map;
import java.util.Set;

import android.content.Intent;
import android.os.Bundle;

import com.google.common.collect.Maps;

public class Intents {
    public static Map<String, String> extractStringExtrasToDictionary(final Intent intent) {
        Map<String, String> result = Maps.newHashMap();
        Bundle bundle = intent.getExtras();

        Set<String> keySet = intent.getExtras().keySet();
        for (String key : keySet) {
            String value = bundle.getString(key);
            if (value != null)
                result.put(key, value);
        }

        return result;
    }
}
