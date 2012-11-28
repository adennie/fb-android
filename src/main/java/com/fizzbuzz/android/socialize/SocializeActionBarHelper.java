package com.fizzbuzz.android.socialize;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Activity;
import android.view.View;

import com.fizzbuzz.android.util.LoggingManager;
import com.socialize.UserUtils;
import com.socialize.entity.Entity;
import com.socialize.entity.Like;
import com.socialize.entity.Share;
import com.socialize.entity.User;
import com.socialize.error.SocializeException;
import com.socialize.ui.actionbar.ActionBarOptions;
import com.socialize.ui.actionbar.ActionBarView;
import com.socialize.ui.actionbar.OnActionBarEventListener;

/* Activities using this class MUST override onCreate, onDestroy, onPause, and onResume, and call the corresponding methods on this class */

public class SocializeActionBarHelper {
    private final Logger mLogger = LoggerFactory.getLogger(LoggingManager.TAG);
    private final ActionBarView mActionBarView;

    public SocializeActionBarHelper(final Activity activity,
            final View actionBarView) {
        checkArgument(actionBarView instanceof ActionBarView, "actionBarView must be instance of com.socialize.ui.actionbar.ActionBarView");
        mActionBarView = (ActionBarView) actionBarView;

        mActionBarView.setOnActionBarEventListener(new OnActionBarEventListener() {

            @Override
            public void onUpdate(ActionBarView actionBar) {
                // Called when the action bar has its data updated
            }

            @Override
            public void onPostUnlike(ActionBarView actionBar) {
                // Called AFTER a user has removed a like
            }

            @Override
            public void onPostShare(ActionBarView actionBar,
                    Share share) {
                // Called AFTER a user has posted a share
            }

            @Override
            public void onPostLike(ActionBarView actionBar,
                    Like like) {
                // Called AFTER a user has posted a like
            }

            @Override
            public void onLoad(ActionBarView actionBar) {
                // Called when the action bar is loaded
            }

            @Override
            public void onGetLike(ActionBarView actionBar,
                    Like like) {
                // Called when the action bar retrieves the like for the
                // current user
            }

            @Override
            public void onGetEntity(ActionBarView actionBar,
                    Entity entity) {
                // Called when the action bar retrieves the entity data
            }

            @Override
            public boolean onClick(ActionBarView actionBar,
                    ActionBarEvent evt) {
                // Called when the user clicks on the action bar
                // Return true to indicate you do NOT want the action to continue

                // if the user clicks on the ticker, display the user profile
                if (evt == ActionBarEvent.VIEW) {
                    try {
                        User user = UserUtils.getCurrentUser(activity);
                        UserUtils.showUserProfile(activity, user);
                        return true;
                    }
                    catch (SocializeException e) {
                        mLogger.error("SocializeActionBarHelper$OnActionBarEventListener.onClick: squelching exception.", e);
                        return false;
                    }
                }
                else
                    return false;
            }
        });

    }

    public void onDestroyView() {
        mActionBarView.setOnActionBarEventListener(null);
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
