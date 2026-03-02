package com.precisium.agent.service;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.precisium.agent.utils.Transfer;

public class Transport implements Transfer {
    private final HttpClient client;

    public Transport() {

        this.client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();
    }

    @Override
    public HttpResponse<String> send(HttpRequest request)
            throws IOException, InterruptedException {
            System.out.println("Tansport");
            HttpResponse<String> res = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("res: "+ res.toString());
            return res;
    }
}
