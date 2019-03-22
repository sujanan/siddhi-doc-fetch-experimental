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
import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;
import java.util.TreeSet;

public class DocStore {
    private static final String DOCSTORE_PROPERTIES = "docstore.properties";

    private final Path propertiesPath;
    private final Properties properties;
    private boolean hasUpdated = false;

    public interface CommitStep {
        SourceUpdateStep commit() throws IOException;
    }

    public interface SourceUpdateStep {
        boolean updateSource() throws IOException;
    }

    public DocStore() throws IOException, URISyntaxException {
        properties = new Properties() {

            @Override
            public synchronized Enumeration<Object> keys() {
                return Collections.enumeration(new TreeSet<>(super.keySet()));
            }
        };

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
        /* TODO: Remove the update hack with a memento. */
        hasUpdated = true;
    }

    public FileTime lastModified() throws IOException {
        return Files.getLastModifiedTime(propertiesPath);
    }

    public CommitStep getUpdater() {
        return (hasUpdated) ? new Updater(properties, propertiesPath) : new NullUpdater();
    }

    public static class Updater implements CommitStep, SourceUpdateStep {
        private final Properties properties;
        private final Path propertiesPath;

        private Updater(Properties properties, Path propertiesPath) {
            this.properties = properties;
            this.propertiesPath = propertiesPath;
        }

        @Override
        public SourceUpdateStep commit() throws IOException {
            FileOutputStream outputStream = new FileOutputStream(propertiesPath.toString());
            properties.store(outputStream, null);
            outputStream.close();
            return this;
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

    public static class NullUpdater implements CommitStep, SourceUpdateStep {

        @Override
        public SourceUpdateStep commit() throws IOException {
            return this;
        }

        @Override
        public boolean updateSource() throws IOException {
            return false;
        }
    }
}
