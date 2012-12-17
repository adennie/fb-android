package com.fizzbuzz.android.ui;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import android.widget.ZoomButtonsController;

import com.fizzbuzz.android.util.LoggingManager;

public class SimpleWebView
        extends WebView {
    private final Logger mLogger = LoggerFactory.getLogger(LoggingManager.TAG);
    private boolean mShowProgress = false;
    private boolean mAllowUrlLoading = true;
    private boolean mEnableZoomControls = false;
    private int mUrlLoadingDisabledToastMessageId;
    private Activity mActivity;
    private ZoomButtonsController mZoomController = null;

    // Construct a new SimpleWebView with a Context object.
    public SimpleWebView(final Context context) {
        super(context.getApplicationContext());
        mZoomController = getZoomController();
    }

    // Construct a new SimpleWebView with layout parameters.
    public SimpleWebView(final Context context,
            final AttributeSet attrs) {
        super(context.getApplicationContext(), attrs);
        mZoomController = getZoomController();
    }

    // Construct a new SimpleWebView with layout parameters and a default style.
    public SimpleWebView(final Context context,
            final AttributeSet attrs,
            final int defStyle) {
        super(context.getApplicationContext(), attrs, defStyle);
        mZoomController = getZoomController();
    }

    public void setAllowUrlLoading(boolean allow,
            int toastMessageId) {
        mAllowUrlLoading = allow;
        mUrlLoadingDisabledToastMessageId = toastMessageId;
    }

    public void setEnableZoomControls(final boolean enable) {
        mEnableZoomControls = enable;
    }

    public void attachActivity(final Activity activity) {
        mActivity = activity;
    }

    public void detachActivity() {
        mActivity = null;
    }

    public void showProgress(final boolean showProgress) {
        mShowProgress = showProgress;
    }

    @SuppressLint({ "SetJavaScriptEnabled", "NewApi" })
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        getSettings().setJavaScriptEnabled(true);
        getSettings().setBuiltInZoomControls(true);
        getSettings().setUseWideViewPort(true); // needed for double-tap

        // if we're not supposed to show zoom controls, use setDisplayZoomControls to disable them on API 11+. On older versions, we override the
        // default behavior in onTouchEvent().
        if (!mEnableZoomControls && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            getSettings().setDisplayZoomControls(false);

        setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

        // intercept any redirects or user navigation. If allowed, handle them with this WebView
        setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(final WebView view,
                    final String url) {
                if (mAllowUrlLoading) {
                    view.loadUrl(url);
                }
                else if (mUrlLoadingDisabledToastMessageId != 0) {
                    Toast.makeText(getContext(), getContext().getString(mUrlLoadingDisabledToastMessageId),
                            Toast.LENGTH_SHORT).show();
                }

                return true;
            }

            @Override
            public void onReceivedError(final WebView view,
                    final int errorCode,
                    final String description,
                    final String failingUrl) {
                mLogger.warn("SimpleWebView.onReceivedError: errorCode={}, " + description + ": " + failingUrl, errorCode);
            }
        });

        setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(final WebView view,
                    final int progress) {
                // Activities and WebViews measure progress with different scales.
                // The progress meter will automatically disappear when we reach 100%
                if (mActivity != null && mShowProgress) {
                    mActivity.setProgressBarVisibility(true); // may already be true, but we don't know
                    mActivity.setProgress(progress * 100);
                }
            }
        });
    }

    // technique for disabling zoom controls on API 10 and earlier.
    // http://stackoverflow.com/questions/5125851/enable-disable-zoom-in-android-webview
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        super.onTouchEvent(ev);
        if (!mEnableZoomControls && mZoomController != null) {
            // Hide the controls AFTER they where made visible by the default implementation.
            mZoomController.setVisible(false);
        }
        return true;
    }

    @Override
    public void destroy() {
        try {
            super.destroy();
        }
        catch (RuntimeException e) {
            mLogger.error("SimpleWebView.destroy: caught Exception (sometimes happens in WebView).  Squelching to avoid problems.", e);
        }
    }

    // For API 10 and earlier, use reflection to get the zoom controller. We'll use this in onTouchEvent to disable the zoom controls.
    // http://stackoverflow.com/questions/5125851/enable-disable-zoom-in-android-webview
    private ZoomButtonsController getZoomController() {
        ZoomButtonsController result = null;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
        {
            try {
                @SuppressWarnings("unchecked")
                Class<WebView> webview = (Class<WebView>) Class.forName("android.webkit.WebView");
                Method method = webview.getMethod("getZoomButtonsController");
                result = (ZoomButtonsController) method.invoke(this, (Object[]) null);
            }
            catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            catch (SecurityException e) {
                e.printStackTrace();
            }
            catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
            catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

}