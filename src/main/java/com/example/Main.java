package com.example;

public class Main {

    private static final String EXTENSIONS_REPO = "wso2-extensions";

    private static final String[] EXTENSIONS = {
            "siddhi-execution-string",
            "siddhi-execution-mat",
            "siddhi-execution-time",
            "siddhi-execution-streamingml",
            "siddhi-execution-regex",
            "siddhi-execution-markov",
            "siddhi-execution-unique",
            "siddhi-execution-map",
            "siddhi-execution-unitconversion",
            "siddhi-execution-extrema",
            "siddhi-execution-timeseries",
            "siddhi-execution-var",
            "siddhi-execution-priority",
            "siddhi-execution-reorder",
            "siddhi-execution-sentiment",
            "siddhi-execution-stats",
            "siddhi-execution-geo",
            "siddhi-execution-kalmanfilter",
            "siddhi-execution-approximate",
            "siddhi-execution-graph",
            "siddhi-execution-tensorflow",
            "siddhi-execution-env",
            "siddhi-io-http",
            "siddhi-io-kafka",
            "siddhi-io-tcp",
            "siddhi-io-wso2event",
            "siddhi-io-email",
            "siddhi-io-jms",
            "siddhi-io-file",
            "siddhi-io-rabbitmq",
            "siddhi-io-mqtt",
            "siddhi-io-websocket",
            "siddhi-io-sqs",
            "siddhi-io-twitter",
            "siddhi-io-cdc",
            "siddhi-io-prometheus",
            "siddhi-map-json",
            "siddhi-map-xml",
            "siddhi-map-binary",
            "siddhi-map-text",
            "siddhi-map-wso2event",
            "siddhi-map-keyvalue",
            "siddhi-map-csv",
            "siddhi-store-rdbms",
            "siddhi-store-solr",
            "siddhi-store-mongodb",
            "siddhi-store-hbase",
            "siddhi-store-redis",
            "siddhi-store-cassandra",
            "siddhi-script-js",
            "siddhi-script-scala",

            "siddhi-gpl-execution-pmml",
            "siddhi-gpl-execution-geo",
            "siddhi-gpl-execution-nlp",
            "siddhi-gpl-execution-r",
            "siddhi-gpl-execution-streamingml",
            "siddhi-gpl-script-r"
    };

    public static void main(String[] args) throws Exception {
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS %4$s %2$s %5$s%6$s%n");

        DocRetriever retriever = new DocRetriever(EXTENSIONS_REPO, EXTENSIONS);
        retriever.fetch();
    }
}
