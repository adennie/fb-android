package com.fizzbuzz.android.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class SimpleWebView
        extends WebView {
    private final Logger mLogger = LoggerFactory.getLogger(LoggingManager.TAG);
    private boolean mShowProgress = false;
    private boolean mAllowUrlLoading = true;
    private int mUrlLoadingDisabledToastMessageId;
    private Activity mActivity;

    // Construct a new SimpleWebView with a Context object.
    public SimpleWebView(final Context context) {
        super(context.getApplicationContext());
    }

    // Construct a new SimpleWebView with layout parameters.
    public SimpleWebView(final Context context,
            final AttributeSet attrs) {
        super(context.getApplicationContext(), attrs);
    }

    // Construct a new SimpleWebView with layout parameters and a default style.
    public SimpleWebView(final Context context,
            final AttributeSet attrs,
            final int defStyle) {
        super(context.getApplicationContext(), attrs, defStyle);
    }

    public void setAllowUrlLoading(boolean allow,
            int toastMessageId) {
        mAllowUrlLoading = allow;
        mUrlLoadingDisabledToastMessageId = toastMessageId;
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
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

    @Override
    public void destroy() {
        try {
            super.destroy();
        }
        catch (RuntimeException e) {
            mLogger.error("SimpleWebView.destroy: caught Exception (sometimes happens in WebView).  Squelching to avoid problems.", e);
        }
    }
}