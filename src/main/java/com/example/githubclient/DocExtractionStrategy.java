package com.example.githubclient;

public abstract class DocExtractionStrategy<T> {

    final T content;

    DocExtractionStrategy(T content) {
        this.content = content;
    }

    public abstract String getFirstParagraph();
}
