package com.example.github;

import javax.net.ssl.HttpsURLConnection;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;

public class GithubContentsClient {
    private static final String DOMAIN = "api.github.com";

    private final URL url;

    public static class Builder {
        private final String owner;
        private final String repos;

        private boolean isReadme = false;
        private String path = "/";

        public Builder(String owner, String repos) {
            this.owner = owner;
            this.repos = repos;
        }

        public Builder isReadme(boolean isReadme) {
            this.isReadme = isReadme;
            return this;
        }

        public Builder path(String path) {
            if (!path.isEmpty() && path.charAt(0) != '/') {
                path = '/' + path;
            }
            this.path = path;
            return this;
        }

        public GithubContentsClient build() throws MalformedURLException {
            return new GithubContentsClient(this);
        }
    }

    private GithubContentsClient(Builder builder) throws MalformedURLException {
        StringBuilder urlBuilder = new StringBuilder()
                .append("https://")
                .append(DOMAIN)
                .append("/repos")
                .append("/").append(builder.owner)
                .append("/").append(builder.repos);
        if (builder.isReadme) {
            urlBuilder.append("/readme");
        } else {
            urlBuilder.append("/contents").append(builder.path);
        }
        url = new URL(urlBuilder.toString());
    }

    public <T extends ContentBody> T get(Class<T> tClass) throws Exception {
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        Constructor<T> constructor = tClass.getDeclaredConstructor(HttpsURLConnection.class);
        constructor.setAccessible(true);
        return constructor.newInstance(connection);
    }
}
