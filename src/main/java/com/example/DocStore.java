package com.example;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.Properties;

public class DocStore {
    private static final String DOCSTORE_PROPERTIES = "docstore.properties";

    private final Path propertiesPath;

    private final Properties properties;

    public interface SourceUpdateStep {
        boolean updateSource() throws IOException;
    }

    public DocStore() throws IOException, URISyntaxException {
        properties = new Properties();

        URL propertiesUrl = DocStore.class.getClassLoader().getResource(DOCSTORE_PROPERTIES);
        if (propertiesUrl == null) {
            throw new IOException("Could not find the resource: " + DOCSTORE_PROPERTIES);
        }
        propertiesPath = Paths.get(propertiesUrl.toURI());

        FileInputStream inputStream = new FileInputStream(propertiesPath.toString());
        properties.load(inputStream);
        inputStream.close();
    }

    public boolean has(String extension) {
        return properties.getProperty(extension) != null;
    }

    public void add(String extension, String doc) {
        properties.setProperty(extension, doc);
    }

    public SourceUpdater commit() throws IOException {
        FileOutputStream outputStream = new FileOutputStream(propertiesPath.toString());
        properties.store(outputStream, null);
        outputStream.close();
        return new SourceUpdater(properties);
    }

    public FileTime lastModified() throws IOException {
        return Files.getLastModifiedTime(propertiesPath);
    }

    public static class SourceUpdater implements SourceUpdateStep {
        private final Properties properties;

        private SourceUpdater(Properties properties) {
            this.properties = properties;
        }

        @Override
        public boolean updateSource() throws IOException {
            Path srcPath = Paths.get("src", "main", "resources", DOCSTORE_PROPERTIES);
            if (!Files.exists(srcPath)) {
                return false;
            }
            FileOutputStream outputStream = new FileOutputStream(srcPath.toString());
            properties.store(outputStream, null);
            return true;
        }
    }
}
