package com.fizzbuzz.android.util;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.socialize.ActionBarUtils;
import com.socialize.Socialize;
import com.socialize.entity.Entity;
import com.socialize.ui.actionbar.ActionBarOptions;
import com.socialize.ui.actionbar.ActionBarView;

/* Activities using this class MUST override onDestroy, onPause, and onResume, and call the corresponding methods on this class */

public class SocializeActionBarHelper
        extends ActivityLifecycleListenerImpl {
    private final Logger mLogger = LoggerFactory.getLogger(LoggingManager.TAG);

    public SocializeActionBarHelper(Activity activity,
            Bundle savedInstanceState) {
        super(activity);

        Socialize.onCreate(activity, savedInstanceState);

    }

    public View createActionBarWrappedView(int resourceId,
            String entityKey,
            String entityName,
            boolean wrapLayoutWithScrollView) {
        Entity entity = Entity.newInstance(entityKey, entityName);

        ActionBarOptions options = new ActionBarOptions();
        if (wrapLayoutWithScrollView)
            options.setAddScrollView(false); // Disable scroll view

        // Wrap the view identified by resourceId with the action bar.
        View result = ActionBarUtils.showActionBar(getActivity(), resourceId, entity, options);
        return result;
    }

    public void setEntity(int resourceId,
            String entityKey,
            String entityName) {
        setEntity(resourceId, entityKey, entityName, null, null, null);
    }

    public void setEntity(int resourceId,
            String entityKey,
            String entityName,
            String entityDescription,
            String entityThumbnail) {
        setEntity(resourceId, entityKey, entityName, entityDescription, entityThumbnail, null);
    }

    public void setEntity(int resourceId,
            String entityKey,
            String entityName,
            String entityDescription,
            String entityThumbnail,
            String openGraphObjectType) {
        ActionBarView socializeActionBarView = (ActionBarView) getActivity().findViewById(resourceId);

        Entity prevEntity = socializeActionBarView.getEntity();
        Entity newEntity = Entity.newInstance(entityKey, entityName);

        if (entityDescription != null || entityThumbnail != null) {
            JSONObject metaData = new JSONObject();
            try {
                if (entityDescription != null)
                    metaData.put("szsd_description", entityDescription);
                if (entityThumbnail != null)
                    metaData.put("szsd_thumb", entityThumbnail);
                newEntity.setMetaData(metaData.toString());
            }
            catch (JSONException e) {
                mLogger.error("SocializeActionBarHelper.setEntity: caught JSONException while setting metadata. Squelching and continuing on. ", e);
            }
        }

        if (openGraphObjectType != null)
            newEntity.setType(openGraphObjectType);

        socializeActionBarView.setEntity(newEntity);

        if (prevEntity != null)
            socializeActionBarView.refresh();
    }

    @Override
    public void onDestroy() {
        Socialize.onDestroy(getActivity());
        super.onDestroy();
    }

    @Override
    public void onPause() {
        Socialize.onPause(getActivity());
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        Socialize.onResume(getActivity());
    }

}
