package com.precisium.agent.controller;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.precisium.agent.service.Transport;
import com.precisium.agent.utils.AgentStatus;

public final class ActivationClient {

    private final Transport client;
    private final String serverUrl;

    public ActivationClient(String serverUrl, Transport client) {
        this.client = client;
        this.serverUrl = serverUrl;
    }

    public AgentStatus fetchCommand(String agentId) throws IllegalStateException, IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(buildCommandUri(agentId))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request);
        
        if (response.statusCode() != 200) {
            throw new IllegalStateException("Erro HTTP: " + response.statusCode());
        }

        return AgentStatus.valueOf(response.body().trim());
    }

    private URI buildCommandUri(String agentId) {
        String normalizedBase = serverUrl.endsWith("/") ? serverUrl.substring(0, serverUrl.length() - 1) : serverUrl;
        return URI.create(normalizedBase + "/agents/" + agentId + "/command");
    }
}
