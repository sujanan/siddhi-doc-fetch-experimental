package com.example;

public class Main {

    private static final String WSO2_EXTENSIONS_REPO = "wso2-extensions";

    private static final String[] WSO2_EXTENSIONS = {
            "siddhi-io-kafka",
            "siddhi-execution-string",
            "siddhi-store-rdbms",
            "siddhi-io-http",
            "siddhi-map-json",
            "siddhi-io-rabbitmq",
            "siddhi-map-xml",
            "siddhi-execution-streamingml",
            "siddhi-io-file",
            "siddhi-io-tcp",
            "siddhi-execution-math",
            "siddhi-map-text",
            "siddhi-io-email",
            "siddhi-execution-reorder"
    };

    public static void main(String[] args) throws Exception {
        /*
        for (String extension : WSO2_EXTENSIONS) {
            GithubContentsClient client = new GithubContentsClient.Builder(WSO2_EXTENSIONS_REPO, extension)
                    .isReadme(true)
                    .build();
            String firstParagraph = client.getBody(HtmlContentBody.class).getFirstParagraph();
        }
        */
        DocRetriever retriever = new DocRetriever(WSO2_EXTENSIONS_REPO, WSO2_EXTENSIONS);
        retriever.init();
    }
}
