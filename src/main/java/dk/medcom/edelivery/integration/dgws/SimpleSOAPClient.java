package dk.medcom.edelivery.integration.dgws;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

public class SimpleSOAPClient {

    public String sendRequest(String url, String action, String docXml, int timeout) {
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(timeout))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofMillis(timeout))
                .setHeader("SOAPAction", '"' + action + '"')
                .setHeader("Content-Type", "text/xml; encoding=utf-8")
                .POST(HttpRequest.BodyPublishers.ofString(docXml, StandardCharsets.UTF_8))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

            if (response.statusCode() != 200)
                throw new SOSIException("Got unexpected response " + response.statusCode() + " from " + url);

            return response.body();

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }
}
