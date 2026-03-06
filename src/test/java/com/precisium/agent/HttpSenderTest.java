package com.precisium.agent;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.precisium.agent.controller.HttpSender;
import com.precisium.agent.service.Transport;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class HttpSenderTest {

    @Test
    void sendLine_shouldBuildPostRequestCorrectly() throws Exception {

        Transport client = mock(Transport.class);

        @SuppressWarnings("unchecked")
        HttpResponse<String> response =
                (HttpResponse<String>) mock(HttpResponse.class);

        when(response.statusCode()).thenReturn(202);
        when(client.send(any()))
                .thenReturn(response);

        HttpSender sender = new HttpSender(client);

        String endpoint = "http://localhost:8080";
        String line = "hello";

        sender.sendLine(endpoint, line);

        ArgumentCaptor<HttpRequest> captor =
                ArgumentCaptor.forClass(HttpRequest.class);

        verify(client).send(captor.capture());

        HttpRequest request = captor.getValue();

        assertEquals(URI.create(endpoint+"/logs"), request.uri());
        assertEquals("POST", request.method());
        assertEquals(
                "application/json",
                request.headers().firstValue("Content-Type").orElse("")
        );
    }

    @Test
    void sendLine_shouldThrowIOExceptionWhenStatusIsError() throws Exception {

        Transport client = mock(Transport.class);

        @SuppressWarnings("unchecked")
        HttpResponse<String> response =
                (HttpResponse<String>) mock(HttpResponse.class);

        when(response.statusCode()).thenReturn(500);
        when(response.body()).thenReturn("internal error");

        when(client.send(any()))
                .thenReturn(response);

        HttpSender sender = new HttpSender(client);

        IOException ex = assertThrows(
                IOException.class,
                () -> sender.sendLine("http://localhost:8080/logs", "line")
        );

        assertTrue(ex.getMessage().contains("HTTP 500"));
        assertTrue(ex.getMessage().contains("internal error"));
    }
}
