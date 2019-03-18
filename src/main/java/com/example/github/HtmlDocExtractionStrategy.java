package com.example.github;

import org.jsoup.nodes.Document;

public class HtmlDocExtractionStrategy extends DocExtractionStrategy<Document> {

    HtmlDocExtractionStrategy(Document content) {
        super(content);
    }

    @Override
    public String getFirstParagraph() {
        return super.content.getElementsByTag("p").first().text();
    }
}
