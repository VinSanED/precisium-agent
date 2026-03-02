package com.precisium.agent.controller;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.precisium.agent.service.Transport;

public final class HttpSender  {

    private final Transport client;

    public HttpSender(Transport client) {
        this.client = client;
    }

    public void sendLine(String baseUri, String line) throws IOException, InterruptedException {
        String payload = "{\"message\":\"" + escapeJson(line) + "\"}";
        String endpoint = baseUri+"logs";
        System.out.println("endPoint: "+endpoint);
        System.out.println("line: "+line);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();

        HttpResponse<String> response = client.send(request);
        System.out.println("res sender: "+response.toString());
        if (response.statusCode() >= 300) {
            throw new IOException("Falha ao enviar log. HTTP " + response.statusCode() + " - " + response.body());
        }
    }

    private String escapeJson(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
