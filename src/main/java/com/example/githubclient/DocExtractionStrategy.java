package com.example.githubclient;

public interface DocExtractionStrategy<T> {

    String getFirstParagraph(T content);
}
