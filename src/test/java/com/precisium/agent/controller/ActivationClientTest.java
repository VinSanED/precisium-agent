package com.precisium.agent.controller;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.precisium.agent.service.Transport;
import com.precisium.agent.utils.AgentStatus;

class ActivationClientTest {

    @Test
    void fetchCommand_shouldCallExpectedEndpointAndReturnAgentStatus() throws Exception {
        Transport tClient = mock(Transport.class);
        @SuppressWarnings("unchecked")
        HttpResponse<String> response = mock(HttpResponse.class);

        when(response.statusCode()).thenReturn(200);
        when(response.body()).thenReturn("STARTED");
        when(tClient.send(any(HttpRequest.class))).thenReturn(response);

        ActivationClient client = new ActivationClient("http://localhost:3000", tClient);

        AgentStatus status = client.fetchCommand("agent-123");

        assertEquals(AgentStatus.STARTED, status);

        ArgumentCaptor<HttpRequest> requestCaptor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(tClient).send(requestCaptor.capture());

        HttpRequest sentRequest = requestCaptor.getValue();
        assertEquals(URI.create("http://localhost:3000/agents/agent-123/command"), sentRequest.uri());
        assertEquals("GET", sentRequest.method());
    }

    @Test
    void fetchCommand_shouldThrowWhenStatusCodeIsNot200() throws Exception {
        Transport tClient = mock(Transport.class);
        @SuppressWarnings("unchecked")
        HttpResponse<String> response = mock(HttpResponse.class);

        when(response.statusCode()).thenReturn(503);
        when(tClient.send(any(HttpRequest.class))).thenReturn(response);

        ActivationClient client = new ActivationClient("http://localhost:3000", tClient);

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> client.fetchCommand("agent-123"));

        assertEquals("Erro HTTP: 503", ex.getMessage());
    }

    @Test
    void fetchCommand_shouldThrowWhenBodyHasInvalidStatusValue() throws Exception {
        Transport tClient = mock(Transport.class);
        @SuppressWarnings("unchecked")
        HttpResponse<String> response = mock(HttpResponse.class);

        when(response.statusCode()).thenReturn(100);
        when(response.body()).thenReturn("INVALID_STATUS");
        when(tClient.send(any(HttpRequest.class))).thenReturn(response);

        ActivationClient client = new ActivationClient("http://localhost:3000", tClient);

        assertThrows(IllegalStateException.class, () -> client.fetchCommand("1"));
    }
}
