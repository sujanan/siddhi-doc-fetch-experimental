package com.example.githubclient;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStream;

public abstract class ContentResponse<T> {

    final InputStream stream;
    private final int status;
    private DocExtractionStrategy docExtractionStrategy = null;

    private ContentResponse(HttpsURLConnection connection) throws IOException {
        connection.setRequestProperty(
                "Accept",
                "application/vnd.githubclient.v3." + mediaType());
        status = connection.getResponseCode();
        if (status != 200) {
            stream = connection.getErrorStream();
        } else {
            stream = connection.getInputStream();
        }
    }

    ContentResponse(HttpsURLConnection connection, DocExtractionStrategy docExtractionStrategy) throws IOException {
        this(connection);
        if (status == 200) {
            this.docExtractionStrategy = docExtractionStrategy;
        }
    }

    abstract String mediaType();

    public abstract T getContent() throws IOException;

    public JSONObject getError() throws IOException {
        if (status == 200) {
            throw new IllegalStateException("Response is a non error stream");
        }
        if (stream == null) {
            /* According to RFC-4627 valid minimum JSON is either '{}' or '[]' */
            return new JSONObject("{}");
        }
        return new JSONObject(IOUtils.toString(stream, "UTF-8"));
    }

    public int getStatus() {
        return status;
    }

    public String getFirstParagraph() throws IOException {
        if (docExtractionStrategy != null) {
            return docExtractionStrategy.getFirstParagraph(getContent());
        }
        return null;
    }
}
