package com.precisium.agent.controller;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.precisium.agent.service.Transport;
import com.precisium.agent.utils.AgentStatus;

class ActivationClientFetchCommandTest {

    @Test
    void fetchCommand_shouldReturnStatusFromApiResponse() throws Exception {
        String defaultEndPoint = "http://localhost:3000/api";
        Transport tClient = mock(Transport.class);
        @SuppressWarnings("unchecked")
        HttpResponse<String> response = mock(HttpResponse.class);

        when(response.body()).thenReturn("STARTED");
        when(response.statusCode()).thenReturn(200);
        when(tClient.send(any(HttpRequest.class))).thenReturn(response);

        ActivationClient client = new ActivationClient(defaultEndPoint, tClient);

        AgentStatus status = client.fetchCommand("agent-123");

        assertEquals(AgentStatus.STARTED, status);
    }
}