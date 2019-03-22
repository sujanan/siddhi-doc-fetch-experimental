package com.example.githubclient;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import javax.net.ssl.HttpsURLConnection;

public abstract class ContentResponse<T> {

    final InputStream stream;
    private final int status;
    DocExtractor docExtractor;

    ContentResponse(HttpsURLConnection connection) throws IOException {
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

    abstract String mediaType();

    public abstract T getContent() throws IOException;

    public JSONObject getError() throws IOException {
        if (status == 200) {
            throw new IllegalStateException("Response is a non error stream");
        }
        if (stream == null) {
            return new JSONObject();
        }
        return new JSONObject(IOUtils.toString(stream, "UTF-8"));
    }

    public int getStatus() {
        return status;
    }

    public String getFirstParagraph() throws IOException {
        if (docExtractor == null) {
            throw new IllegalStateException(this.getClass().getName() + " does not implement the DocExtractor");
        }
        return docExtractor.getFirstParagraph();
    }
}
