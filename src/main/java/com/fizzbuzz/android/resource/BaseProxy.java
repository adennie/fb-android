package com.fizzbuzz.android.resource;

import android.content.Context;
import com.fizzbuzz.exception.NotModifiedException;
import com.fizzbuzz.resource.UriHelper;
import com.google.common.collect.ImmutableMap;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

public class BaseProxy<T> {
    public static ImmutableMap<String, String> mEmptyMap = ImmutableMap.of();
    protected final Logger mLogger = LoggerFactory.getLogger(LoggingManager.TAG);
    protected final Context mAppContext;
    private final String mServerBaseUrl;
    private final Class<T> mTargetResourceClass;
    private final UriHelper mUriHelper;

    /*
    protected static ThreadPolicyWrapper prepThreadPolicyForRestletCall(InjectingApplication app) {
        StrictModeWrapper strictMode = VersionedStrictModeWrapper.getInstance();
        ThreadPolicyWrapper origPolicy = strictMode.allowThreadDiskReads();
        strictMode.allowThreadDiskWrites();
        strictMode.allowThreadNetwork();
        return origPolicy;

    }

    protected static void resetThreadPolicyAfterRestletCall(final ThreadPolicyWrapper origPolicy) {
        StrictModeWrapper strictMode = VersionedStrictModeWrapper.getInstance();
        strictMode.restoreThreadPolicy(origPolicy);
    }
    */
    public BaseProxy(final Context appContext,
                     final String serverBaseUrl,
                     final Class<T> targetResourceClass,
                     final UriHelper uriHelper) {
        mServerBaseUrl = serverBaseUrl;
        mTargetResourceClass = targetResourceClass;
        mUriHelper = uriHelper;
        mAppContext = appContext;
    }

    protected Context getAppContext() {
        return mAppContext;
    }

    protected void processResponse(ClientResource clientResource) {
        Status status = clientResource.getResponse().getStatus();

        // Note: error statuses (e.g. 4xx, 5xx, 10xx) are translated into thrown exceptions by BaseClientResource
        // .doError()

        // if we get a 304 status back in the response, it means there are no changes since the last fetch
        if (status.equals(Status.REDIRECTION_NOT_MODIFIED))
            throw new NotModifiedException("requested resource not modified since last fetch");

        // http://restlet-discuss.1400322.n2.nabble.com/response-release-versus-response-exhaust-in-2-0-td5427308.html
        Representation rep = clientResource.getResponseEntity();
        mLogger.info("BaseProxy.processResponse: back from Restlet call on {}, status={}", clientResource.getRequest()
                .getResourceRef(), status);
        if (rep != null) {
            try {
                rep.exhaust();
            } catch (IOException e) {
                mLogger.error("BaseProxy.processResponse: squelching IOException from rep.exhaust()", e);
            }
            rep.release();
        }
    }

    // Map of token strings (see xxxUriHelper.URL_ELEM_TOKEN_xxx strings) to token substitution values.
    // Subclasses representing specific server resources must provide an implementation of this method so that the token
    // strings in the resource's URI template can be replaced (by getResourceUrl()) with values prior to making the
    // request
    protected ImmutableMap<String, String> getUriTokenValues() {
        return ImmutableMap.<String, String>of();
    }

    protected String getResourceUrl(final Map<String, String> uriTokenValues) {
        String baseUrl = getBaseUrl();
        String resourcePath = mUriHelper.getUriForResourceInterface(mTargetResourceClass, uriTokenValues);
        return baseUrl + resourcePath;
    }

    protected T makeProxy(ClientResource clientResource) {
        return clientResource.wrap(mTargetResourceClass);
    }

    private String getBaseUrl() {
        return mServerBaseUrl;
    }
}
