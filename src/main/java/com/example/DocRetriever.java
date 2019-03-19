package com.example;

import com.example.githubclient.GithubContentsClient;
import com.example.githubclient.HtmlContentBody;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.logging.Logger;

public class DocRetriever {
    private static final Logger LOG = Logger.getLogger(DocRetriever.class.getName());

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

    public void init() throws Exception {
        if (docStore == null) {
            return;
        }
        Properties credentials = new Properties();
        Path credentialsPath = Paths.get(
                DocRetriever.class.getClassLoader().getResource("credentials.properties").toURI());
        credentials.load(new FileInputStream(credentialsPath.toString()));
        String clientId = credentials.getProperty("client_id");
        String clientSecret = credentials.getProperty("client_secret");

        for (String extension : extensions) {
            GithubContentsClient client = new GithubContentsClient.Builder(baseRepo, extension)
                    .isReadme(true)
                    .queryParam("client_id", clientId)
                    .queryParam("client_secret", clientSecret)
                    .build();
            String firstParagraph = client.getBody(HtmlContentBody.class).getFirstParagraph();
            docStore.add(extension, firstParagraph);
        }
        docStore.commit().updateSource();
    }

    public void fetch() throws IOException {
        if (docStore == null) {
            return;
        }
        String httpStandardLastModified = DateTimeFormatter.RFC_1123_DATE_TIME
                .withZone(ZoneOffset.UTC)
                .format(docStore.lastModified().toInstant());

        for  (String extension : extensions) {
            GithubContentsClient client = new GithubContentsClient.Builder(baseRepo, extension)
                    .isReadme(true)
                    .build();
            client.setHeader("If-Modified-Since", httpStandardLastModified);
            int status = client.getStatus();
        }
    }
}
