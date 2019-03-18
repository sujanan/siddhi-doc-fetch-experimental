package com.example.github;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStream;

public abstract class ContentBody<T> {

    final InputStream stream;
    DocExtractionStrategy docExtractionStrategy;

    ContentBody(HttpsURLConnection connection) throws IOException {
        connection.setRequestProperty(
                "Accept",
                "application/vnd.github.v3." + mediaType());
        stream = connection.getInputStream();
    }

    abstract String mediaType();

    public abstract T getContent() throws IOException;

    public String getFirstParagraph() {
        return docExtractionStrategy.getFirstParagraph();
    }
}
