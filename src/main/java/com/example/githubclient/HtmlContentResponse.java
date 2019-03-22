package com.example.githubclient;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;

public class HtmlContentResponse extends ContentResponse<Document> {

    HtmlContentResponse(HttpsURLConnection connection) throws IOException {
        super(connection);
        super.docExtractor = new DocExtractor<Document, HtmlContentResponse>(this) {

            @Override
            String getFirstParagraph(Document content) {
                Elements pTags = content.getElementsByTag("p");
                if (pTags != null) {
                    Element firstPTag = pTags.first();
                    if (firstPTag != null) {
                        return firstPTag.text();
                    }
                }
                return null;
            }
        };
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
