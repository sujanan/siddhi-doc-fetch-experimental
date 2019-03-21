package com.example.githubclient;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URL;

public class GithubContentsClient {
    private static final String DOMAIN = "api.github.com";

    private final HttpsURLConnection connection;

    public static class Builder {
        private final String owner;
        private final String repos;
        private final StringBuilder queryParamsBuilder;

        private boolean isReadme = false;
        private String path = "/";

        public Builder(String owner, String repos) {
            this.owner = owner;
            this.repos = repos;
            queryParamsBuilder = new StringBuilder();
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

        public Builder queryParam(String key, String val) {
            if (queryParamsBuilder.length() != 0) {
                queryParamsBuilder.append("&");
            }
            queryParamsBuilder.append(key).append("=").append(val);
            return this;
        }

        public GithubContentsClient build() throws IOException {
            return new GithubContentsClient(this);
        }
    }

    private GithubContentsClient(Builder builder) throws IOException {
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
        String queryParams = builder.queryParamsBuilder.toString();
        if (!queryParams.isEmpty()) {
            urlBuilder.append("?").append(queryParams);
        }
        URL url = new URL(urlBuilder.toString());
        connection = (HttpsURLConnection) url.openConnection();
    }

    public void setHeader(String key, String val) {
        connection.setRequestProperty(key, val);
    }

    public <T extends ContentResponse> T getContentResponse(Class<T> tClass) throws Exception {
        Constructor<T> constructor = tClass.getDeclaredConstructor(HttpsURLConnection.class);
        constructor.setAccessible(true);
        return constructor.newInstance(connection);
    }
}
