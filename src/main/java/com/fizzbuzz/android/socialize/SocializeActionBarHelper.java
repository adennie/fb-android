package com.fizzbuzz.android.socialize;

import static com.google.common.base.Preconditions.checkNotNull;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.fizzbuzz.android.fragment.FragmentEvents.ActivityCreatedEvent;
import com.fizzbuzz.android.fragment.FragmentEvents.FragmentPausedEvent;
import com.fizzbuzz.android.fragment.FragmentEvents.FragmentResumedEvent;
import com.fizzbuzz.android.fragment.FragmentEvents.FragmentViewDestroyedEvent;
import com.fizzbuzz.android.util.NetworkHelper;
import com.fizzbuzz.android.util.VersionedStrictModeWrapper;
import com.socialize.Socialize;
import com.socialize.UserUtils;
import com.socialize.entity.Entity;
import com.socialize.entity.Like;
import com.socialize.entity.Share;
import com.socialize.entity.User;
import com.socialize.error.SocializeException;
import com.socialize.ui.actionbar.ActionBarListener;
import com.socialize.ui.actionbar.ActionBarOptions;
import com.socialize.ui.actionbar.ActionBarView;
import com.socialize.ui.actionbar.OnActionBarEventListener;
import com.socialize.ui.comment.LinkifyCommentViewActionListener;
import com.squareup.otto.OttoBus;
import com.squareup.otto.Subscribe;

/*
 * A helper class to facilitate working with a Socialize ActionBar
 */
public class SocializeActionBarHelper {
    private final Logger mLogger = LoggerFactory.getLogger(LoggingManager.TAG);
    private final ActionBarView mActionBarView;

    public static boolean isSocializeSupported(final Context context) {
        boolean result = true;
        try {
            Socialize.getSocialize().isSocializeSupported(context);
        } catch (SocializeException e) {
            result = false;
        }
        return result;
    }

    public static ActionBarView createActionBar(final Context context,
            final ViewGroup actionBarFrame,
            final LayoutInflater inflater,
            final int layoutResId,
            final OttoBus bus) {
        ActionBarView result = null;
        if (NetworkHelper.isConnected(context) && SocializeActionBarHelper.isSocializeSupported(context)) {
            ActionBarView abView = (ActionBarView) VersionedStrictModeWrapper.inflateWithStrictModeOverride(
                    inflater,
                    layoutResId, null, false);
            actionBarFrame.addView(abView);

            // register an event handler to coordinate Fragment lifecycle events with the Socialize ActionBar.
            SocializeActionBarHelper.FragmentEventHandler.registerWithBus(bus, abView);
            result = abView;
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
            } catch (JSONException e) {
                mLogger.error(
                        "SocializeActionBarHelper.setEntity: caught JSONException while setting metadata. Squelching and continuing on. ",
                        e);
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

    /*
     * An handler that responds to Fragment lifecycle events posted to an event bus
     */
    public static class FragmentEventHandler {
        private final Logger mLogger = LoggerFactory.getLogger(LoggingManager.TAG);
        private final OttoBus mBus;
        private final ActionBarView mActionBarView;

        @SuppressWarnings("unused")
        public static void registerWithBus(final OttoBus bus,
                final ActionBarView actionBarView) {
            // no need to hold a reference; the bus will hold one until the handler auto-deregisters itself later
            new FragmentEventHandler(bus, actionBarView);
        }

        private FragmentEventHandler(final OttoBus bus,
                final ActionBarView actionBarView) {
            mBus = bus;
            mBus.register(this);
            mActionBarView = actionBarView;
        }

        @Subscribe
        public void onActivityCreated(final ActivityCreatedEvent event) {
            mLogger.debug("SocializeActionBarHelper$FragmentEventHandler.onActivityCreated: for Fragment {}",
                    event.getFragment());
            final Activity activity = event.getFragment().getActivity();
            Socialize.onCreate(activity, event.getSavedInstanceState());

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
                            User user = UserUtils.getCurrentUser(activity);
                            UserUtils.showUserProfile(activity, user);
                        }
                        catch (SocializeException e) {
                            mLogger.error(
                                    "SocializeFragmentLifecycleListener$OnActionBarEventListener.onClick: squelching exception.",
                                    e);
                        }
                        finally {
                            consumed = true;
                        }
                    }
                    return consumed;
                }
            });

        }

        @Subscribe
        public void onFragmentResumed(final FragmentResumedEvent event) {
            mLogger.debug("SocializeActionBarHelper$FragmentEventHandler.onFragmentResumed: for Fragment {}",
                    event.getFragment());
            Socialize.onResume(event.getFragment().getActivity());
        }

        @Subscribe
        public void onFragmentPaused(final FragmentPausedEvent event) {
            mLogger.debug("SocializeActionBarHelper$FragmentEventHandler.onFragmentPaused: for Fragment {}",
                    event.getFragment());
            Socialize.onPause(event.getFragment().getActivity());
        }

        @Subscribe
        public void onFragmentViewDestroyed(FragmentViewDestroyedEvent event) {
            checkNotNull(mActionBarView, "mActionBarView");
            mLogger.debug("SocializeActionBarHelper$FragmentEventHandler.onFragmentViewDestroyed: for Fragment {}",
                    event.getFragment());

            Socialize.onDestroy(event.getFragment().getActivity());

            // now that the view is destroyed, we can unregister this handler
            mBus.unregister(this);
        }
    }
}
