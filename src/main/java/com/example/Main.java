package com.example;

import com.example.github.GithubContentsClient;
import com.example.github.HtmlContentBody;

public class Main {

    public static void main(String[] args) throws Exception {
        GithubContentsClient client = new GithubContentsClient.Builder("mayuravaani", "siddhi-io-hl7")
                .isReadme(true)
                .build();

        String firstParagraph = client.get(HtmlContentBody.class).getFirstParagraph();
    }
}
