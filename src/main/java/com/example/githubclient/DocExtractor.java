package com.example.githubclient;

import java.io.IOException;

public abstract class DocExtractor<E, T extends ContentResponse<E>> {

    private final T response;

    public DocExtractor(T response) {
        this.response = response;
    }

    public String getFirstParagraph() throws IOException {
        if (response.getStatus() != 200) {
            return null;
        }
        return getFirstParagraph(response.getContent());
    }

    abstract String getFirstParagraph(E content);
}
