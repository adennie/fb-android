package com.fizzbuzz.android.socialize;

import static com.google.common.base.Preconditions.checkNotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Activity;
import android.os.Bundle;

import com.fizzbuzz.android.fragment.AbstractFragmentLifecycleListener;
import com.socialize.Socialize;
import com.socialize.UserUtils;
import com.socialize.entity.Entity;
import com.socialize.entity.Like;
import com.socialize.entity.Share;
import com.socialize.entity.User;
import com.socialize.error.SocializeException;
import com.socialize.ui.actionbar.ActionBarListener;
import com.socialize.ui.actionbar.ActionBarView;
import com.socialize.ui.actionbar.OnActionBarEventListener;
import com.socialize.ui.comment.LinkifyCommentViewActionListener;

/*
 * Activities using this class should instantiate it in their onCreate method, and call onDestroy, onPause, and onResume from the corresponding
 * Activity methods
 */

public class SocializeFragmentLifecycleListener
        extends AbstractFragmentLifecycleListener {
    private final Logger mLogger = LoggerFactory.getLogger(LoggingManager.TAG);

    private final Activity mActivity;
    private final ActionBarView mActionBarView;

    public SocializeFragmentLifecycleListener(Activity activity,
            ActionBarView actionBarView) {
        mActivity = checkNotNull(activity, "activity");
        mActionBarView = checkNotNull(actionBarView, "actionBarView");

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Socialize.onCreate(mActivity, savedInstanceState);

        mActionBarView.setActionBarListener(new ActionBarListener() {
            @Override
            public void onCreate(ActionBarView actionBar) {
                // Add clickable links to comments
                actionBar.setOnCommentViewActionListener(new LinkifyCommentViewActionListener());
            }
        });

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

                boolean consumed = false;
                // if the user clicks on the ticker, display the user profile
                if (evt == ActionBarEvent.VIEW) {
                    try {
                        User user = UserUtils.getCurrentUser(mActivity);
                        UserUtils.showUserProfile(mActivity, user);
                    }
                    catch (SocializeException e) {
                        mLogger.error("SocializeFragmentLifecycleListener$OnActionBarEventListener.onClick: squelching exception.", e);
                    }
                    finally {
                        consumed = true;
                    }
                }
                return consumed;
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        Socialize.onResume(mActivity);
    }

    @Override
    public void onPause() {
        Socialize.onPause(mActivity);
        super.onPause();
    }

    @Override
    public void onDetach() {
        Socialize.onDestroy(mActivity);
        super.onDetach();
    }

}
