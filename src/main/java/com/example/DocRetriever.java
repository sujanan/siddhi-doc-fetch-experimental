package com.example;

import com.example.githubclient.GithubContentsClient;
import com.example.githubclient.HtmlContentResponse;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.logging.Logger;

public class DocRetriever {
    private static final Logger LOG = Logger.getLogger(DocRetriever.class.getName());

    private static final String CREDENTIALS_PROPERTIES = "credentials.properties";
    private static final String CLIENT_ID = "client_id";
    private static final String CLIENT_SECRET = "client_secret";

    private final String baseRepo;
    private final String[] extensions;

    private DocStore docStore;

    public DocRetriever(String baseRepo, String[] extensions) {
        this.baseRepo = baseRepo;
        this.extensions = extensions;
        docStore = null;
        try {
            docStore = new DocStore();
        } catch (IOException | URISyntaxException e) {
            LOG.warning("DocStore creation failed. Will depend on the remote data only.");
        }
    }

    public void fetch() throws Exception {
        if (docStore == null) {
            return;
        }
        String httpStandardLastModified = DateTimeFormatter.RFC_1123_DATE_TIME
                .withZone(ZoneOffset.UTC)
                .format(docStore.lastModified().toInstant());

        int i = 0;
        caller:
        for (; i < extensions.length; i++) {
            final String extension = extensions[i];

            GithubContentsClient client = new GithubContentsClient.Builder(baseRepo, extension)
                    .isReadme(true)
                    .build();
            if (docStore.has(extension)) {
                client.setHeader("If-Modified-Since", httpStandardLastModified);
            }
            HtmlContentResponse response = client.getContentResponse(HtmlContentResponse.class);
            switch (response.getStatus()) {
                case 304:
                    LOG.info(String.format("[%s] %s", extension, "Content was not modified."));
                    continue;
                case 403:
                    LOG.info("Maximum request quota exceeded. Falling back to Authorized routes.");
                    break caller;
                case 200:
                    String firstParagraph = response.getFirstParagraph();
                    if (firstParagraph == null) {
                        continue;
                    }
                    LOG.info(String.format("[%s] %s", extension, "Content was updated."));
                    docStore.add(extension, firstParagraph);
                    break;
                default:
                    LOG.warning(response.getError().toString(2));
                    break;
            }
        }
        Properties credentials = new Properties();
        if (!loadCredentials(credentials)) {
            return;
        }

        for (; i < extensions.length; i++) {
            final String extension = extensions[i];

            GithubContentsClient client = new GithubContentsClient.Builder(baseRepo, extension)
                    .isReadme(true)
                    .queryParam(CLIENT_ID, credentials.getProperty(CLIENT_ID))
                    .queryParam(CLIENT_SECRET, credentials.getProperty(CLIENT_SECRET))
                    .build();
            if (docStore.has(extension)) {
                client.setHeader("If-Modified-Since", httpStandardLastModified);
            }
            HtmlContentResponse response = client.getContentResponse(HtmlContentResponse.class);
            switch (response.getStatus()) {
                case 304:
                    LOG.info(String.format("[%s] %s", extension, "Content was not modified."));
                    continue;
                case 200:
                    String firstParagraph = response.getFirstParagraph();
                    if (firstParagraph == null) {
                        continue;
                    }
                    LOG.info(String.format("[%s] %s", extension, "Content was updated."));
                    docStore.add(extension, firstParagraph);
                    break;
                default:
                    LOG.warning(response.getError().toString(2));
                    break;
            }
        }
        docStore.getUpdater().commit().updateSource();
    }

    private boolean loadCredentials(Properties credentials) {
        URL credentialsUrl = DocRetriever.class.getClassLoader().getResource(CREDENTIALS_PROPERTIES);
        if (credentialsUrl == null) {
            return false;
        }
        Path credentialsPath;
        try {
            credentialsPath = Paths.get(credentialsUrl.toURI());
        } catch (URISyntaxException e) {
            return false;
        }

        try (FileInputStream stream = new FileInputStream(credentialsPath.toString())) {
            credentials.load(stream);
            if (credentials.getProperty(CLIENT_ID) == null || credentials.getProperty(CLIENT_SECRET) == null) {
                return false;
            }
        } catch (IOException e) {
            return false;
        }
        return true;
    }
}
