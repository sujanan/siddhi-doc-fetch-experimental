package com.example.github;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;

public class HtmlContentBody extends ContentBody<Document> {

    HtmlContentBody(HttpsURLConnection connection) throws IOException {
        super(connection);
        super.docExtractionStrategy = new HtmlDocExtractionStrategy(getContent());
    }

    @Override
    String mediaType() {
        return "html";
    }

    @Override
    public Document getContent() throws IOException {
        return Jsoup.parse(super.stream, null, "");
    }
}
