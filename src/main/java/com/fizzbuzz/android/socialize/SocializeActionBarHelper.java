package com.fizzbuzz.android.socialize;

import static com.google.common.base.Preconditions.checkNotNull;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;

import com.fizzbuzz.android.util.LoggingManager;
import com.socialize.Socialize;
import com.socialize.entity.Entity;
import com.socialize.error.SocializeException;
import com.socialize.ui.actionbar.ActionBarOptions;
import com.socialize.ui.actionbar.ActionBarView;

/* Activities using this class MUST override onCreate, onDestroy, onPause, and onResume, and call the corresponding methods on this class */

public class SocializeActionBarHelper {
    private final Logger mLogger = LoggerFactory.getLogger(LoggingManager.TAG);
    private final ActionBarView mActionBarView;

    public static boolean isSocializeSupported(final Context context) {
        boolean result = true;
        try {
            Socialize.getSocialize().isSocializeSupported(context);
        }
        catch (SocializeException e) {
            result = false;
        }
        return result;
    }

    public SocializeActionBarHelper(final ActionBarView actionBarView) {
        mActionBarView = actionBarView;
    }

    public void setEntity(String entityKey,
            String entityName) {
        setEntity(entityKey, entityName, null, null, null);
    }

    public void setEntity(String entityKey,
            String entityName,
            String entityDescription,
            String entityThumbnail) {
        setEntity(entityKey, entityName, entityDescription, entityThumbnail, null);
    }

    public void setEntity(String entityKey,
            String entityName,
            String entityDescription,
            String entityThumbnail,
            String openGraphObjectType) {

        checkNotNull(mActionBarView, "mActionBarView");

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

        mActionBarView.setEntity(newEntity);

        mActionBarView.refresh();
    }

    /*
     * set colors for ActionBar buttons. Any param that is null will be ignored and the default will be used instead.
     */
    public void setColors(Integer strokeColor, // the vertical separator line between buttons
            Integer accentColor, // the accent line below the buttons
            Integer fillColor, // the main background color of the buttons
            Integer backgroundColor, // the background of the leftmost icon
            Integer highlightColor, // the thin highlight line above the buttons
            Integer textColor) { // the text color for the button labels

        checkNotNull(mActionBarView, "mActionBarView");

        ActionBarOptions options = mActionBarView.getActionBarOptions();

        if (options == null)
            options = new ActionBarOptions();

        options.setStrokeColor(strokeColor);
        options.setAccentColor(accentColor);
        options.setFillColor(fillColor);
        options.setBackgroundColor(backgroundColor);
        options.setHighlightColor(highlightColor);
        options.setTextColor(textColor);

        mActionBarView.setActionBarOptions(options);

    }
}
