package com.fizzbuzz.android.socialize;

import android.app.Activity;
import android.content.Context;

import com.socialize.entity.Entity;
import com.socialize.ui.SocializeEntityLoader;

public class BaseSocializeEntityLoader
        implements SocializeEntityLoader {

    public BaseSocializeEntityLoader() {
        super();
    }

    @Override
    public void loadEntity(Activity activity,
            Entity entity) {

    }

    @Override
    public boolean canLoad(Context context,
            Entity entity) {
        return false;
    }

}
