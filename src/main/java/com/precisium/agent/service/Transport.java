package com.precisium.agent.service;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.precisium.agent.utils.Transfer;

public class Transport implements Transfer {
    private final HttpClient client;

    public Transport() {
        this.client = HttpClient.newHttpClient();
    }

    @Override
    public HttpResponse<String> send(HttpRequest request)
            throws IOException, InterruptedException {

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
