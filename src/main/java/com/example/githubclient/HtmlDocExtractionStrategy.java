package com.example.githubclient;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HtmlDocExtractionStrategy extends DocExtractionStrategy<Document> {

    HtmlDocExtractionStrategy(Document content) {
        super(content);
    }

    @Override
    public String getFirstParagraph() {
        Elements pTags = super.content.getElementsByTag("p");
        if (pTags != null) {
            Element firstPTag = pTags.first();
            if (firstPTag != null) {
                return firstPTag.text();
            }
        }
        return null;
    }
}
