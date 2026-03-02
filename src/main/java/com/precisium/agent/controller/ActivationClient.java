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
        System.out.println("init fetch command: "+buildCommandUri(agentId));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(buildCommandUri(agentId))
                .GET()
                .build();

        System.out.println("req: "+ request.toString());
        HttpResponse<String> response = client.send(request);
        System.out.println(response);
        
        if (response.statusCode() != 200) {
            throw new IllegalStateException("Erro HTTP: " + response.statusCode());
        }

        return AgentStatus.valueOf(response.body().trim());
    }

    private URI buildCommandUri(String agentId) {
        System.out.println("build command uri");
        String normalizedBase = serverUrl.endsWith("/") ? serverUrl.substring(0, serverUrl.length() - 1) : serverUrl;
        System.out.println(normalizedBase+"/agents/"+agentId+"/command");
        return URI.create(normalizedBase + "/agents/" + agentId + "/command");
    }
}
