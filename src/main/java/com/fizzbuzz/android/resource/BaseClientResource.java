package com.fizzbuzz.android.resource;

import com.fizzbuzz.exception.HttpConnectionException;
import com.fizzbuzz.exception.NotFoundException;
import com.fizzbuzz.exception.UpgradeRequiredException;
import org.restlet.data.ClientInfo;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

public class BaseClientResource extends ClientResource {
    private final String mResourceUrl;
    private final MediaType mMediaType;

    public BaseClientResource(String resourceUrl, MediaType mediaType) {
        super(resourceUrl);
        mResourceUrl = resourceUrl;
        mMediaType = mediaType;

        // when doing a GET, the media type goes into the Accept header. When doing a PUT/POST, it goes into the
        // Content-Type header.
        if (mediaType != null)
            setClientInfo(new ClientInfo(mediaType));

        // Workaround for GAE servers to prevent chunk encoding for PUT and POST requests. Note: I have not yet
        // found this to be necessary. Maybe it's only a problem under certain conditions?
        setRequestEntityBuffering(true);
    }
    public String getResourceUrl() {
        return mResourceUrl;
    }

    public MediaType getMediaType() {
        return mMediaType;
    }

    @Override
    public void doError(Status status) {
        if (status.isConnectorError()) {
            throw new HttpConnectionException(status.toString());
        }

        // if we get a 406 status back in the response, it means the server no longer supports the requested media type
        if (status.equals(Status.CLIENT_ERROR_NOT_ACCEPTABLE))
            throw new UpgradeRequiredException("requested media type no longer supported by server");

        if (status.equals(Status.CLIENT_ERROR_NOT_FOUND))
            throw new NotFoundException("requested resource not found: "
                    + getRequest().getResourceRef());

        throw new ResourceException(status);
    }
}
